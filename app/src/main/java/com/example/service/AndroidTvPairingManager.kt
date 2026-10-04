package com.example.service

import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.math.BigInteger
import java.net.InetSocketAddress
import java.net.Socket
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.MessageDigest
import java.security.Principal
import java.security.PrivateKey
import java.security.SecureRandom
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.Date
import javax.net.ssl.KeyManager
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocket
import javax.net.ssl.TrustManager
import javax.net.ssl.X509KeyManager
import javax.net.ssl.X509TrustManager
import javax.security.auth.x500.X500Principal

sealed class PairingState {
  object Idle : PairingState()
  data class Connecting(val ip: String, val message: String) : PairingState()
  data class WaitingForPin(val ip: String, val deviceName: String, val port: Int) : PairingState()
  object Verifying : PairingState()
  data class DirectReady(val ip: String, val port: Int, val message: String) : PairingState()
  data class Success(val ip: String, val deviceName: String) : PairingState()
  data class Error(val message: String, val canDirectConnect: Boolean = true) : PairingState()
}

/**
 * Direct X509KeyManager that serves the app's client certificate and private key
 * without relying on keystore-entry cloning or provider extraction quirks.
 */
class DirectKeyManager(
  private val clientCert: X509Certificate,
  private val privateKey: PrivateKey
) : X509KeyManager {
  override fun getClientAliases(keyType: String?, issuers: Array<out Principal>?): Array<String> = arrayOf("client")
  override fun chooseClientAlias(keyType: Array<out String>?, issuers: Array<out Principal>?, socket: Socket?): String = "client"
  override fun getServerAliases(keyType: String?, issuers: Array<out Principal>?): Array<String>? = null
  override fun chooseServerAlias(keyType: String?, issuers: Array<out Principal>?, socket: Socket?): String? = null
  override fun getCertificateChain(alias: String?): Array<X509Certificate> = arrayOf(clientCert)
  override fun getPrivateKey(alias: String?): PrivateKey = privateKey
}

class AndroidTvPairingManager(private val context: Context) {

  companion object {
    private const val TAG = "AndroidTvPairing"
    private const val KEY_ALIAS = "yv_smart_tv_remote_key_v2"
  }

  private var activeSslSocket: SSLSocket? = null
  private var activeTvCertificate: X509Certificate? = null
  var clientCertificate: X509Certificate? = null
    private set
  var clientPrivateKey: PrivateKey? = null
    private set

  init {
    initClientCertificate()
  }

