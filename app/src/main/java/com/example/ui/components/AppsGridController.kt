package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PredefinedTvApps
import com.example.model.TvApp
import com.example.ui.theme.RemoteAccentCyan
import com.example.ui.theme.RemoteBorderColor
import com.example.ui.theme.RemoteCardDark
import com.example.ui.theme.RemoteTextMuted
import com.example.ui.theme.RemoteTextPrimary

@Composable
fun AppsGridController(
  onAppClick: (TvApp) -> Unit,
  modifier: Modifier = Modifier
) {
  var searchQuery by remember { mutableStateOf("") }

  val filteredApps = remember(searchQuery) {
    if (searchQuery.isBlank()) {
      PredefinedTvApps.defaultApps
    } else {
      PredefinedTvApps.defaultApps.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
          it.category.contains(searchQuery, ignoreCase = true)
      }
    }
  }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .height(230.dp)
      .clip(RoundedCornerShape(24.dp))
      .background(RemoteCardDark)
      .border(1.dp, RemoteBorderColor, RoundedCornerShape(24.dp))
      .padding(12.dp)
      .testTag("apps_grid_controller")
  ) {
    // Search Apps
    OutlinedTextField(
      value = searchQuery,
      onValueChange = { searchQuery = it },
      modifier = Modifier
        .fillMaxWidth()
        .height(44.dp),
      placeholder = {
        Text(
          "Search TV apps…",
          fontSize = 12.sp,
          color = RemoteTextMuted
        )
      },
      leadingIcon = {
        Icon(
          imageVector = Icons.Default.Search,
          contentDescription = "Search",
          tint = RemoteTextMuted,
          modifier = Modifier.size(16.dp)
        )
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

    LazyVerticalGrid(
      columns = GridCells.Fixed(3),
      contentPadding = PaddingValues(2.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.fillMaxSize()
    ) {
      items(filteredApps) { app ->
        AppGridItem(app = app, onClick = { onAppClick(app) })
      }
    }
  }
}

@Composable
private fun AppGridItem(
  app: TvApp,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(48.dp)
      .clip(RoundedCornerShape(12.dp))
      .background(Color(0xFF1B2332))
      .border(1.dp, RemoteBorderColor, RoundedCornerShape(12.dp))
      .clickable { onClick() }
      .padding(horizontal = 8.dp),
    contentAlignment = Alignment.CenterStart
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Start
    ) {
      Box(
        modifier = Modifier
          .size(28.dp)
          .clip(CircleShape)
          .background(app.brandColor),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = app.shortLabel.take(2),
          color = Color.White,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold
        )
      }
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = app.name,
        style = MaterialTheme.typography.bodySmall.copy(
          fontWeight = FontWeight.Medium,
          fontSize = 11.sp
        ),
        color = RemoteTextPrimary,
        maxLines = 1
      )
    }
  }
}
