package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ConnectionStatus
import com.example.model.PredefinedTvApps
import com.example.model.RemoteMode
import com.example.model.TvDevice
import com.example.model.TvState
import com.example.ui.components.AppsGridController
import com.example.ui.components.DPadController
import com.example.ui.components.HardwareRockers
import com.example.ui.components.KeyboardController
import com.example.ui.components.ModeNavigationTabs
import com.example.ui.components.NumpadController
import com.example.ui.components.TouchpadController
import com.example.ui.components.TvCompanionBar
import com.example.ui.theme.RemoteAccentCyan
import com.example.ui.theme.RemoteAccentRed
import com.example.ui.theme.RemoteBorderColor
import com.example.ui.theme.RemoteCardDark
import com.example.ui.theme.RemoteChassisDark
import com.example.ui.theme.RemoteTextMuted
import com.example.ui.theme.RemoteTextPrimary
import com.example.ui.theme.TvRemoteTheme

/**
 * Reusable stateless remote container for visual previews and rendering without ViewModel.
 */
@Composable
fun StatelessRemotePreviewLayout(
  state: TvState,
  modifier: Modifier = Modifier,
  onModeSelected: (RemoteMode) -> Unit = {},
  onCommand: (String, String) -> Unit = { _, _ -> },
  onOpenGuide: () -> Unit = {}
) {
  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(
            Color(0xFF101520),
            Color(0xFF0C0F17),
            Color(0xFF090B10)
          )
        )
      ),
    contentAlignment = Alignment.TopCenter
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .widthIn(max = 520.dp)
        .padding(horizontal = 16.dp, vertical = 10.dp)
        .verticalScroll(rememberScrollState()),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // 1. Header: Branding, Guide Button, Power Button
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(32.dp)
              .clip(CircleShape)
              .background(Color(0xFF1A2333))
              .border(1.dp, RemoteBorderColor, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Tv,
              contentDescription = "Android TV",
              tint = RemoteAccentCyan,
              modifier = Modifier.size(18.dp)
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = "YV Smart Remote",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
              ),
              color = RemoteTextPrimary
            )
            Text(
              text = "Universal TV Controller",
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
              color = RemoteTextMuted
            )
          }
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Layout Guide Button
          IconButton(
            onClick = onOpenGuide,
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(RemoteCardDark)
              .border(1.dp, RemoteBorderColor, CircleShape)
          ) {
            Icon(
              imageVector = Icons.Default.Tune,
              contentDescription = "Button Guide",
              tint = RemoteAccentCyan,
              modifier = Modifier.size(20.dp)
            )
          }

          // TV Power Button
          Box(
            modifier = Modifier
              .size(42.dp)
              .shadow(
                elevation = if (state.isPowerOn) 8.dp else 0.dp,
                shape = CircleShape,
                ambientColor = RemoteAccentRed
              )
              .clip(CircleShape)
              .background(if (state.isPowerOn) Color(0xFFEF4444) else Color(0xFF26191D))
              .border(1.dp, if (state.isPowerOn) Color(0xFFF87171) else Color(0xFF4A252B), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.PowerSettingsNew,
              contentDescription = "Power",
              tint = if (state.isPowerOn) Color.White else Color(0xFFEF4444),
              modifier = Modifier.size(22.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 2. TV Companion Monitor Bar
      TvCompanionBar(
        state = state,
        onTvSelectorClick = {},
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(12.dp))

      // 3. Segmented Mode Switcher (Remote, Touchpad, Apps, Numpad, Keyboard)
      ModeNavigationTabs(
        selectedMode = state.activeMode,
        onModeSelected = onModeSelected,
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(14.dp))

      // 4. Central Controller Viewport
      AnimatedContent(
        targetState = state.activeMode,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "preview_mode_swap",
        modifier = Modifier.fillMaxWidth()
      ) { mode ->
        when (mode) {
          RemoteMode.DPAD -> {
            Box(
              modifier = Modifier.fillMaxWidth(),
              contentAlignment = Alignment.Center
            ) {
              DPadController(
                onDirectionClick = { onCommand("DPAD_$it", it) },
                onOkClick = { onCommand("DPAD_CENTER", "OK") }
              )
            }
          }
          RemoteMode.TOUCHPAD -> {
            TouchpadController(
              onDirectionSwipe = { onCommand("SWIPE_$it", it) },
              onTapClick = { onCommand("DPAD_CENTER", "OK") }
            )
          }
          RemoteMode.APPS -> {
            AppsGridController(
              onAppClick = { app -> onCommand("APP", app.name) }
            )
          }
          RemoteMode.NUMPAD -> {
            NumpadController(
              onNumberClick = { digit -> onCommand("NUM", "$digit") },
              onSpecialClick = { special -> onCommand("SPECIAL", special) }
            )
          }
          RemoteMode.KEYBOARD -> {
            KeyboardController(
              text = "Search Netflix 4K",
              onTextChange = {},
              onSubmit = {},
              onClear = {},
              onVoiceClick = {}
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 5. Hardware Rockers & Quick Launcher Row
      HardwareRockers(
        onCommand = onCommand,
        onAppLaunch = {},
        onVoiceClick = {},
        isListeningVoice = state.isListeningVoice,
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

// ----------------- COMPOSE PREVIEWS -----------------

@Preview(name = "1. Classic Remote (D-Pad Placement)", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun PreviewClassicDpadRemote() {
  TvRemoteTheme {
    StatelessRemotePreviewLayout(
      state = TvState(
        activeTv = TvDevice("tv1", "Living Room Android TV", "192.168.1.102", 6466),
        connectionStatus = ConnectionStatus.CONNECTED,
        isPowerOn = true,
        volume = 38,
        currentInput = "HDMI 1",
        activeMode = RemoteMode.DPAD,
        lastAction = "D-Pad Navigation"
      )
    )
  }
}

@Preview(name = "2. Touchpad Trackpad Mode", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun PreviewTouchpadMode() {
  TvRemoteTheme {
    StatelessRemotePreviewLayout(
      state = TvState(
        activeTv = TvDevice("tv1", "Sony BRAVIA 4K OLED", "192.168.1.145", 6466),
        connectionStatus = ConnectionStatus.CONNECTED,
        isPowerOn = true,
        volume = 45,
        currentInput = "HDMI 2",
        activeMode = RemoteMode.TOUCHPAD,
        lastAction = "Trackpad Active"
      )
    )
  }
}

@Preview(name = "3. TV Apps Launcher Grid", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun PreviewAppsGridMode() {
  TvRemoteTheme {
    StatelessRemotePreviewLayout(
      state = TvState(
        activeTv = TvDevice("tv1", "TCL 55\" Google TV", "192.168.1.118", 6466),
        connectionStatus = ConnectionStatus.CONNECTED,
        isPowerOn = true,
        currentRunningApp = PredefinedTvApps.defaultApps.first { it.id == "youtube" },
        activeMode = RemoteMode.APPS,
        lastAction = "Opened YouTube"
      )
    )
  }
}

@Preview(name = "4. Numpad Dialpad Mode", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun PreviewNumpadMode() {
  TvRemoteTheme {
    StatelessRemotePreviewLayout(
      state = TvState(
        activeTv = TvDevice("tv1", "Xiaomi Mi Box S", "192.168.1.189", 6466),
        connectionStatus = ConnectionStatus.CONNECTED,
        isPowerOn = true,
        currentChannel = 104,
        activeMode = RemoteMode.NUMPAD,
        lastAction = "Channel: 104"
      )
    )
  }
}

@Preview(name = "5. Live Keyboard Type Mode", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun PreviewKeyboardMode() {
  TvRemoteTheme {
    StatelessRemotePreviewLayout(
      state = TvState(
        activeTv = TvDevice("tv1", "Living Room Android TV", "192.168.1.102", 6466),
        connectionStatus = ConnectionStatus.CONNECTED,
        isPowerOn = true,
        activeMode = RemoteMode.KEYBOARD,
        keyboardText = "Stranger Things",
        lastAction = "Typing to TV"
      )
    )
  }
}
