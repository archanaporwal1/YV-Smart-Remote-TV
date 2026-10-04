package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Input
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PredefinedTvApps
import com.example.model.TvApp
import com.example.ui.theme.BrandDisney
import com.example.ui.theme.BrandNetflix
import com.example.ui.theme.BrandPrimeVideo
import com.example.ui.theme.BrandYouTube
import com.example.ui.theme.RemoteAccentCyan
import com.example.ui.theme.RemoteAccentGreen
import com.example.ui.theme.RemoteAccentRed
import com.example.ui.theme.RemoteBorderColor
import com.example.ui.theme.RemoteButtonNormal
import com.example.ui.theme.RemoteCardDark
import com.example.ui.theme.RemoteTextMuted
import com.example.ui.theme.RemoteTextPrimary
import com.example.ui.theme.RemoteTextSecondary

@Composable
fun HardwareRockers(
  onCommand: (command: String, info: String) -> Unit,
  onAppLaunch: (TvApp) -> Unit,
  onVoiceClick: () -> Unit,
  isListeningVoice: Boolean = false,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    // 1. Navigation Toolbar (Back, Home, Menu, Input)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(18.dp))
        .background(RemoteCardDark)
        .border(1.dp, RemoteBorderColor, RoundedCornerShape(18.dp))
        .padding(horizontal = 8.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.SpaceAround,
      verticalAlignment = Alignment.CenterVertically
    ) {
      RemoteActionButton(
        icon = Icons.AutoMirrored.Filled.ArrowBack,
        label = "Back",
        testTag = "button_back",
        onClick = { onCommand("BACK", "Back") }
      )
      RemoteActionButton(
        icon = Icons.Default.Home,
        label = "Home",
        testTag = "button_home",
        onClick = { onCommand("HOME", "Home Screen") }
      )
      RemoteActionButton(
        icon = Icons.Default.Menu,
        label = "Menu",
        testTag = "button_menu",
        onClick = { onCommand("MENU", "Quick Menu") }
      )
      RemoteActionButton(
        icon = Icons.Default.Input,
        label = "Source",
        testTag = "button_input",
        onClick = { onCommand("INPUT", "Input Source") }
      )
    }

    // 2. Dual Rockers (Volume Rocker + Channel Rocker) with Center Voice Pill
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Left Column: Volume Rocker
      Column(
        modifier = Modifier
          .weight(1f)
          .height(130.dp)
          .clip(RoundedCornerShape(20.dp))
          .background(Color(0xFF192231))
          .border(1.dp, RemoteBorderColor, RoundedCornerShape(20.dp))
          .padding(4.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        RockerButton(
          label = "+",
          testTag = "button_vol_up",
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
          onClick = { onCommand("VOL_UP", "Volume +") }
        )
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(28.dp)
            .clickable { onCommand("MUTE", "Mute Toggle") },
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "VOL",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = RemoteTextMuted
          )
        }
        RockerButton(
          label = "-",
          testTag = "button_vol_down",
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
          onClick = { onCommand("VOL_DOWN", "Volume -") }
        )
      }

      // Middle Column: Voice Assistant & Media Controls
      Column(
        modifier = Modifier
          .weight(1.1f)
          .height(130.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Voice Assistant Button
        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
        val voicePulse by infiniteTransition.animateFloat(
          initialValue = 1f,
          targetValue = if (isListeningVoice) 1.15f else 1f,
          animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
          ),
          label = "voice_scale"
        )

        Box(
          modifier = Modifier
            .size(56.dp)
            .scale(voicePulse)
            .clip(CircleShape)
            .background(
              Brush.radialGradient(
                colors = if (isListeningVoice) {
                  listOf(RemoteAccentGreen, Color(0xFF065F46))
                } else {
                  listOf(Color(0xFF1F2D40), Color(0xFF141C28))
                }
              )
            )
            .border(
              width = 2.dp,
              color = if (isListeningVoice) RemoteAccentGreen else RemoteAccentCyan.copy(alpha = 0.5f),
              shape = CircleShape
            )
            .clickable { onVoiceClick() }
            .testTag("button_voice_assist"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Mic,
            contentDescription = "Voice Search",
            tint = if (isListeningVoice) Color.White else RemoteAccentCyan,
            modifier = Modifier.size(26.dp)
          )
        }

        // Quick Media Playback row
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF192231))
            .border(1.dp, RemoteBorderColor, RoundedCornerShape(14.dp))
            .padding(horizontal = 4.dp, vertical = 4.dp),
          horizontalArrangement = Arrangement.SpaceAround,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(32.dp)
              .clip(CircleShape)
              .clickable { onCommand("REWIND", "Rewind") }
              .testTag("button_rewind"),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.FastRewind,
              contentDescription = "Rewind",
              tint = RemoteTextSecondary,
              modifier = Modifier.size(18.dp)
            )
          }

          Box(
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(RemoteAccentCyan.copy(alpha = 0.2f))
              .clickable { onCommand("PLAY_PAUSE", "Play/Pause") }
              .testTag("button_play_pause"),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.PlayArrow,
              contentDescription = "Play/Pause",
              tint = RemoteAccentCyan,
              modifier = Modifier.size(22.dp)
            )
          }

          Box(
            modifier = Modifier
              .size(32.dp)
              .clip(CircleShape)
              .clickable { onCommand("FAST_FORWARD", "Fast Forward") }
              .testTag("button_fast_forward"),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.FastForward,
              contentDescription = "Forward",
              tint = RemoteTextSecondary,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }

      // Right Column: Channel Rocker
      Column(
        modifier = Modifier
          .weight(1f)
          .height(130.dp)
          .clip(RoundedCornerShape(20.dp))
          .background(Color(0xFF192231))
          .border(1.dp, RemoteBorderColor, RoundedCornerShape(20.dp))
          .padding(4.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        RockerButton(
          label = "+",
          testTag = "button_ch_up",
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
          onClick = { onCommand("CH_UP", "Channel +") }
        )
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(28.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "CH",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = RemoteTextMuted
          )
        }
        RockerButton(
          label = "-",
          testTag = "button_ch_down",
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
          onClick = { onCommand("CH_DOWN", "Channel -") }
        )
      }
    }

    // 3. Branded Quick Launch Buttons (YouTube, Netflix, Prime Video, Disney+)
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      val quickApps = listOf(
        PredefinedTvApps.defaultApps.first { it.id == "youtube" },
        PredefinedTvApps.defaultApps.first { it.id == "netflix" },
        PredefinedTvApps.defaultApps.first { it.id == "prime_video" },
        PredefinedTvApps.defaultApps.first { it.id == "disney" }
      )

      quickApps.forEach { app ->
        Box(
          modifier = Modifier
            .weight(1f)
            .height(38.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF161E2A))
            .border(1.dp, RemoteBorderColor, RoundedCornerShape(10.dp))
            .clickable { onAppLaunch(app) }
            .testTag("button_app_${app.id}"),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = app.shortLabel,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = app.brandColor
          )
        }
      }
    }
  }
}

@Composable
private fun RemoteActionButton(
  icon: ImageVector,
  label: String,
  testTag: String,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .size(46.dp)
      .clip(CircleShape)
      .clickable { onClick() }
      .testTag(testTag),
    contentAlignment = Alignment.Center
  ) {
    Icon(
      imageVector = icon,
      contentDescription = label,
      tint = RemoteTextSecondary,
      modifier = Modifier.size(22.dp)
    )
  }
}

@Composable
private fun RockerButton(
  label: String,
  testTag: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .background(if (isPressed) Color(0xFF28364F) else Color(0xFF202A3C))
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onClick
      )
      .testTag(testTag),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = label,
      fontSize = 20.sp,
      fontWeight = FontWeight.Bold,
      color = if (isPressed) RemoteAccentCyan else RemoteTextPrimary
    )
  }
}
