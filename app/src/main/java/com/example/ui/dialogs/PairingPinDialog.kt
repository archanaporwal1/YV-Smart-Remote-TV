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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.TvDevice
import com.example.ui.theme.RemoteAccentCyan
import com.example.ui.theme.RemoteBorderColor
import com.example.ui.theme.RemoteChassisDark
import com.example.ui.theme.RemoteTextMuted
import com.example.ui.theme.RemoteTextPrimary

@Composable
fun PairingPinDialog(
  targetTv: TvDevice?,
  pairingCode: String?,
  onConfirmPin: (String) -> Boolean,
  onCancel: () -> Unit
) {
  var enteredPin by remember { mutableStateOf(pairingCode ?: "") }
  var hasError by remember { mutableStateOf(false) }

  Dialog(onDismissRequest = onCancel) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = RemoteChassisDark,
      border = androidx.compose.foundation.BorderStroke(1.dp, RemoteBorderColor),
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .testTag("pairing_pin_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Icon(
          imageVector = Icons.Default.Tv,
          contentDescription = "Pairing",
          tint = RemoteAccentCyan,
          modifier = Modifier.size(44.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = "Pair with ${targetTv?.name ?: "Android TV"}",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp
          ),
          color = RemoteTextPrimary,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = "Enter the 4-digit code shown on your Android TV screen:",
          style = MaterialTheme.typography.bodySmall,
          color = RemoteTextMuted,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Suggested TV screen PIN demo badge
        if (pairingCode != null) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(Color(0xFF1E2838))
              .border(1.dp, RemoteAccentCyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
              .padding(horizontal = 16.dp, vertical = 8.dp)
          ) {
            Text(
              text = "TV Screen displays: $pairingCode",
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp,
              color = RemoteAccentCyan
            )
          }
          Spacer(modifier = Modifier.height(12.dp))
        }

        OutlinedTextField(
          value = enteredPin,
          onValueChange = {
            if (it.length <= 6) {
              enteredPin = it
              hasError = false
            }
          },
          singleLine = true,
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          textStyle = MaterialTheme.typography.titleLarge.copy(
            textAlign = TextAlign.Center,
            letterSpacing = 6.sp,
            fontWeight = FontWeight.Bold
          ),
          modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFF141923),
            unfocusedContainerColor = Color(0xFF141923),
            focusedBorderColor = RemoteAccentCyan,
            unfocusedBorderColor = RemoteBorderColor,
            focusedTextColor = RemoteTextPrimary,
            unfocusedTextColor = RemoteTextPrimary
          ),
          shape = RoundedCornerShape(14.dp)
        )

        if (hasError) {
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Incorrect code. Please try again.",
            color = Color(0xFFEF4444),
            fontSize = 12.sp
          )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedButton(
            onClick = onCancel,
            modifier = Modifier
              .weight(1f)
              .height(44.dp),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text("Cancel", color = RemoteTextPrimary)
          }

          Button(
            onClick = {
              val success = onConfirmPin(enteredPin)
              if (!success) {
                hasError = true
              }
            },
            modifier = Modifier
              .weight(1f)
              .height(44.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RemoteAccentCyan),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text("Pair & Connect", color = Color.Black, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
