package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardReturn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.ui.theme.RemoteAccentCyan
import com.example.ui.theme.RemoteAccentGreen
import com.example.ui.theme.RemoteBorderColor
import com.example.ui.theme.RemoteCardDark
import com.example.ui.theme.RemoteTextMuted
import com.example.ui.theme.RemoteTextPrimary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun KeyboardController(
  text: String,
  onTextChange: (String) -> Unit,
  onSubmit: () -> Unit,
  onClear: () -> Unit,
  onVoiceClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val suggestions = listOf("4K Nature", "Lofi Music", "Action Movies", "Latest News", "Cartoons")

  Column(
    modifier = modifier
      .fillMaxWidth()
      .height(230.dp)
      .clip(RoundedCornerShape(24.dp))
      .background(RemoteCardDark)
      .border(1.dp, RemoteBorderColor, RoundedCornerShape(24.dp))
      .padding(12.dp)
      .testTag("keyboard_controller"),
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    Column {
      Text(
        text = "Type directly into Android TV",
        style = MaterialTheme.typography.labelSmall.copy(
          fontWeight = FontWeight.SemiBold,
          fontSize = 11.sp
        ),
        color = RemoteTextMuted
      )

      Spacer(modifier = Modifier.height(8.dp))

      OutlinedTextField(
        value = text,
        onValueChange = onTextChange,
        modifier = Modifier
          .fillMaxWidth()
          .height(54.dp),
        placeholder = {
          Text(
            "Search or enter text on TV…",
            fontSize = 13.sp,
            color = RemoteTextMuted
          )
        },
        trailingIcon = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            if (text.isNotEmpty()) {
              IconButton(onClick = onClear) {
                Icon(
                  imageVector = Icons.Default.Clear,
                  contentDescription = "Clear",
                  tint = RemoteTextMuted,
                  modifier = Modifier.size(18.dp)
                )
              }
            }
            IconButton(onClick = onVoiceClick) {
              Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Voice Dictate",
                tint = RemoteAccentGreen,
                modifier = Modifier.size(20.dp)
              )
            }
          }
        },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = Color(0xFF141923),
          unfocusedContainerColor = Color(0xFF141923),
          focusedBorderColor = RemoteAccentCyan,
          unfocusedBorderColor = RemoteBorderColor,
          focusedTextColor = RemoteTextPrimary,
          unfocusedTextColor = RemoteTextPrimary
        ),
        shape = RoundedCornerShape(12.dp)
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Quick Suggestions
      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        suggestions.forEach { suggestion ->
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(Color(0xFF1F2839))
              .border(1.dp, RemoteBorderColor, RoundedCornerShape(8.dp))
              .clickable {
                onTextChange(suggestion)
              }
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(
              text = suggestion,
              fontSize = 11.sp,
              color = RemoteAccentCyan,
              fontWeight = FontWeight.Medium
            )
          }
        }
      }
    }

    // Submit button
    Button(
      onClick = onSubmit,
      modifier = Modifier
        .fillMaxWidth()
        .height(44.dp)
        .testTag("button_submit_keyboard"),
      colors = ButtonDefaults.buttonColors(
        containerColor = RemoteAccentCyan,
        contentColor = Color.Black
      ),
      shape = RoundedCornerShape(12.dp)
    ) {
      Icon(
        imageVector = Icons.Default.KeyboardReturn,
        contentDescription = "Enter",
        modifier = Modifier.size(16.dp)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = "Send to TV (Enter)",
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp
      )
    }
  }
}
