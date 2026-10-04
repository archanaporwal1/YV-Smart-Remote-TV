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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.example.ui.theme.RemoteAccentBlue
import com.example.ui.theme.RemoteAccentCyan
import com.example.ui.theme.RemoteAccentGreen
import com.example.ui.theme.RemoteAccentRed
import com.example.ui.theme.RemoteAccentYellow
import com.example.ui.theme.RemoteBorderColor
import com.example.ui.theme.RemoteCardDark
import com.example.ui.theme.RemoteChassisDark
import com.example.ui.theme.RemoteTextMuted
import com.example.ui.theme.RemoteTextPrimary

data class ButtonGuideItem(
  val sectionNumber: String,
  val title: String,
  val badgeColor: Color,
  val description: String,
  val buttons: List<String>
)

@Composable
fun ButtonPlacementGuideDialog(
  onDismiss: () -> Unit
) {
  val guideSections = listOf(
    ButtonGuideItem(
      sectionNumber = "1",
      title = "Header & Power Controls",
      badgeColor = RemoteAccentRed,
      description = "Located at the very top of the remote chassis.",
      buttons = listOf(
        "🔴 Power Button: Toggles TV standby / wake with glowing red LED state indicator",
        "📡 IR & Wi-Fi Status: Real-time connectivity and TV transmitter signal",
        "⚙️ Settings Button: Opens haptic strength, screen awake & sensitivity options"
      )
    ),
    ButtonGuideItem(
      sectionNumber = "2",
      title = "Smart TV Companion Monitor",
      badgeColor = RemoteAccentCyan,
      description = "Real-time feedback bar mirroring your Android TV.",
      buttons = listOf(
        "🟢 TV Status Pill: Active TV name, IP address, and connection badge",
        "📺 Live Feed Banner: Shows active HDMI input, channel, or running streaming app",
        "🔊 Volume Bar: Dynamic percentage bar (0-100%) and instant mute alert",
        "💬 Command Toast: Real-time acknowledgment for each button press"
      )
    ),
    ButtonGuideItem(
      sectionNumber = "3",
      title = "Mode Navigation Switcher",
      badgeColor = RemoteAccentBlue,
      description = "Fast switching between 5 distinct controller layouts.",
      buttons = listOf(
        "🔘 Remote: Classic circular tactile D-Pad navigation",
        "👆 Touchpad: Smooth swipe trackpad with tap-to-click & list scrollbar",
        "📱 Apps: Direct launcher grid for installed Android TV applications",
        "🔢 Numpad: 0-9 channel dialer with sub-channel dash and TV Guide",
        "⌨️ Type (Keyboard): Direct phone-to-TV keyboard with instant sync"
      )
    ),
    ButtonGuideItem(
      sectionNumber = "4",
      title = "Main Navigation Zone",
      badgeColor = RemoteAccentYellow,
      description = "The central interaction pad for navigating TV menus.",
      buttons = listOf(
        "⬆️ Up / ⬇️ Down / ⬅️ Left / ➡️ Right: 4-Way directional navigation ring",
        "⭕ Center 'OK' Button: Large circular select key with radial gradient and haptics"
      )
    ),
    ButtonGuideItem(
      sectionNumber = "5",
      title = "Android TV Navigation Bar",
      badgeColor = Color(0xFFA855F7),
      description = "Standard system controls for Android TV and Google TV.",
      buttons = listOf(
        "↩️ Back: Returns to previous screen or closes active app dialog",
        "🏠 Home: Returns immediately to Android TV Leanback launcher",
        "☰ Menu: Opens app-specific contextual settings and options",
        "🔌 Source (Input): Cycles through HDMI 1, HDMI 2, HDMI 3, TV, and AV inputs"
      )
    ),
    ButtonGuideItem(
      sectionNumber = "6",
      title = "Dual Rockers & Voice Control Hub",
      badgeColor = RemoteAccentGreen,
      description = "Tactile vertical rockers alongside voice assistant.",
      buttons = listOf(
        "🔊 Left Rocker: Volume Up (+), Volume Down (-), and Center Mute key",
        "🎙️ Center Mic Pill: Google Assistant / Voice Search with pulsating speech waves",
        "⏯️ Media Row: Rewind (<<), Play/Pause, and Fast-Forward (>>)",
        "📺 Right Rocker: Channel Up (+), Channel Down (-), and Center Channel info"
      )
    ),
    ButtonGuideItem(
      sectionNumber = "7",
      title = "Streaming App Quick Shortcuts",
      badgeColor = Color(0xFFEC4899),
      description = "Dedicated physical-style branded shortcut buttons at bottom.",
      buttons = listOf(
        "🔴 YouTube (YT): Direct launch for Android TV YouTube app",
        "🔴 Netflix (N): Direct launch for Netflix Ninja app",
        "🔵 Prime Video (Prime): Direct launch for Amazon Prime Video",
        "🔵 Disney+: Direct launch for Disney+ TV application"
      )
    )
  )

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = RemoteChassisDark,
      border = androidx.compose.foundation.BorderStroke(1.dp, RemoteBorderColor),
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .testTag("button_placement_guide_dialog")
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
            Box(
              modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(RemoteAccentCyan.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Tune,
                contentDescription = "Button Guide",
                tint = RemoteAccentCyan,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Remote UI Layout Guide",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  fontSize = 17.sp
                ),
                color = RemoteTextPrimary
              )
              Text(
                text = "Button Placement & Controller Architecture",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = RemoteTextMuted
              )
            }
          }

          IconButton(onClick = onDismiss) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = RemoteTextPrimary
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Column(
          modifier = Modifier
            .fillMaxWidth()
            .height(380.dp)
            .verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          guideSections.forEach { section ->
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(RemoteCardDark)
                .border(1.dp, RemoteBorderColor, RoundedCornerShape(14.dp))
                .padding(12.dp)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(section.badgeColor.copy(alpha = 0.2f)),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = section.sectionNumber,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = section.badgeColor
                  )
                }

                Text(
                  text = section.title,
                  style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                  ),
                  color = RemoteTextPrimary
                )
              }

              Spacer(modifier = Modifier.height(4.dp))

              Text(
                text = section.description,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = RemoteTextMuted
              )

              Spacer(modifier = Modifier.height(8.dp))

              section.buttons.forEach { btnDesc ->
                Text(
                  text = "• $btnDesc",
                  style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                  color = Color(0xFFCBD5E1),
                  modifier = Modifier.padding(vertical = 2.dp)
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Button(
          onClick = onDismiss,
          modifier = Modifier
            .fillMaxWidth()
            .height(42.dp),
          colors = ButtonDefaults.buttonColors(containerColor = RemoteAccentCyan),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text(
            text = "Got it",
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          )
        }
      }
    }
  }
}
