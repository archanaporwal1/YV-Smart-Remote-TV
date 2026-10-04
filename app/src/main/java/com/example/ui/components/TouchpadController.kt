package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.PanToolAlt
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.RemoteAccentBlue
import com.example.ui.theme.RemoteAccentCyan
import com.example.ui.theme.RemoteBorderActive
import com.example.ui.theme.RemoteBorderColor
import com.example.ui.theme.RemoteTextMuted
import com.example.ui.theme.RemoteTextSecondary
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun TouchpadController(
  onDirectionSwipe: (direction: String) -> Unit,
  onTapClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  var touchPosition by remember { mutableStateOf<Offset?>(null) }
  var isTouching by remember { mutableStateOf(false) }
  var accumulatedDx by remember { mutableFloatStateOf(0f) }
  var accumulatedDy by remember { mutableFloatStateOf(0f) }

  val swipeThreshold = 55f // Pixels threshold for triggering directional jump

  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(230.dp)
      .clip(RoundedCornerShape(24.dp))
      .background(
        Brush.linearGradient(
          colors = listOf(
            Color(0xFF1E2838),
            Color(0xFF131A26),
            Color(0xFF17202E)
          )
        )
      )
      .border(
        width = 1.5.dp,
        color = if (isTouching) RemoteAccentCyan else RemoteBorderColor,
        shape = RoundedCornerShape(24.dp)
      )
      .pointerInput(Unit) {
        detectTapGestures(
          onTap = {
            onTapClick()
          }
        )
      }
      .pointerInput(Unit) {
        detectDragGestures(
          onDragStart = { offset ->
            isTouching = true
            touchPosition = offset
            accumulatedDx = 0f
            accumulatedDy = 0f
          },
          onDragEnd = {
            isTouching = false
            touchPosition = null
          },
          onDragCancel = {
            isTouching = false
            touchPosition = null
          },
          onDrag = { change, dragAmount ->
            change.consume()
            touchPosition = change.position
            accumulatedDx += dragAmount.x
            accumulatedDy += dragAmount.y

            if (abs(accumulatedDx) > swipeThreshold) {
              if (accumulatedDx > 0) {
                onDirectionSwipe("RIGHT")
              } else {
                onDirectionSwipe("LEFT")
              }
              accumulatedDx = 0f
            }

            if (abs(accumulatedDy) > swipeThreshold) {
              if (accumulatedDy > 0) {
                onDirectionSwipe("DOWN")
              } else {
                onDirectionSwipe("UP")
              }
              accumulatedDy = 0f
            }
          }
        )
      }
      .testTag("touchpad_surface"),
    contentAlignment = Alignment.Center
  ) {
    // Subtle background guidelines and hint
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Icon(
        imageVector = Icons.Default.TouchApp,
        contentDescription = "Touchpad Mode",
        tint = if (isTouching) RemoteAccentCyan else RemoteTextMuted.copy(alpha = 0.5f),
        modifier = Modifier.size(36.dp)
      )
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = "Swipe to Navigate • Tap to Select",
        style = MaterialTheme.typography.bodySmall.copy(
          fontWeight = FontWeight.Medium,
          fontSize = 12.sp
        ),
        color = if (isTouching) RemoteAccentCyan else RemoteTextMuted
      )
    }

    // Touch Indicator Dot / Ripple Cursor
    touchPosition?.let { pos ->
      Box(
        modifier = Modifier
          .offset { IntOffset(pos.x.roundToInt() - 20, pos.y.roundToInt() - 20) }
          .size(40.dp)
          .clip(CircleShape)
          .background(RemoteAccentCyan.copy(alpha = 0.25f))
          .border(2.dp, RemoteAccentCyan, CircleShape)
      )
    }

    // Right-side scroll guideline
    Box(
      modifier = Modifier
        .align(Alignment.CenterEnd)
        .padding(end = 8.dp)
        .size(width = 3.dp, height = 70.dp)
        .clip(RoundedCornerShape(2.dp))
        .background(RemoteBorderActive)
    )
  }
}
