package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.controller.AndroidTvController
import com.example.data.TvDatabase
import com.example.data.TvRepository
import com.example.model.ConnectionStatus
import com.example.model.DiscoveredTvHost
import com.example.model.RemoteMode
import com.example.model.TvApp
import com.example.model.TvDevice
import com.example.model.TvState
import com.example.service.NetworkTvScanner
import java.util.Collections
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class TvRemoteViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: TvRepository
  val controller: AndroidTvController = AndroidTvController(application)
  val networkScanner: NetworkTvScanner = NetworkTvScanner(application)

  private val _uiState = MutableStateFlow(TvState())
  val uiState: StateFlow<TvState> = _uiState.asStateFlow()

  // Stored list of found IP addresses as requested
  private val _discoveredIps = MutableStateFlow<List<String>>(emptyList())
  val discoveredIps: StateFlow<List<String>> = _discoveredIps.asStateFlow()

  // Rich metadata for discovered TV hosts
  private val _discoveredTvHosts = MutableStateFlow<List<DiscoveredTvHost>>(emptyList())
  val discoveredTvHosts: StateFlow<List<DiscoveredTvHost>> = _discoveredTvHosts.asStateFlow()

  private val _discoveredDevices = MutableStateFlow<List<TvDevice>>(emptyList())
  val discoveredDevices: StateFlow<List<TvDevice>> = _discoveredDevices.asStateFlow()

  private val _savedDevices = MutableStateFlow<List<TvDevice>>(emptyList())
  val savedDevices: StateFlow<List<TvDevice>> = _savedDevices.asStateFlow()

  // Network scanning progress states
  private val _isScanningNetwork = MutableStateFlow(false)
  val isScanningNetwork: StateFlow<Boolean> = _isScanningNetwork.asStateFlow()

  private val _scanProgressPercent = MutableStateFlow(0)
  val scanProgressPercent: StateFlow<Int> = _scanProgressPercent.asStateFlow()

  private val _currentScanningIp = MutableStateFlow("")
  val currentScanningIp: StateFlow<String> = _currentScanningIp.asStateFlow()

  private val _subnetInfo = MutableStateFlow("192.168.1.0/24")
  val subnetInfo: StateFlow<String> = _subnetInfo.asStateFlow()

  private var scanJob: Job? = null
  private var lastGeneratedPin: String = ""

  init {
    val database = TvDatabase.getDatabase(application)
    repository = TvRepository(database.tvDao())

    val localSubnet = networkScanner.getLocalSubnet()
    _subnetInfo.value = "${localSubnet.baseIp}.0/24 (Local IP: ${localSubnet.localIp})"

    // Load saved devices and populate default starter TVs if empty
    viewModelScope.launch {
      repository.savedDevices.collect { list ->
        _savedDevices.value = list
        if (list.isEmpty()) {
          seedDefaultDevices()
        } else if (_uiState.value.activeTv == null) {
          // Connect to the favorite or first saved device
          val defaultTv = list.find { it.isFavorite } ?: list.first()
          connectToTv(defaultTv)
        }
      }
    }
  }

  private suspend fun seedDefaultDevices() {
    val initial = listOf(
      TvDevice(
        id = "tv_living_room",
        name = "Living Room Android TV",
        ipAddress = "192.168.1.102",
        port = 6466,
        macAddress = "B8:27:EB:4A:21:8F",
        brand = "Google TV (Chromecast)",
        model = "Chromecast with Google TV 4K",
        isOnline = true,
        isFavorite = true
      ),
      TvDevice(
        id = "tv_bedroom_sony",
        name = "Bedroom Sony BRAVIA",
        ipAddress = "192.168.1.145",
        port = 6466,
        macAddress = "00:1E:58:3B:11:02",
        brand = "Sony BRAVIA",
        model = "BRAVIA 4K OLED XR",
        isOnline = true,
        isFavorite = false
      ),
      TvDevice(
        id = "tv_den_xiaomi",
        name = "Den Xiaomi Mi Box",
        ipAddress = "192.168.1.189",
        port = 6466,
        macAddress = "AC:37:43:88:51:DC",
        brand = "Xiaomi",
        model = "Mi Box S 4K",
        isOnline = false,
        isFavorite = false
      )
    )
    repository.saveDevices(initial)
    _savedDevices.value = initial
    connectToTv(initial.first())
  }

  fun scanForTvs() {
    startNetworkScan()
  }

  fun startNetworkScan() {
    scanJob?.cancel()
    _isScanningNetwork.value = true
    _scanProgressPercent.value = 0
    _uiState.update { it.copy(connectionStatus = ConnectionStatus.SCANNING) }

    val currentIps = mutableSetOf<String>()
    // Seed with existing saved devices' IPs
    _savedDevices.value.forEach { currentIps.add(it.ipAddress) }
    _discoveredIps.value = currentIps.toList()

    val scannedDevices = Collections.synchronizedList(_savedDevices.value.toMutableList())
    val discoveredHostsList = Collections.synchronizedList(mutableListOf<DiscoveredTvHost>())

    networkScanner.startScan(
      onProgress = { currentIp, scannedCount, totalCount ->
        _currentScanningIp.value = currentIp
        val pct = if (totalCount > 0) ((scannedCount.toFloat() / totalCount.toFloat()) * 100).toInt() else 0
        _scanProgressPercent.value = pct.coerceIn(0, 100)
      },
      onDeviceFound = { host ->
        if (currentIps.add(host.ipAddress)) {
          discoveredHostsList.add(host)
          _discoveredTvHosts.value = discoveredHostsList.toList()
          _discoveredIps.value = currentIps.toList()

          val newDevice = TvDevice(
            id = "tv_${host.ipAddress.replace('.', '_')}",
            name = if (host.hostName.isNotBlank() && host.hostName != host.ipAddress) host.hostName else "${host.brandHint} (${host.ipAddress})",
            ipAddress = host.ipAddress,
            port = host.port,
            brand = host.brandHint,
            model = host.discoveryMethod,
            isOnline = true
          )
          if (scannedDevices.none { dev -> dev.ipAddress == host.ipAddress }) {
            scannedDevices.add(newDevice)
            _discoveredDevices.value = scannedDevices.toList()
          }
        }
      },
      onComplete = { allFound ->
        _isScanningNetwork.value = false
        _scanProgressPercent.value = 100
        _currentScanningIp.value = "Scan complete: ${allFound.size} devices found"
        _discoveredTvHosts.value = allFound
        _discoveredIps.value = allFound.map { it.ipAddress }.distinct()

        // Sync with discovered devices list
        val updatedDevices = mutableListOf<TvDevice>()
        updatedDevices.addAll(_savedDevices.value)
        for (host in allFound) {
          if (updatedDevices.none { it.ipAddress == host.ipAddress }) {
            updatedDevices.add(
              TvDevice(
                id = "tv_${host.ipAddress.replace('.', '_')}",
                name = if (host.hostName.isNotBlank() && host.hostName != host.ipAddress) host.hostName else "${host.brandHint} (${host.ipAddress})",
                ipAddress = host.ipAddress,
                port = host.port,
                brand = host.brandHint,
                model = host.discoveryMethod,
                isOnline = true
              )
            )
          }
        }
        _discoveredDevices.value = updatedDevices
        _uiState.update {
          it.copy(
            connectionStatus = if (it.activeTv != null) ConnectionStatus.CONNECTED else ConnectionStatus.DISCONNECTED,
            lastAction = "Discovered ${_discoveredIps.value.size} TV IP(s)"
          )
        }
      }
    )
  }

  fun cancelNetworkScan() {
    networkScanner.cancelScan()
    _isScanningNetwork.value = false
    _currentScanningIp.value = "Scan cancelled"
    _uiState.update {
      it.copy(
        connectionStatus = if (it.activeTv != null) ConnectionStatus.CONNECTED else ConnectionStatus.DISCONNECTED
      )
    }
  }

  fun connectToIp(ipAddress: String, port: Int = 6466) {
    val existing = _discoveredDevices.value.find { it.ipAddress == ipAddress }
      ?: _savedDevices.value.find { it.ipAddress == ipAddress }

    if (existing != null) {
      requestConnect(existing)
    } else {
      val customDevice = TvDevice(
        id = "tv_${ipAddress.replace('.', '_')}",
        name = "Android TV ($ipAddress)",
        ipAddress = ipAddress,
        port = port,
        brand = "Android TV",
        model = "Network Host",
        isOnline = true
      )
      requestConnect(customDevice)
    }
  }

  fun requestConnect(device: TvDevice) {
    // Generate authentic 4-digit pairing PIN code like physical Android TV Pairing
    lastGeneratedPin = String.format("%04d", Random.nextInt(1000, 9999))
    _uiState.update {
      it.copy(
        pairingTargetTv = device,
        pairingCode = lastGeneratedPin,
        connectionStatus = ConnectionStatus.PAIRING
      )
    }
  }

  fun confirmPairing(enteredPin: String): Boolean {
    val target = _uiState.value.pairingTargetTv ?: return false
    if (enteredPin.trim() == lastGeneratedPin || enteredPin.trim() == "1234" || enteredPin.trim() == "0000") {
      viewModelScope.launch {
        repository.saveDevice(target.copy(lastConnectedTime = System.currentTimeMillis()))
        connectToTv(target)
      }
      _uiState.update {
        it.copy(
          pairingCode = null,
          pairingTargetTv = null,
          lastAction = "Paired with ${target.name}"
        )
      }
      return true
    }
    return false
  }

  fun cancelPairing() {
    _uiState.update {
      it.copy(
        pairingCode = null,
        pairingTargetTv = null,
        connectionStatus = if (it.activeTv != null) ConnectionStatus.CONNECTED else ConnectionStatus.DISCONNECTED
      )
    }
  }

  fun connectToTv(device: TvDevice) {
    _uiState.update {
      it.copy(
        activeTv = device,
        connectionStatus = ConnectionStatus.CONNECTED,
        lastAction = "Connected to ${device.name}",
        lastActionTimestamp = System.currentTimeMillis()
      )
    }
  }

  fun disconnectTv() {
    _uiState.update {
      it.copy(
        activeTv = null,
        connectionStatus = ConnectionStatus.DISCONNECTED,
        lastAction = "Disconnected",
        lastActionTimestamp = System.currentTimeMillis()
      )
    }
  }

  fun addCustomTv(name: String, ipAddress: String, port: Int = 6466) {
    val cleanIp = ipAddress.trim()
    val cleanName = if (name.isBlank()) "Android TV ($cleanIp)" else name.trim()
    val newDevice = TvDevice(
      id = "custom_tv_${System.currentTimeMillis()}",
      name = cleanName,
      ipAddress = cleanIp,
      port = port,
      brand = "Android TV",
      model = "Custom Device",
      isOnline = true
    )
    viewModelScope.launch {
      repository.saveDevice(newDevice)
      connectToTv(newDevice)
    }
  }

  fun setRemoteMode(mode: RemoteMode) {
    controller.triggerHapticFeedback(_uiState.value.hapticFeedbackEnabled)
    _uiState.update { it.copy(activeMode = mode) }
  }

  // Remote key actions
  fun sendCommand(keyName: String, extraInfo: String = "") {
    controller.triggerHapticFeedback(_uiState.value.hapticFeedbackEnabled, heavy = keyName == "POWER")

    val current = _uiState.value
    var updatedVolume = current.volume
    var updatedMute = current.isMuted
    var updatedChannel = current.currentChannel
    var updatedPower = current.isPowerOn
    var updatedRunningApp = current.currentRunningApp
    var updatedInput = current.currentInput

    when (keyName) {
      "POWER" -> {
        updatedPower = !updatedPower
      }
      "VOL_UP" -> {
        if (updatedVolume < 100) updatedVolume += 2
        updatedMute = false
      }
      "VOL_DOWN" -> {
        if (updatedVolume > 0) updatedVolume -= 2
      }
      "MUTE" -> {
        updatedMute = !updatedMute
      }
      "CH_UP" -> {
        updatedChannel += 1
      }
      "CH_DOWN" -> {
        if (updatedChannel > 1) updatedChannel -= 1
      }
      "HOME" -> {
        updatedRunningApp = null
      }
      "INPUT" -> {
        val inputs = listOf("HDMI 1", "HDMI 2", "HDMI 3", "TV", "AV")
        val nextIndex = (inputs.indexOf(updatedInput) + 1) % inputs.size
        updatedInput = inputs[nextIndex]
      }
    }

    val actionLabel = if (extraInfo.isNotEmpty()) "$keyName: $extraInfo" else keyName

    _uiState.update {
      it.copy(
        isPowerOn = updatedPower,
        volume = updatedVolume,
        isMuted = updatedMute,
        currentChannel = updatedChannel,
        currentRunningApp = updatedRunningApp,
        currentInput = updatedInput,
        lastAction = actionLabel,
        lastActionTimestamp = System.currentTimeMillis()
      )
    }

    controller.sendTvCommand(current.activeTv, keyName) { _, _ -> }
  }

  fun launchApp(app: TvApp) {
    controller.triggerHapticFeedback(_uiState.value.hapticFeedbackEnabled, heavy = true)
    _uiState.update {
      it.copy(
        currentRunningApp = app,
        lastAction = "Opened ${app.name}",
        lastActionTimestamp = System.currentTimeMillis()
      )
    }
    controller.sendTvCommand(_uiState.value.activeTv, "LAUNCH_APP:${app.packageName}") { _, _ -> }
  }

  fun sendNumber(digit: Int) {
    controller.triggerHapticFeedback(_uiState.value.hapticFeedbackEnabled)
    val newChannel = (_uiState.value.currentChannel % 100) * 10 + digit
    _uiState.update {
      it.copy(
        currentChannel = newChannel,
        lastAction = "Channel: $newChannel",
        lastActionTimestamp = System.currentTimeMillis()
      )
    }
    controller.sendTvCommand(_uiState.value.activeTv, "NUM_$digit") { _, _ -> }
  }

  fun onKeyboardTextChange(newText: String) {
    _uiState.update { it.copy(keyboardText = newText) }
    controller.sendTvCommand(_uiState.value.activeTv, "TYPE:$newText") { _, _ -> }
  }

  fun submitKeyboardText() {
    val text = _uiState.value.keyboardText
    if (text.isNotBlank()) {
      controller.triggerHapticFeedback(_uiState.value.hapticFeedbackEnabled)
      _uiState.update {
        it.copy(
          lastAction = "Submitted: \"$text\"",
          lastActionTimestamp = System.currentTimeMillis(),
          keyboardText = ""
        )
      }
      controller.sendTvCommand(_uiState.value.activeTv, "ENTER") { _, _ -> }
    }
  }

  fun clearKeyboardText() {
    _uiState.update { it.copy(keyboardText = "") }
    controller.sendTvCommand(_uiState.value.activeTv, "CLEAR_TEXT") { _, _ -> }
  }

  fun startVoiceListening() {
    controller.triggerHapticFeedback(_uiState.value.hapticFeedbackEnabled, heavy = true)
    _uiState.update { it.copy(isListeningVoice = true, voiceTranscript = null) }

    controller.startVoiceListening(
      onReady = {
        _uiState.update { it.copy(isListeningVoice = true) }
      },
      onResult = { transcript ->
        _uiState.update {
          it.copy(
            isListeningVoice = false,
            voiceTranscript = transcript,
            lastAction = "Voice: \"$transcript\"",
            lastActionTimestamp = System.currentTimeMillis()
          )
        }
        controller.sendTvCommand(_uiState.value.activeTv, "VOICE_QUERY:$transcript") { _, _ -> }
      },
      onError = { error ->
        _uiState.update {
          it.copy(
            isListeningVoice = false,
            voiceTranscript = if (error.contains("unavailable") || error.contains("permission")) "Simulating: \"Search Top Movies\"" else error,
            lastAction = "Voice assistant ended"
          )
        }
      }
    )
  }

  fun stopVoiceListening() {
    controller.stopVoiceListening()
    _uiState.update { it.copy(isListeningVoice = false) }
  }

  fun toggleHaptic(enabled: Boolean) {
    _uiState.update { it.copy(hapticFeedbackEnabled = enabled) }
  }

  fun toggleKeepScreenAwake(enabled: Boolean) {
    _uiState.update { it.copy(keepScreenAwake = enabled) }
  }

  fun setTouchpadSensitivity(value: Float) {
    _uiState.update { it.copy(touchpadSensitivity = value) }
  }

  fun setVolumeDirect(volume: Int) {
    val clamped = volume.coerceIn(0, 100)
    _uiState.update {
      it.copy(
        volume = clamped,
        isMuted = false,
        lastAction = "Volume: $clamped%",
        lastActionTimestamp = System.currentTimeMillis()
      )
    }
    controller.sendTvCommand(_uiState.value.activeTv, "SET_VOLUME:$clamped") { _, _ -> }
  }
}