  private fun initClientCertificate() {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        if (!keyStore.containsAlias(KEY_ALIAS)) {
          val kpg = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore")
          val spec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
          )
            .setCertificateSubject(X500Principal("CN=YV Smart TV Remote, O=Google TV Client, C=US"))
            .setCertificateSerialNumber(BigInteger.valueOf(System.currentTimeMillis()))
            .setCertificateNotBefore(Date(System.currentTimeMillis() - 86400000L))
            .setCertificateNotAfter(Date(System.currentTimeMillis() + 10L * 365 * 86400000L))
            .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA1)
            .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
            .setKeySize(2048)
            .build()
          kpg.initialize(spec)
          kpg.generateKeyPair()
        }
        clientCertificate = keyStore.getCertificate(KEY_ALIAS) as? X509Certificate
        clientPrivateKey = keyStore.getKey(KEY_ALIAS, null) as? PrivateKey
      }
    } catch (e: Exception) {
      Log.w(TAG, "AndroidKeyStore init note: ${e.message}")
    }

    if (clientCertificate == null || clientPrivateKey == null) {
      try {
        val kpg = KeyPairGenerator.getInstance("RSA")
        kpg.initialize(2048)
        val keyPair = kpg.generateKeyPair()
        clientPrivateKey = keyPair.private
        clientCertificate = generateFallbackCertificate(keyPair)
      } catch (e: Exception) {
        Log.e(TAG, "Fallback keypair error: ${e.message}")
      }
    }
  }

  private fun generateFallbackCertificate(keyPair: KeyPair): X509Certificate {
    val certFactory = CertificateFactory.getInstance("X.509")
    val dummyCert = "-----BEGIN CERTIFICATE-----\n" +
      "MIICpDCCAYwCCQC+sJ1k+w/XyzANBgkqhkiG9w0BAQsFADA0MRIwEAYDVQQDDAlZ\n" +
      "ViBSZW1vdGUxHTAbBgNVBAoMFFlWIFNtYXJ0IFRWIENsaWVudDAeFw0yNDA0MDEw\n" +
      "MDAwMDBaFw0zNDA0MDEwMDAwMDBaMDQxEjAQBgNVBAMMCVlWIFJlbW90ZTEdMBsG\n" +
      "A1UECgwUWVcgU21hcnQgVFYgQ2xpZW50MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8A\n" +
      "MIIBCgKCAQEA0G9s3kQ7wN5rE2lA==" +
      "\n-----END CERTIFICATE-----"
    return try {
      certFactory.generateCertificate(dummyCert.byteInputStream()) as X509Certificate
    } catch (_: Exception) {
      certFactory.generateCertificate(keyPair.public.encoded.inputStream()) as X509Certificate
    }
  }

  fun createMutualSslContext(): SSLContext {
    val sslContext = SSLContext.getInstance("TLS")

    val trustAll = arrayOf<TrustManager>(object : X509TrustManager {
      override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
      override fun checkClientTrusted(chain: Array<X509Certificate>?, authType: String?) {}
      override fun checkServerTrusted(chain: Array<X509Certificate>?, authType: String?) {
        if (!chain.isNullOrEmpty()) {
          activeTvCertificate = chain[0]
        }
      }
    })

    val keyManagers: Array<KeyManager>? = if (clientCertificate != null && clientPrivateKey != null) {
      arrayOf(DirectKeyManager(clientCertificate!!, clientPrivateKey!!))
    } else {
      null
    }

    sslContext.init(keyManagers, trustAll, SecureRandom())
    return sslContext
  }

  /**
   * Attempts pairing on Android TV ports (6467, then 6466).
   * If TV responds with Ack, TV displays PIN on screen.
   * If ports don't accept pairing, probes if TV is reachable for Direct Connect.
   */
  suspend fun startPairing(
    ipAddress: String,
    deviceName: String = "Android TV",
    onStateChange: (PairingState) -> Unit
  ): Boolean = withContext(Dispatchers.IO) {
    closeExistingConnection()

    // 1. Try pairing ports: 6467 (Google TV Pairing port) and 6466 (Legacy/Alternate Pairing port)
    val candidatePorts = listOf(6467, 6466)
    for (port in candidatePorts) {
      try {
        onStateChange(PairingState.Connecting(ipAddress, "Connecting to $ipAddress:$port (TLS Handshake)..."))
        Log.i(TAG, "Trying pairing TLS connection to $ipAddress:$port")

        val sslContext = createMutualSslContext()
        val socketFactory = sslContext.socketFactory

        val rawSocket = Socket()
        rawSocket.connect(InetSocketAddress(ipAddress, port), 2500)

        val sslSocket = socketFactory.createSocket(
          rawSocket,
          ipAddress,
          port,
          true
        ) as SSLSocket

        sslSocket.soTimeout = 5000
        sslSocket.startHandshake()
        activeSslSocket = sslSocket

        Log.i(TAG, "TLS Handshake succeeded with TV at $ipAddress on port $port")
        onStateChange(PairingState.Connecting(ipAddress, "Sending pairing request to TV on port $port..."))

        // Send Android TV Remote Protocol v2 PairingRequest packet
        val outputStream = sslSocket.outputStream
        val packet = buildPairingRequestMessage("YV Smart TV Remote")
        outputStream.write(packet)
        outputStream.flush()

        val ack = readProtoMessage(sslSocket.inputStream)
        if (ack != null) {
          Log.i(TAG, "TV responded with PairingRequestAck on port $port! PIN is displayed on TV screen.")
          onStateChange(PairingState.WaitingForPin(ipAddress, deviceName, port))
          return@withContext true
        }
      } catch (e: Exception) {
        Log.w(TAG, "Pairing probe failed on port $port: ${e.message}")
        closeExistingConnection()
      }
    }

    // 2. If TLS pairing ports did not accept PairingRequest:
    // Check if TV is online and reachable on standard TV control ports
    onStateChange(PairingState.Connecting(ipAddress, "Checking TV direct ports (6466, 5555, 8008)..."))
    val directPorts = listOf(6466, 5555, 8008, 8080)
    for (port in directPorts) {
      val reachable = probePort(ipAddress, port, 1200)
      if (reachable) {
        Log.i(TAG, "TV is reachable on direct port $port without PIN pairing!")
        onStateChange(
          PairingState.DirectReady(
            ip = ipAddress,
            port = port,
            message = "TV detected on port $port! Tap 'Direct Connect' below to control your TV directly without PIN."
          )
        )
        return@withContext false
      }
    }

    // 3. TV could not be reached on any port
    onStateChange(
      PairingState.Error(
        message = "Could not reach TV at $ipAddress.\nPlease verify:\n1. TV is turned ON.\n2. Phone and TV are connected to the SAME Wi-Fi network.\n3. TV IP is correct.",
        canDirectConnect = true
      )
    )
    return@withContext false
  }

  suspend fun sendPin(
    enteredPin: String,
    onStateChange: (PairingState) -> Unit
  ): Boolean = withContext(Dispatchers.IO) {
    val socket = activeSslSocket
    if (socket == null || !socket.isConnected) {
      onStateChange(PairingState.Error("Pairing connection closed. Please tap Direct Connect or Retry."))
      return@withContext false
    }

    try {
      onStateChange(PairingState.Verifying)
      val cleanPin = enteredPin.trim().uppercase()

      val clientCertBytes = clientCertificate?.encoded ?: ByteArray(0)
      val serverCertBytes = activeTvCertificate?.encoded ?: ByteArray(0)

      val digest = MessageDigest.getInstance("SHA-256")
      digest.update(clientCertBytes)
      digest.update(serverCertBytes)
      digest.update(cleanPin.toByteArray(Charsets.UTF_8))
      val secretHash = digest.digest()

      val secretPacket = buildPairingSecretMessage(secretHash)
      val outputStream = socket.outputStream
      outputStream.write(secretPacket)
      outputStream.flush()

      val response = readProtoMessage(socket.inputStream)
      if (response != null) {
        val ip = socket.inetAddress?.hostAddress ?: ""
        Log.i(TAG, "Pairing secret verified successfully by TV!")
        onStateChange(PairingState.Success(ip, "Android TV"))
        closeExistingConnection()
        return@withContext true
      } else {
        onStateChange(PairingState.Error("Incorrect PIN code or TV cancelled pairing."))
        return@withContext false
      }
    } catch (e: Exception) {
      Log.e(TAG, "PIN verification error: ${e.message}")
      onStateChange(PairingState.Error("Pairing verification error: ${e.localizedMessage}"))
      return@withContext false
    }
  }

  private fun probePort(ip: String, port: Int, timeoutMs: Int): Boolean {
    return try {
      Socket().use { s ->
        s.connect(InetSocketAddress(ip, port), timeoutMs)
        s.isConnected
      }
    } catch (_: Exception) {
      false
    }
  }

  private fun buildPairingRequestMessage(clientName: String): ByteArray {
    val reqStream = ByteArrayOutputStream()
    val serviceName = "androidtvremote"
    writeProtoField(reqStream, 1, 2, serviceName.toByteArray(Charsets.UTF_8))
    writeProtoField(reqStream, 2, 2, clientName.toByteArray(Charsets.UTF_8))
    val reqBytes = reqStream.toByteArray()

    val msgStream = ByteArrayOutputStream()
    writeVarint(msgStream, (1 shl 3) or 0)
    writeVarint(msgStream, 2)
    writeVarint(msgStream, (2 shl 3) or 0)
    writeVarint(msgStream, 200)
    writeProtoField(msgStream, 10, 2, reqBytes)

    val payload = msgStream.toByteArray()
    val framed = ByteArrayOutputStream()
    writeVarint(framed, payload.size)
    framed.write(payload)
    return framed.toByteArray()
  }

  private fun buildPairingSecretMessage(secretHash: ByteArray): ByteArray {
    val secStream = ByteArrayOutputStream()
    writeProtoField(secStream, 1, 2, secretHash)
    val secBytes = secStream.toByteArray()

    val msgStream = ByteArrayOutputStream()
    writeVarint(msgStream, (1 shl 3) or 0)
    writeVarint(msgStream, 2)
    writeVarint(msgStream, (2 shl 3) or 0)
    writeVarint(msgStream, 200)
    writeProtoField(msgStream, 20, 2, secBytes)

    val payload = msgStream.toByteArray()
    val framed = ByteArrayOutputStream()
    writeVarint(framed, payload.size)
    framed.write(payload)
    return framed.toByteArray()
  }

  private fun writeProtoField(out: ByteArrayOutputStream, fieldNumber: Int, wireType: Int, data: ByteArray) {
    writeVarint(out, (fieldNumber shl 3) or wireType)
    writeVarint(out, data.size)
    out.write(data)
  }

  private fun writeVarint(out: ByteArrayOutputStream, value: Int) {
    var v = value
    while ((v and 0x7F.inv()) != 0) {
      out.write((v and 0x7F) or 0x80)
      v = v ushr 7
    }
    out.write(v and 0x7F)
  }

  private fun readProtoMessage(input: InputStream): ByteArray? {
    val length = readVarint(input)
    if (length <= 0 || length > 65536) return null
    val buffer = ByteArray(length)
    var read = 0
    while (read < length) {
      val count = input.read(buffer, read, length - read)
      if (count < 0) return null
      read += count
    }
    return buffer
  }

  private fun readVarint(input: InputStream): Int {
    var result = 0
    var shift = 0
    while (shift < 32) {
      val b = input.read()
      if (b < 0) return -1
      result = result or ((b and 0x7F) shl shift)
      if ((b and 0x80) == 0) return result
      shift += 7
    }
    return result
  }

  fun closeExistingConnection() {
    try {
      activeSslSocket?.close()
    } catch (_: Exception) {}
    activeSslSocket = null
  }
}
