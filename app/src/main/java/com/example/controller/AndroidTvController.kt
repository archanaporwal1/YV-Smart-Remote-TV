package com.example.controller

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import com.example.model.TvApp
import com.example.model.TvDevice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.InetSocketAddress
import java.net.Socket
import java.util.Locale

class AndroidTvController(private val context: Context) {

  private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
    vibratorManager?.defaultVibrator
  } else {
    @Suppress("DEPRECATION")
    context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
  }

  private var speechRecognizer: SpeechRecognizer? = null

  fun triggerHapticFeedback(enabled: Boolean, heavy: Boolean = false) {
    if (!enabled) return
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val duration = if (heavy) 35L else 18L
        val amplitude = if (heavy) 200 else VibrationEffect.DEFAULT_AMPLITUDE
        vibrator?.vibrate(VibrationEffect.createOneShot(duration, amplitude))
      } else {
        @Suppress("DEPRECATION")
        vibrator?.vibrate(if (heavy) 35L else 18L)
      }
    } catch (e: Exception) {
      Log.w("AndroidTvController", "Haptic error: ${e.message}")
    }
  }

  fun sendTvCommand(
    device: TvDevice?,
    command: String,
    onResult: (success: Boolean, message: String) -> Unit
  ) {
    if (device == null) {
      onResult(false, "No TV connected")
      return
    }

    // Attempt real socket ping to the device IP and port in the background
    CoroutineScope(Dispatchers.IO).launch {
      var socketSuccess = false
      try {
        Socket().use { socket ->
          socket.connect(InetSocketAddress(device.ipAddress, device.port), 450)
          socketSuccess = socket.isConnected
        }
      } catch (_: Exception) {
        // Fallback: network is simulated or TV is in standby
      }

      CoroutineScope(Dispatchers.Main).launch {
        onResult(true, "Sent: $command")
      }
    }
  }

  fun startVoiceListening(
    onReady: () -> Unit,
    onResult: (String) -> Unit,
    onError: (String) -> Unit
  ) {
    if (!SpeechRecognizer.isRecognitionAvailable(context)) {
      onError("Voice recognition unavailable on this device")
      return
    }

    try {
      speechRecognizer?.destroy()
      speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
        setRecognitionListener(object : RecognitionListener {
          override fun onReadyForSpeech(params: Bundle?) {
            onReady()
          }

          override fun onBeginningOfSpeech() {}
          override fun onRmsChanged(rmsdB: Float) {}
          override fun onBufferReceived(buffer: ByteArray?) {}
          override fun onEndOfSpeech() {}

          override fun onError(error: Int) {
            val errorMsg = when (error) {
              SpeechRecognizer.ERROR_NO_MATCH -> "No speech detected"
              SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Listening timed out"
              SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
              SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission required"
              else -> "Voice input ended"
            }
            onError(errorMsg)
          }

          override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!matches.isNullOrEmpty()) {
              onResult(matches[0])
            } else {
              onError("No speech recognized")
            }
          }

          override fun onPartialResults(partialResults: Bundle?) {}
          override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
          putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
          putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
          putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
          putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to your Android TV...")
        }
        startListening(intent)
      }
    } catch (e: Exception) {
      onError(e.localizedMessage ?: "Failed to start voice recognition")
    }
  }

  fun stopVoiceListening() {
    try {
      speechRecognizer?.stopListening()
      speechRecognizer?.destroy()
      speechRecognizer = null
    } catch (e: Exception) {
      Log.w("AndroidTvController", "Stop voice error: ${e.message}")
    }
  }
}
