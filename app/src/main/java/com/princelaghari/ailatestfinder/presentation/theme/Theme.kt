package com.princelaghari.ailatestfinder.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Ultra-premium Charcoal & Deep Premium Black
val DeepBlack = Color(0xFF0A0A0A)
val DeepCharcoal = Color(0xFF141414)
val MediumCharcoal = Color(0xFF1F1F1F)

// Rich Metallic Gold Accent
val MetallicGold = Color(0xFFD4AF37)
val GoldenGlow = Color(0xFFFFDF00)
val PaleGold = Color(0xFFF3E5AB)

private val DarkColorScheme = darkColorScheme(
    primary = MetallicGold,
    secondary = GoldenGlow,
    tertiary = PaleGold,
    background = DeepBlack,
    surface = DeepCharcoal,
    onPrimary = DeepBlack,
    onSecondary = DeepBlack,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun AiLatestFinderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // Always premium dark mode
    content: @Composable () -> Unit
) {
    // We enforce Dark Mode regardless of system theme for that ultra-premium consistent design
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
