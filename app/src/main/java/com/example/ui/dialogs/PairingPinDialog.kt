package com.example.ui.dialogs

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CastConnected
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
  statusMessage: String,
  onConfirmPin: (pin: String, onResult: (Boolean, String) -> Unit) -> Unit,
  onDirectConnect: (TvDevice) -> Unit,
  onCancel: () -> Unit
) {
  var enteredPin by remember { mutableStateOf("") }
  var isVerifying by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

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
        Box(
          modifier = Modifier
            .size(54.dp)
            .clip(CircleShape)
            .background(Color(0xFF142030))
            .border(1.dp, RemoteAccentCyan.copy(alpha = 0.5f), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Tv,
            contentDescription = "Pairing with TV",
            tint = RemoteAccentCyan,
            modifier = Modifier.size(30.dp)
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = "Pair with ${targetTv?.name ?: "Android TV"}",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp
          ),
          color = RemoteTextPrimary,
          textAlign = TextAlign.Center
        )

        if (targetTv != null) {
          Text(
            text = "IP: ${targetTv.ipAddress}:${targetTv.port}",
            style = MaterialTheme.typography.labelSmall,
            color = RemoteTextMuted,
            textAlign = TextAlign.Center
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Real-time status / prompt banner
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF131924))
            .border(1.dp, Color(0xFF233045), RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(
              imageVector = Icons.Default.Info,
              contentDescription = "Info",
              tint = RemoteAccentCyan,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = if (statusMessage.isNotBlank()) statusMessage else "Look at your TV screen and enter the pairing code:",
              style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
              color = Color(0xFFCBD5E1),
              lineHeight = 16.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
          value = enteredPin,
          onValueChange = {
            if (it.length <= 8) {
              enteredPin = it
              errorMessage = null
            }
          },
          placeholder = {
            Text(
              text = "e.g. 1234 or A1B2",
              modifier = Modifier.fillMaxWidth(),
              textAlign = TextAlign.Center,
              color = Color(0xFF64748B),
              fontSize = 14.sp
            )
          },
          singleLine = true,
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
          textStyle = MaterialTheme.typography.titleLarge.copy(
            textAlign = TextAlign.Center,
            letterSpacing = 4.sp,
            fontWeight = FontWeight.Bold
          ),
          modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .testTag("input_pin"),
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

        AnimatedVisibility(visible = errorMessage != null) {
          errorMessage?.let { msg ->
            Text(
              text = msg,
              color = Color(0xFFEF4444),
              fontSize = 12.sp,
              modifier = Modifier.padding(top = 6.dp),
              textAlign = TextAlign.Center
            )
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Action Buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedButton(
            onClick = onCancel,
            modifier = Modifier
              .weight(1f)
              .height(46.dp),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text("Cancel", color = RemoteTextPrimary)
          }

          Button(
            onClick = {
              if (enteredPin.isBlank()) {
                errorMessage = "Please enter the code shown on your TV screen."
                return@Button
              }
              isVerifying = true
              errorMessage = null
              onConfirmPin(enteredPin) { success, msg ->
                isVerifying = false
                if (!success) {
                  errorMessage = msg
                }
              }
            },
            enabled = !isVerifying,
            modifier = Modifier
              .weight(1f)
              .height(46.dp)
              .testTag("button_pair_confirm"),
            colors = ButtonDefaults.buttonColors(containerColor = RemoteAccentCyan),
            shape = RoundedCornerShape(12.dp)
          ) {
            if (isVerifying) {
              CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                color = Color.Black,
                strokeWidth = 2.dp
              )
            } else {
              Text("Pair & Connect", color = Color.Black, fontWeight = FontWeight.Bold)
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Direct connect prominent action button
        if (targetTv != null) {
          Button(
            onClick = { onDirectConnect(targetTv) },
            modifier = Modifier
              .fillMaxWidth()
              .height(44.dp)
              .testTag("button_direct_connect"),
            colors = ButtonDefaults.buttonColors(
              containerColor = Color(0xFF1E2D44),
              contentColor = RemoteAccentCyan
            ),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, RemoteAccentCyan.copy(alpha = 0.6f))
          ) {
            Icon(
              imageVector = Icons.Default.CastConnected,
              contentDescription = "Direct Connect",
              tint = RemoteAccentCyan,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Direct Connect (No PIN Needed)",
              color = RemoteAccentCyan,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}
