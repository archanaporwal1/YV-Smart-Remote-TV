package com.example.model

enum class ConnectionStatus {
  DISCONNECTED,
  SCANNING,
  PAIRING,
  CONNECTED,
  ERROR
}

enum class RemoteMode {
  DPAD,
  TOUCHPAD,
  APPS,
  NUMPAD,
  KEYBOARD
}

data class TvState(
  val activeTv: TvDevice? = null,
  val connectionStatus: ConnectionStatus = ConnectionStatus.DISCONNECTED,
  val isPowerOn: Boolean = true,
  val volume: Int = 35,
  val isMuted: Boolean = false,
  val currentChannel: Int = 104,
  val currentInput: String = "HDMI 1",
  val currentRunningApp: TvApp? = null,
  val lastAction: String = "Ready",
  val lastActionTimestamp: Long = System.currentTimeMillis(),
  val activeMode: RemoteMode = RemoteMode.DPAD,
  val isTouchpadPressed: Boolean = false,
  val keyboardText: String = "",
  val isListeningVoice: Boolean = false,
  val voiceTranscript: String? = null,
  val pairingCode: String? = null,
  val pairingTargetTv: TvDevice? = null,
  val hapticFeedbackEnabled: Boolean = true,
  val soundFeedbackEnabled: Boolean = true,
  val keepScreenAwake: Boolean = true,
  val touchpadSensitivity: Float = 1.0f
)
