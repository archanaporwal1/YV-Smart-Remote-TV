package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.RemoteAccentBlue
import com.example.ui.theme.RemoteAccentCyan
import com.example.ui.theme.RemoteBorderColor
import com.example.ui.theme.RemoteTextPrimary
import com.example.ui.theme.RemoteTextSecondary

@Composable
fun DPadController(
  onDirectionClick: (direction: String) -> Unit,
  onOkClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .size(230.dp)
      .shadow(16.dp, CircleShape, ambientColor = RemoteAccentCyan.copy(alpha = 0.2f))
      .clip(CircleShape)
      .background(
        Brush.radialGradient(
          colors = listOf(
            Color(0xFF283448),
            Color(0xFF18202E),
            Color(0xFF0F141F)
          )
        )
      )
      .border(2.dp, Color(0xFF33425B), CircleShape)
      .testTag("dpad_controller"),
    contentAlignment = Alignment.Center
  ) {
    // Up Button
    DPadDirectionButton(
      direction = "UP",
      icon = Icons.Default.KeyboardArrowUp,
      modifier = Modifier
        .align(Alignment.TopCenter)
        .offset(y = 12.dp),
      onClick = { onDirectionClick("UP") }
    )

    // Down Button
    DPadDirectionButton(
      direction = "DOWN",
      icon = Icons.Default.KeyboardArrowDown,
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .offset(y = (-12).dp),
      onClick = { onDirectionClick("DOWN") }
    )

    // Left Button
    DPadDirectionButton(
      direction = "LEFT",
      icon = Icons.Default.KeyboardArrowLeft,
      modifier = Modifier
        .align(Alignment.CenterStart)
        .offset(x = 12.dp),
      onClick = { onDirectionClick("LEFT") }
    )

    // Right Button
    DPadDirectionButton(
      direction = "RIGHT",
      icon = Icons.Default.KeyboardArrowRight,
      modifier = Modifier
        .align(Alignment.CenterEnd)
        .offset(x = (-12).dp),
      onClick = { onDirectionClick("RIGHT") }
    )

    // Center OK / Select Button
    val okInteractionSource = remember { MutableInteractionSource() }
    val isOkPressed by okInteractionSource.collectIsPressedAsState()
    val okScale by animateFloatAsState(if (isOkPressed) 0.92f else 1f, label = "ok_scale")

    Box(
      modifier = Modifier
        .size(86.dp)
        .scale(okScale)
        .shadow(8.dp, CircleShape)
        .clip(CircleShape)
        .background(
          Brush.radialGradient(
            colors = if (isOkPressed) {
              listOf(RemoteAccentCyan, RemoteAccentBlue)
            } else {
              listOf(Color(0xFF2E3D56), Color(0xFF1B2433))
            }
          )
        )
        .border(1.5.dp, if (isOkPressed) RemoteAccentCyan else RemoteBorderColor, CircleShape)
        .clickable(
          interactionSource = okInteractionSource,
          indication = null,
          onClick = onOkClick
        )
        .testTag("button_ok"),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = "OK",
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 18.sp,
          letterSpacing = 1.sp
        ),
        color = if (isOkPressed) Color.White else RemoteTextPrimary
      )
    }
  }
}

@Composable
private fun DPadDirectionButton(
  direction: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val scale by animateFloatAsState(if (isPressed) 0.88f else 1f, label = "dpad_scale_$direction")

  Box(
    modifier = modifier
      .size(54.dp)
      .scale(scale)
      .clip(CircleShape)
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onClick
      )
      .testTag("button_dpad_${direction.lowercase()}"),
    contentAlignment = Alignment.Center
  ) {
    Icon(
      imageVector = icon,
      contentDescription = "DPad $direction",
      tint = if (isPressed) RemoteAccentCyan else RemoteTextSecondary,
      modifier = Modifier.size(32.dp)
    )
  }
}
