package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.model.ConnectionStatus
import com.example.model.RemoteMode
import com.example.ui.components.AppsGridController
import com.example.ui.components.DPadController
import com.example.ui.components.HardwareRockers
import com.example.ui.components.KeyboardController
import com.example.ui.components.ModeNavigationTabs
import com.example.ui.components.NumpadController
import com.example.ui.components.TouchpadController
import com.example.ui.components.TvCompanionBar
import com.example.ui.dialogs.ButtonPlacementGuideDialog
import com.example.ui.dialogs.PairingPinDialog
import com.example.ui.dialogs.SettingsDialog
import com.example.ui.dialogs.TvDiscoveryDialog
import com.example.ui.theme.RemoteAccentCyan
import com.example.ui.theme.RemoteAccentGreen
import com.example.ui.theme.RemoteAccentRed
import com.example.ui.theme.RemoteBorderColor
import com.example.ui.theme.RemoteCardDark
import com.example.ui.theme.RemoteChassisDark
import com.example.ui.theme.RemoteTextMuted
import com.example.ui.theme.RemoteTextPrimary
import com.example.viewmodel.TvRemoteViewModel

@Composable
fun MainRemoteScreen(
  viewModel: TvRemoteViewModel,
  modifier: Modifier = Modifier
) {
  val uiState by viewModel.uiState.collectAsState()
  val discoveredDevices by viewModel.discoveredDevices.collectAsState()
  val discoveredIps by viewModel.discoveredIps.collectAsState()
  val discoveredHosts by viewModel.discoveredTvHosts.collectAsState()
  val isScanning by viewModel.isScanningNetwork.collectAsState()
  val scanProgress by viewModel.scanProgressPercent.collectAsState()
  val currentScanningIp by viewModel.currentScanningIp.collectAsState()
  val subnetInfo by viewModel.subnetInfo.collectAsState()
  val context = LocalContext.current
  val currentView = LocalView.current

  // Dialog visibility states
  var showDiscoveryDialog by remember { mutableStateOf(false) }
  var showSettingsDialog by remember { mutableStateOf(false) }
  var showGuideDialog by remember { mutableStateOf(false) }
  val snackbarHostState = remember { SnackbarHostState() }

  // Keep screen awake handler
  DisposableEffect(uiState.keepScreenAwake) {
    currentView.keepScreenOn = uiState.keepScreenAwake
    onDispose {
      currentView.keepScreenOn = false
    }
  }

  // Voice recording runtime permission launcher
  val voicePermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    if (isGranted) {
      viewModel.startVoiceListening()
    }
  }

  val onVoiceClickAction = {
    val hasPermission = ContextCompat.checkSelfPermission(
      context,
      Manifest.permission.RECORD_AUDIO
    ) == PackageManager.PERMISSION_GRANTED

    if (hasPermission) {
      if (uiState.isListeningVoice) {
        viewModel.stopVoiceListening()
      } else {
        viewModel.startVoiceListening()
      }
    } else {
      voicePermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }
  }

  // Show Pairing Dialog if needed
  if (uiState.connectionStatus == ConnectionStatus.PAIRING && uiState.pairingTargetTv != null) {
    PairingPinDialog(
      targetTv = uiState.pairingTargetTv,
      pairingCode = uiState.pairingCode,
      onConfirmPin = { pin -> viewModel.confirmPairing(pin) },
      onCancel = { viewModel.cancelPairing() }
    )
  }

  // Show Discovery Dialog
  if (showDiscoveryDialog) {
    TvDiscoveryDialog(
      tvState = uiState,
      discoveredDevices = discoveredDevices,
      discoveredIps = discoveredIps,
      discoveredHosts = discoveredHosts,
      isScanning = isScanning,
      scanProgress = scanProgress,
      currentScanningIp = currentScanningIp,
      subnetInfo = subnetInfo,
      onConnect = { device ->
        showDiscoveryDialog = false
        viewModel.requestConnect(device)
      },
      onConnectIp = { ip ->
        showDiscoveryDialog = false
        viewModel.connectToIp(ip)
      },
      onDisconnect = {
        viewModel.disconnectTv()
        showDiscoveryDialog = false
      },
      onScan = { viewModel.startNetworkScan() },
      onCancelScan = { viewModel.cancelNetworkScan() },
      onAddManualIp = { name, ip, port ->
        viewModel.addCustomTv(name, ip, port)
        showDiscoveryDialog = false
      },
      onDismiss = { showDiscoveryDialog = false }
    )
  }

  // Show Settings Dialog
  if (showSettingsDialog) {
    SettingsDialog(
      hapticEnabled = uiState.hapticFeedbackEnabled,
      keepScreenOn = uiState.keepScreenAwake,
      touchpadSensitivity = uiState.touchpadSensitivity,
      onToggleHaptic = { viewModel.toggleHaptic(it) },
      onToggleKeepScreenOn = { viewModel.toggleKeepScreenAwake(it) },
      onSensitivityChange = { viewModel.setTouchpadSensitivity(it) },
      onDismiss = { showSettingsDialog = false }
    )
  }

  // Show Button Placement Guide Dialog
  if (showGuideDialog) {
    ButtonPlacementGuideDialog(
      onDismiss = { showGuideDialog = false }
    )
  }

  Scaffold(
    snackbarHost = { SnackbarHost(snackbarHostState) },
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
    containerColor = RemoteChassisDark,
    modifier = modifier.fillMaxSize()
  ) { paddingValues ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          Brush.verticalGradient(
            colors = listOf(
              Color(0xFF101520),
              Color(0xFF0C0F17),
              Color(0xFF090B10)
            )
          )
        )
        .padding(paddingValues)
        .statusBarsPadding()
        .navigationBarsPadding(),
      contentAlignment = Alignment.TopCenter
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .widthIn(max = 520.dp)
          .padding(horizontal = 16.dp, vertical = 8.dp)
          .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // App Top Header (Brand, IR transmitter indicator, Power & Settings)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Brand & IR Indicator
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
                contentDescription = "Android TV Remote",
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
                  fontSize = 16.sp,
                  letterSpacing = 0.5.sp
                ),
                color = RemoteTextPrimary
              )
              Text(
                text = "Android TV & Google TV",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 10.sp
                ),
                color = RemoteTextMuted
              )
            }
          }

          // Top Right: Power Button & Settings
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // Button Placement & UI Layout Guide
            IconButton(
              onClick = { showGuideDialog = true },
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(RemoteCardDark)
                .border(1.dp, RemoteBorderColor, CircleShape)
                .testTag("button_layout_guide")
            ) {
              Icon(
                imageVector = Icons.Default.Tune,
                contentDescription = "Button Guide",
                tint = RemoteAccentCyan,
                modifier = Modifier.size(20.dp)
              )
            }

            // Settings Button
            IconButton(
              onClick = { showSettingsDialog = true },
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(RemoteCardDark)
                .border(1.dp, RemoteBorderColor, CircleShape)
                .testTag("button_settings")
            ) {
              Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = RemoteTextPrimary,
                modifier = Modifier.size(20.dp)
              )
            }

            // TV Power Button (Red backlight when TV is on)
            Box(
              modifier = Modifier
                .size(42.dp)
                .shadow(
                  elevation = if (uiState.isPowerOn) 8.dp else 0.dp,
                  shape = CircleShape,
                  ambientColor = RemoteAccentRed
                )
                .clip(CircleShape)
                .background(
                  if (uiState.isPowerOn) Color(0xFFEF4444) else Color(0xFF26191D)
                )
                .border(
                  width = 1.dp,
                  color = if (uiState.isPowerOn) Color(0xFFF87171) else Color(0xFF4A252B),
                  shape = CircleShape
                )
                .clickable { viewModel.sendCommand("POWER", "Toggle Power") }
                .testTag("button_power"),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.PowerSettingsNew,
                contentDescription = "Power",
                tint = if (uiState.isPowerOn) Color.White else Color(0xFFEF4444),
                modifier = Modifier.size(22.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // TV Companion Monitor (Active Device, Volume, Status)
        TvCompanionBar(
          state = uiState,
          onTvSelectorClick = { showDiscoveryDialog = true },
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Segmented Mode Tabs (Remote, Touchpad, Apps, Numpad, Keyboard)
        ModeNavigationTabs(
          selectedMode = uiState.activeMode,
          onModeSelected = { viewModel.setRemoteMode(it) },
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Center Controller (Smooth Animated Content Swap)
        AnimatedContent(
          targetState = uiState.activeMode,
          transitionSpec = { fadeIn() togetherWith fadeOut() },
          label = "mode_swap",
          modifier = Modifier.fillMaxWidth()
        ) { mode ->
          when (mode) {
            RemoteMode.DPAD -> {
              Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
              ) {
                DPadController(
                  onDirectionClick = { dir -> viewModel.sendCommand("DPAD_$dir", dir) },
                  onOkClick = { viewModel.sendCommand("DPAD_CENTER", "OK") }
                )
              }
            }
            RemoteMode.TOUCHPAD -> {
              TouchpadController(
                onDirectionSwipe = { dir -> viewModel.sendCommand("SWIPE_$dir", dir) },
                onTapClick = { viewModel.sendCommand("DPAD_CENTER", "OK") }
              )
            }
            RemoteMode.APPS -> {
              AppsGridController(
                onAppClick = { app -> viewModel.launchApp(app) }
              )
            }
            RemoteMode.NUMPAD -> {
              NumpadController(
                onNumberClick = { digit -> viewModel.sendNumber(digit) },
                onSpecialClick = { special -> viewModel.sendCommand("SPECIAL_$special", special) }
              )
            }
            RemoteMode.KEYBOARD -> {
              KeyboardController(
                text = uiState.keyboardText,
                onTextChange = { viewModel.onKeyboardTextChange(it) },
                onSubmit = { viewModel.submitKeyboardText() },
                onClear = { viewModel.clearKeyboardText() },
                onVoiceClick = onVoiceClickAction
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Hardware Controls (Volume & Channel Rockers, Voice, Navigation, Branded Shortcuts)
        HardwareRockers(
          onCommand = { cmd, info -> viewModel.sendCommand(cmd, info) },
          onAppLaunch = { app -> viewModel.launchApp(app) },
          onVoiceClick = onVoiceClickAction,
          isListeningVoice = uiState.isListeningVoice,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
      }
    }
  }
}
