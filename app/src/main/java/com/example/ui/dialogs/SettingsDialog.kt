package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.RemoteAccentCyan
import com.example.ui.theme.RemoteBorderColor
import com.example.ui.theme.RemoteCardDark
import com.example.ui.theme.RemoteChassisDark
import com.example.ui.theme.RemoteTextMuted
import com.example.ui.theme.RemoteTextPrimary
import com.example.ui.theme.RemoteTextSecondary

@Composable
fun SettingsDialog(
  hapticEnabled: Boolean,
  keepScreenOn: Boolean,
  touchpadSensitivity: Float,
  onToggleHaptic: (Boolean) -> Unit,
  onToggleKeepScreenOn: (Boolean) -> Unit,
  onSensitivityChange: (Float) -> Unit,
  onDismiss: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = RemoteChassisDark,
      border = androidx.compose.foundation.BorderStroke(1.dp, RemoteBorderColor),
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .testTag("settings_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Settings,
              contentDescription = "Settings",
              tint = RemoteAccentCyan,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Remote Settings",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
              ),
              color = RemoteTextPrimary
            )
          }

          IconButton(onClick = onDismiss) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = RemoteTextSecondary
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Haptic Feedback Switch
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(RemoteCardDark)
            .padding(horizontal = 14.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Vibration,
              contentDescription = "Vibrate",
              tint = RemoteAccentCyan,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text("Haptic Feedback", color = RemoteTextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
              Text("Vibrate on remote button press", color = RemoteTextMuted, fontSize = 11.sp)
            }
          }

          Switch(
            checked = hapticEnabled,
            onCheckedChange = onToggleHaptic,
            colors = SwitchDefaults.colors(checkedThumbColor = RemoteAccentCyan)
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Keep Screen Awake
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(RemoteCardDark)
            .padding(horizontal = 14.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Visibility,
              contentDescription = "Screen Awake",
              tint = RemoteAccentCyan,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text("Keep Screen On", color = RemoteTextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
              Text("Prevent screen from sleeping while using", color = RemoteTextMuted, fontSize = 11.sp)
            }
          }

          Switch(
            checked = keepScreenOn,
            onCheckedChange = onToggleKeepScreenOn,
            colors = SwitchDefaults.colors(checkedThumbColor = RemoteAccentCyan)
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Touchpad Sensitivity
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(RemoteCardDark)
            .padding(14.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Touchpad Sensitivity", color = RemoteTextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Text("${(touchpadSensitivity * 100).toInt()}%", color = RemoteAccentCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
          }
          Slider(
            value = touchpadSensitivity,
            onValueChange = onSensitivityChange,
            valueRange = 0.5f..2.0f,
            colors = SliderDefaults.colors(
              thumbColor = RemoteAccentCyan,
              activeTrackColor = RemoteAccentCyan
            )
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // App Info Box
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF131822))
            .padding(12.dp)
        ) {
          Column {
            Text(
              text = "YV Smart TV Remote",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            )
            Text(
              text = "Universal Smart Remote control for Android TV and Google TV OS. Built with touch navigation, live keyboard, and voice assistant.",
              color = RemoteTextMuted,
              fontSize = 11.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
          onClick = onDismiss,
          modifier = Modifier
            .fillMaxWidth()
            .height(42.dp),
          colors = ButtonDefaults.buttonColors(containerColor = RemoteAccentCyan),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text("Done", color = Color.Black, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
