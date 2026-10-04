package com.example.service

import android.content.Context
import android.util.Log
import com.example.model.TvDevice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.net.InetSocketAddress
import java.net.Socket
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocket
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import java.security.SecureRandom
import java.security.cert.X509Certificate

class AndroidTvRemoteClient(private val context: Context) {

  companion object {
    private const val TAG = "AndroidTvRemoteClient"
    const val PORT_CONTROL = 6466
    const val PORT_ADB = 5555

    // Android KeyEvents
    val KEYCODE_MAP = mapOf(
      "UP" to 19,
      "DOWN" to 20,
      "LEFT" to 21,
      "RIGHT" to 22,
      "OK" to 23,
      "BACK" to 4,
      "HOME" to 3,
      "MENU" to 82,
      "POWER" to 26,
      "VOL_UP" to 24,
      "VOL_DOWN" to 25,
      "MUTE" to 164,
      "CH_UP" to 166,
      "CH_DOWN" to 167,
      "PLAY_PAUSE" to 85,
      "REWIND" to 89,
      "FORWARD" to 90,
      "INPUT" to 178
    )
  }

  private var activeControlSocket: Socket? = null
  private var currentDeviceIp: String? = null

  /**
   * Sends a key command to the connected Android TV device.
   */
  suspend fun sendKey(
    device: TvDevice?,
    command: String
  ): Boolean = withContext(Dispatchers.IO) {
    if (device == null) return@withContext false

    val keycode = KEYCODE_MAP[command] ?: when {
      command.startsWith("NUM_") -> {
        val digit = command.removePrefix("NUM_").toIntOrNull() ?: 0
        7 + digit // KEYCODE_0 = 7, KEYCODE_9 = 16
      }
      else -> 23
    }

    // 1. Try sending via Android TV Remote Protocol v2 on Port 6466 (TLS)
    val sentV2 = trySendRemoteMessageV2(device.ipAddress, keycode)
    if (sentV2) return@withContext true

    // 2. Try raw TCP socket fallback or ADB on Port 5555 / 6466
    return@withContext trySendDirectTcp(device.ipAddress, device.port, command, keycode)
  }

  private fun trySendRemoteMessageV2(ip: String, keycode: Int): Boolean {
    return try {
      val socket = getOrCreateControlSocket(ip) ?: return false
      val packet = buildKeyMessageV2(keycode)
      socket.outputStream.write(packet)
      socket.outputStream.flush()
      true
    } catch (e: Exception) {
      Log.w(TAG, "Failed to send v2 key to $ip: ${e.message}")
      closeSocket()
      false
    }
  }

  private fun getOrCreateControlSocket(ip: String): Socket? {
    if (activeControlSocket != null && activeControlSocket!!.isConnected && currentDeviceIp == ip) {
      return activeControlSocket
    }

    return try {
      closeSocket()
      val sslContext = SSLContext.getInstance("TLS")
      val trustAll = arrayOf<TrustManager>(object : X509TrustManager {
        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        override fun checkClientTrusted(chain: Array<X509Certificate>?, authType: String?) {}
        override fun checkServerTrusted(chain: Array<X509Certificate>?, authType: String?) {}
      })
      sslContext.init(null, trustAll, SecureRandom())

      val rawSocket = Socket()
      rawSocket.connect(InetSocketAddress(ip, PORT_CONTROL), 1500)
      val sslSocket = sslContext.socketFactory.createSocket(rawSocket, ip, PORT_CONTROL, true) as SSLSocket
      sslSocket.soTimeout = 2000
      sslSocket.startHandshake()

      activeControlSocket = sslSocket
      currentDeviceIp = ip
      sslSocket
    } catch (e: Exception) {
      Log.w(TAG, "Cannot open TLS control socket to $ip:$PORT_CONTROL: ${e.message}")
      null
    }
  }

  private fun buildKeyMessageV2(keycode: Int): ByteArray {
    // Android TV Remote v2 RemoteKeyInject packet
    val msg = ByteArrayOutputStream()
    // RemoteKeyCode (field 1, varint)
    writeVarint(msg, (1 shl 3) or 0)
    writeVarint(msg, keycode)
    // Direction (field 2, varint: 3 = SHORT_PRESS)
    writeVarint(msg, (2 shl 3) or 0)
    writeVarint(msg, 3)

    val payload = msg.toByteArray()
    val frame = ByteArrayOutputStream()
    writeVarint(frame, payload.size)
    frame.write(payload)
    return frame.toByteArray()
  }

  private fun trySendDirectTcp(ip: String, port: Int, command: String, keycode: Int): Boolean {
    return try {
      Socket().use { s ->
        s.connect(InetSocketAddress(ip, port), 1000)
        s.outputStream.write("KEY:$command:$keycode\n".toByteArray(Charsets.UTF_8))
        s.outputStream.flush()
        true
      }
    } catch (_: Exception) {
      false
    }
  }

  private fun writeVarint(out: ByteArrayOutputStream, value: Int) {
    var v = value
    while ((v and 0x7F.inv()) != 0) {
      out.write((v and 0x7F) or 0x80)
      v = v ushr 7
    }
    out.write(v and 0x7F)
  }

  fun closeSocket() {
    try {
      activeControlSocket?.close()
    } catch (_: Exception) {}
    activeControlSocket = null
    currentDeviceIp = null
  }
}
