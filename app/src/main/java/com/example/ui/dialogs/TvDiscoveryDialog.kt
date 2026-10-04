package com.example.ui.dialogs

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.ConnectionStatus
import com.example.model.DiscoveredTvHost
import com.example.model.TvDevice
import com.example.model.TvState
import com.example.ui.theme.RemoteAccentCyan
import com.example.ui.theme.RemoteAccentGreen
import com.example.ui.theme.RemoteAccentRed
import com.example.ui.theme.RemoteBorderColor
import com.example.ui.theme.RemoteCardDark
import com.example.ui.theme.RemoteChassisDark
import com.example.ui.theme.RemoteTextMuted
import com.example.ui.theme.RemoteTextPrimary
import com.example.ui.theme.RemoteTextSecondary

@Composable
fun TvDiscoveryDialog(
  tvState: TvState,
  discoveredDevices: List<TvDevice>,
  discoveredIps: List<String>,
  discoveredHosts: List<DiscoveredTvHost>,
  isScanning: Boolean,
  scanProgress: Int,
  currentScanningIp: String,
  subnetInfo: String,
  onConnect: (TvDevice) -> Unit,
  onConnectIp: (String) -> Unit,
  onDisconnect: () -> Unit,
  onScan: () -> Unit,
  onCancelScan: () -> Unit,
  onAddManualIp: (name: String, ip: String, port: Int) -> Unit,
  onDismiss: () -> Unit
) {
  var showManualInput by remember { mutableStateOf(false) }
  var manualName by remember { mutableStateOf("") }
  var manualIp by remember { mutableStateOf("") }
  var activeViewTab by remember { mutableStateOf("devices") } // "devices" or "found_ips"

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = RemoteChassisDark,
      border = androidx.compose.foundation.BorderStroke(1.dp, RemoteBorderColor),
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .testTag("tv_discovery_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        // Title Bar
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Wifi,
              contentDescription = "Wi-Fi",
              tint = RemoteAccentCyan,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "Network TV Scanner",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  fontSize = 17.sp
                ),
                color = RemoteTextPrimary
              )
              Text(
                text = subnetInfo,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = RemoteTextMuted
              )
            }
          }

          IconButton(onClick = onDismiss) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = RemoteTextSecondary
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Scanning Progress Card
        if (isScanning) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(Color(0xFF131B28))
              .border(1.dp, RemoteAccentCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
              .padding(10.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(
                  modifier = Modifier.size(14.dp),
                  strokeWidth = 2.dp,
                  color = RemoteAccentCyan
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "Scanning subnet: $currentScanningIp",
                  fontSize = 11.sp,
                  color = RemoteTextPrimary,
                  maxLines = 1
                )
              }
              Text(
                text = "$scanProgress%",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = RemoteAccentCyan
              )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
              progress = { (scanProgress / 100f).coerceIn(0f, 1f) },
              modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(CircleShape),
              color = RemoteAccentCyan,
              trackColor = Color(0xFF1E2838)
            )
          }
          Spacer(modifier = Modifier.height(10.dp))
        }

        // Segmented selector: TV Devices vs Discovered IP Addresses
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF161E2C))
            .padding(3.dp)
        ) {
          Box(
            modifier = Modifier
              .weight(1f)
              .height(34.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(if (activeViewTab == "devices") Color(0xFF26334A) else Color.Transparent)
              .clickable { activeViewTab = "devices" }
              .padding(horizontal = 4.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "TVs (${discoveredDevices.size})",
              fontSize = 12.sp,
              fontWeight = if (activeViewTab == "devices") FontWeight.Bold else FontWeight.Medium,
              color = if (activeViewTab == "devices") RemoteAccentCyan else RemoteTextMuted
            )
          }

          Box(
            modifier = Modifier
              .weight(1f)
              .height(34.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(if (activeViewTab == "found_ips") Color(0xFF26334A) else Color.Transparent)
              .clickable { activeViewTab = "found_ips" }
              .padding(horizontal = 4.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "Found IPs (${discoveredIps.size})",
              fontSize = 12.sp,
              fontWeight = if (activeViewTab == "found_ips") FontWeight.Bold else FontWeight.Medium,
              color = if (activeViewTab == "found_ips") RemoteAccentCyan else RemoteTextMuted
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (showManualInput) {
          // Manual IP Form
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(16.dp))
              .background(RemoteCardDark)
              .padding(14.dp)
          ) {
            Text(
              text = "Connect via IP Address",
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp,
              color = RemoteAccentCyan
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
              value = manualName,
              onValueChange = { manualName = it },
              placeholder = { Text("TV Name (e.g. Sony BRAVIA)", fontSize = 12.sp) },
              modifier = Modifier.fillMaxWidth(),
              singleLine = true,
              colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = RemoteTextPrimary,
                unfocusedTextColor = RemoteTextPrimary
              )
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
              value = manualIp,
              onValueChange = { manualIp = it },
              placeholder = { Text("IP Address (e.g. 192.168.1.102)", fontSize = 12.sp) },
              modifier = Modifier.fillMaxWidth(),
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              singleLine = true,
              colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = RemoteTextPrimary,
                unfocusedTextColor = RemoteTextPrimary
              )
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.End
            ) {
              OutlinedButton(
                onClick = { showManualInput = false },
                modifier = Modifier.height(38.dp)
              ) {
                Text("Cancel", fontSize = 12.sp)
              }
              Spacer(modifier = Modifier.width(8.dp))
              Button(
                onClick = {
                  if (manualIp.isNotBlank()) {
                    onAddManualIp(manualName, manualIp, 6466)
                    showManualInput = false
                  }
                },
                modifier = Modifier.height(38.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RemoteAccentCyan)
              ) {
                Text("Connect", fontSize = 12.sp, color = Color.Black)
              }
            }
          }
        } else if (activeViewTab == "found_ips") {
          // List of Found IP Addresses directly stored in ViewModel
          LazyColumn(
            modifier = Modifier
              .fillMaxWidth()
              .height(200.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            if (discoveredIps.isEmpty()) {
              item {
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 30.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = if (isScanning) "Scanning local subnet for TV IPs…" else "No TV IPs discovered yet. Tap 'Scan Wi-Fi' below.",
                    color = RemoteTextMuted,
                    fontSize = 12.sp
                  )
                }
              }
            } else {
              items(discoveredIps) { ip ->
                val hostDetails = discoveredHosts.find { it.ipAddress == ip }
                val isConnected = tvState.activeTv?.ipAddress == ip

                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isConnected) Color(0xFF1E2D44) else RemoteCardDark)
                    .border(
                      width = 1.dp,
                      color = if (isConnected) RemoteAccentCyan else RemoteBorderColor,
                      shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onConnectIp(ip) }
                    .padding(10.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                  ) {
                    Box(
                      modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(if (isConnected) RemoteAccentCyan else Color(0xFF263246)),
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(
                        imageVector = Icons.Default.Router,
                        contentDescription = "IP Host",
                        tint = if (isConnected) Color.Black else RemoteAccentCyan,
                        modifier = Modifier.size(18.dp)
                      )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                      Text(
                        text = ip,
                        style = MaterialTheme.typography.bodyMedium.copy(
                          fontWeight = FontWeight.Bold,
                          fontSize = 13.sp
                        ),
                        color = RemoteTextPrimary
                      )
                      Text(
                        text = "${hostDetails?.brandHint ?: "Android TV"} • Port ${hostDetails?.port ?: 6466}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = RemoteTextMuted
                      )
                    }
                  }

                  // Ping latency badge & Connect
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    if (hostDetails != null && hostDetails.latencyMs > 0) {
                      Text(
                        text = "${hostDetails.latencyMs}ms",
                        color = RemoteAccentGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                      )
                      Spacer(modifier = Modifier.width(8.dp))
                    }
                    Button(
                      onClick = { onConnectIp(ip) },
                      modifier = Modifier.height(30.dp),
                      colors = ButtonDefaults.buttonColors(
                        containerColor = if (isConnected) RemoteAccentGreen.copy(alpha = 0.2f) else RemoteAccentCyan
                      ),
                      shape = RoundedCornerShape(8.dp)
                    ) {
                      Text(
                        text = if (isConnected) "Active" else "Connect",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isConnected) RemoteAccentGreen else Color.Black
                      )
                    }
                  }
                }
              }
            }
          }
        } else {
          // Standard Devices List
          LazyColumn(
            modifier = Modifier
              .fillMaxWidth()
              .height(200.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            items(discoveredDevices) { device ->
              val isCurrent = tvState.activeTv?.id == device.id

              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(14.dp))
                  .background(if (isCurrent) Color(0xFF1E2D44) else RemoteCardDark)
                  .border(
                    width = 1.dp,
                    color = if (isCurrent) RemoteAccentCyan else RemoteBorderColor,
                    shape = RoundedCornerShape(14.dp)
                  )
                  .clickable { onConnect(device) }
                  .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.weight(1f)
                ) {
                  Box(
                    modifier = Modifier
                      .size(36.dp)
                      .clip(CircleShape)
                      .background(if (isCurrent) RemoteAccentCyan else Color(0xFF263246)),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(
                      imageVector = Icons.Default.Tv,
                      contentDescription = "TV",
                      tint = if (isCurrent) Color.Black else RemoteAccentCyan,
                      modifier = Modifier.size(20.dp)
                    )
                  }
                  Spacer(modifier = Modifier.width(12.dp))
                  Column {
                    Text(
                      text = device.name,
                      style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                      ),
                      color = RemoteTextPrimary,
                      maxLines = 1
                    )
                    Text(
                      text = "${device.brand} • ${device.ipAddress}",
                      style = MaterialTheme.typography.labelSmall,
                      color = RemoteTextMuted
                    )
                  }
                }

                if (isCurrent) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = Icons.Default.CheckCircle,
                      contentDescription = "Active",
                      tint = RemoteAccentGreen,
                      modifier = Modifier.size(20.dp)
                    )
                  }
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Action Buttons Row (Scan Wi-Fi / Cancel Scan, Add by IP)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          if (isScanning) {
            Button(
              onClick = onCancelScan,
              modifier = Modifier
                .weight(1f)
                .height(42.dp),
              colors = ButtonDefaults.buttonColors(containerColor = RemoteAccentRed.copy(alpha = 0.2f)),
              shape = RoundedCornerShape(12.dp)
            ) {
              Text("Stop Scan", color = RemoteAccentRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
          } else {
            OutlinedButton(
              onClick = onScan,
              modifier = Modifier
                .weight(1f)
                .height(42.dp),
              shape = RoundedCornerShape(12.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Scan",
                tint = RemoteAccentCyan,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text("Scan Wi-Fi", fontSize = 12.sp, color = RemoteTextPrimary)
            }
          }

          Spacer(modifier = Modifier.width(8.dp))

          Button(
            onClick = { showManualInput = !showManualInput },
            modifier = Modifier
              .weight(1f)
              .height(42.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RemoteCardDark),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Add,
              contentDescription = "Manual IP",
              tint = RemoteAccentCyan,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Add by IP",
              fontSize = 12.sp,
              color = RemoteTextPrimary
            )
          }
        }

        if (tvState.activeTv != null) {
          Spacer(modifier = Modifier.height(8.dp))
          Button(
            onClick = onDisconnect,
            modifier = Modifier
              .fillMaxWidth()
              .height(38.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RemoteAccentRed.copy(alpha = 0.15f)),
            shape = RoundedCornerShape(10.dp)
          ) {
            Text("Disconnect TV", color = RemoteAccentRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
