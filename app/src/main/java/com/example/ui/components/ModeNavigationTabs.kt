package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.PanToolAlt
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.RemoteMode
import com.example.ui.theme.RemoteAccentCyan
import com.example.ui.theme.RemoteBorderColor
import com.example.ui.theme.RemoteCardDark
import com.example.ui.theme.RemoteTextMuted
import com.example.ui.theme.RemoteTextPrimary

data class TabItem(
  val mode: RemoteMode,
  val label: String,
  val icon: ImageVector,
  val testTag: String
)

@Composable
fun ModeNavigationTabs(
  selectedMode: RemoteMode,
  onModeSelected: (RemoteMode) -> Unit,
  modifier: Modifier = Modifier
) {
  val tabs = listOf(
    TabItem(RemoteMode.DPAD, "Remote", Icons.Default.RadioButtonChecked, "tab_remote"),
    TabItem(RemoteMode.TOUCHPAD, "Touchpad", Icons.Default.PanToolAlt, "tab_touchpad"),
    TabItem(RemoteMode.APPS, "Apps", Icons.Default.Apps, "tab_apps"),
    TabItem(RemoteMode.NUMPAD, "Numpad", Icons.Default.Dialpad, "tab_numpad"),
    TabItem(RemoteMode.KEYBOARD, "Type", Icons.Default.Keyboard, "tab_keyboard")
  )

  Row(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .background(RemoteCardDark)
      .border(1.dp, RemoteBorderColor, RoundedCornerShape(16.dp))
      .padding(4.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    tabs.forEach { tab ->
      val isSelected = tab.mode == selectedMode
      val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFF26334A) else Color.Transparent,
        label = "tab_bg"
      )
      val contentColor by animateColorAsState(
        targetValue = if (isSelected) RemoteAccentCyan else RemoteTextMuted,
        label = "tab_color"
      )

      Box(
        modifier = Modifier
          .weight(1f)
          .height(40.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(backgroundColor)
          .clickable { onModeSelected(tab.mode) }
          .padding(horizontal = 4.dp)
          .testTag(tab.testTag),
        contentAlignment = Alignment.Center
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Icon(
            imageVector = tab.icon,
            contentDescription = tab.label,
            tint = contentColor,
            modifier = Modifier.size(16.dp)
          )
          if (isSelected) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = tab.label,
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
              ),
              color = RemoteTextPrimary,
              maxLines = 1
            )
          }
        }
      }
    }
  }
}
