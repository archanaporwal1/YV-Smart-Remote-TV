package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val TvRemoteColorScheme = darkColorScheme(
  primary = RemoteAccentCyan,
  onPrimary = Color.Black,
  primaryContainer = RemoteAccentBlue.copy(alpha = 0.2f),
  onPrimaryContainer = RemoteAccentCyan,
  secondary = RemoteAccentGreen,
  onSecondary = Color.Black,
  secondaryContainer = RemoteCardDark,
  onSecondaryContainer = RemoteTextPrimary,
  tertiary = RemoteAccentYellow,
  onTertiary = Color.Black,
  background = RemoteChassisDark,
  onBackground = RemoteTextPrimary,
  surface = RemoteSurfaceDark,
  onSurface = RemoteTextPrimary,
  surfaceVariant = RemoteCardDark,
  onSurfaceVariant = RemoteTextSecondary,
  outline = RemoteBorderColor,
  outlineVariant = RemoteBorderActive,
  error = RemoteAccentRed,
  onError = Color.White
)

@Composable
fun TvRemoteTheme(
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = TvRemoteColorScheme,
    typography = Typography,
    content = content
  )
}
