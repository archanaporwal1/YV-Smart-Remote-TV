package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.CastConnected
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.TvOff
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ConnectionStatus
import com.example.model.TvState
import com.example.ui.theme.RemoteAccentCyan
import com.example.ui.theme.RemoteAccentGreen
import com.example.ui.theme.RemoteAccentRed
import com.example.ui.theme.RemoteBorderColor
import com.example.ui.theme.RemoteCardDark
import com.example.ui.theme.RemoteTextMuted
import com.example.ui.theme.RemoteTextSecondary

@Composable
fun TvCompanionBar(
  state: TvState,
  onTvSelectorClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(18.dp))
      .background(
        Brush.verticalGradient(
          colors = listOf(
            RemoteCardDark.copy(alpha = 0.95f),
            Color(0xFF131A26)
          )
        )
      )
      .border(1.dp, RemoteBorderColor, RoundedCornerShape(18.dp))
      .clickable { onTvSelectorClick() }
      .padding(horizontal = 14.dp, vertical = 10.dp)
      .testTag("tv_companion_bar")
  ) {
    Column {
      // Top row: Connected TV name & Status pill
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          Icon(
            imageVector = if (state.isPowerOn) Icons.Default.Tv else Icons.Default.TvOff,
            contentDescription = "TV Status",
            tint = if (state.isPowerOn) RemoteAccentCyan else RemoteTextMuted,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = state.activeTv?.name ?: "No TV Connected",
            style = MaterialTheme.typography.bodyMedium.copy(
              fontWeight = FontWeight.SemiBold,
              fontSize = 14.sp
            ),
            color = Color.White,
            maxLines = 1
          )
        }

        // Connection badge
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
              if (state.connectionStatus == ConnectionStatus.CONNECTED)
                RemoteAccentGreen.copy(alpha = 0.15f)
              else RemoteAccentRed.copy(alpha = 0.15f)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Box(
            modifier = Modifier
              .size(7.dp)
              .clip(CircleShape)
              .background(
                if (state.connectionStatus == ConnectionStatus.CONNECTED)
                  RemoteAccentGreen
                else RemoteAccentRed
              )
          )
          Spacer(modifier = Modifier.width(5.dp))
          Text(
            text = when (state.connectionStatus) {
              ConnectionStatus.CONNECTED -> "Online"
              ConnectionStatus.SCANNING -> "Scanning…"
              ConnectionStatus.PAIRING -> "Pairing…"
              else -> "Disconnected"
            },
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Medium,
              fontSize = 11.sp
            ),
            color = if (state.connectionStatus == ConnectionStatus.CONNECTED)
              RemoteAccentGreen
            else RemoteAccentRed
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // TV Display Monitor / Simulated On-Screen Info
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(Color(0xFF090D14))
          .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Current App / Input
        Row(verticalAlignment = Alignment.CenterVertically) {
          if (state.currentRunningApp != null) {
            Box(
              modifier = Modifier
                .size(18.dp)
                .clip(CircleShape)
                .background(state.currentRunningApp.brandColor),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = state.currentRunningApp.shortLabel.take(1),
                color = Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
              )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = state.currentRunningApp.name,
              color = Color(0xFFE2E8F0),
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium
            )
          } else {
            Text(
              text = "${state.currentInput} • CH ${state.currentChannel}",
              color = Color(0xFF94A3B8),
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }

        // Live Action Indicator & Volume
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = if (state.isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
            contentDescription = "Volume",
            tint = if (state.isMuted) RemoteAccentRed else RemoteTextSecondary,
            modifier = Modifier.size(15.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (state.isMuted) "Mute" else "${state.volume}%",
            color = if (state.isMuted) RemoteAccentRed else Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
          )

          Spacer(modifier = Modifier.width(8.dp))
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(RemoteAccentCyan.copy(alpha = 0.12f))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = state.lastAction.take(16),
              color = RemoteAccentCyan,
              fontSize = 10.sp,
              maxLines = 1
            )
          }
        }
      }

      // Small volume bar indicator
      Spacer(modifier = Modifier.height(4.dp))
      LinearProgressIndicator(
        progress = { (state.volume / 100f).coerceIn(0f, 1f) },
        modifier = Modifier
          .fillMaxWidth()
          .height(2.dp)
          .clip(CircleShape),
        color = if (state.isMuted) RemoteAccentRed else RemoteAccentCyan,
        trackColor = Color(0xFF1E2638)
      )
    }
  }
}
