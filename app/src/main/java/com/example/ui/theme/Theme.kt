package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val NdakoColorScheme = darkColorScheme(
  primary = OrPrestige,
  secondary = GrisTechnique,
  tertiary = OrMuted,
  background = NoirProfond,
  surface = NoirSec,
  onPrimary = NoirProfond,
  onSecondary = BlancPur,
  onTertiary = BlancPur,
  onBackground = BlancPur,
  onSurface = BlancPur,
  error = RougeAlerte,
  onError = NoirProfond
)

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = NdakoColorScheme,
    typography = Typography,
    content = content
  )
}
