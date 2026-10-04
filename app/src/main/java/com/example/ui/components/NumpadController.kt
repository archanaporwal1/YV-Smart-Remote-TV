package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.RemoteAccentCyan
import com.example.ui.theme.RemoteBorderColor
import com.example.ui.theme.RemoteCardDark
import com.example.ui.theme.RemoteTextPrimary
import com.example.ui.theme.RemoteTextSecondary

@Composable
fun NumpadController(
  onNumberClick: (Int) -> Unit,
  onSpecialClick: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val rows = listOf(
    listOf("1", "2", "3"),
    listOf("4", "5", "6"),
    listOf("7", "8", "9"),
    listOf("-", "0", "INFO")
  )

  Column(
    modifier = modifier
      .fillMaxWidth()
      .height(230.dp)
      .clip(RoundedCornerShape(24.dp))
      .background(RemoteCardDark)
      .border(1.dp, RemoteBorderColor, RoundedCornerShape(24.dp))
      .padding(10.dp)
      .testTag("numpad_controller"),
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    rows.forEach { rowItems ->
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        rowItems.forEach { label ->
          NumpadKey(
            label = label,
            modifier = Modifier.weight(1f),
            onClick = {
              val digit = label.toIntOrNull()
              if (digit != null) {
                onNumberClick(digit)
              } else {
                onSpecialClick(label)
              }
            }
          )
        }
      }
    }
  }
}

@Composable
private fun NumpadKey(
  label: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val scale by animateFloatAsState(if (isPressed) 0.92f else 1f, label = "num_scale")

  Box(
    modifier = modifier
      .height(44.dp)
      .scale(scale)
      .clip(RoundedCornerShape(12.dp))
      .background(if (isPressed) Color(0xFF2E3E5B) else Color(0xFF1B2332))
      .border(1.dp, if (isPressed) RemoteAccentCyan else RemoteBorderColor, RoundedCornerShape(12.dp))
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onClick
      )
      .testTag("numpad_key_$label"),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.titleMedium.copy(
        fontWeight = FontWeight.Bold,
        fontSize = if (label.length > 2) 12.sp else 18.sp
      ),
      color = if (isPressed) RemoteAccentCyan else RemoteTextPrimary
    )
  }
}
