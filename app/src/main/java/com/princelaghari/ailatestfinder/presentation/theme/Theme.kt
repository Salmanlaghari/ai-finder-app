package com.princelaghari.ailatestfinder.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Ultra-premium Violet-Black and Deep Dashboard tones
val BgColor = Color(0xFF08070C)
val CardColor = Color(0xFF131019)
val Card2Color = Color(0xFF1A1524)

// Premium Accents from dashboard
val MetallicGold = Color(0xFFD4AF6A)
val AmberAccent = Color(0xFFF2C879)
val VioletAccent = Color(0xFF8B5CF6)
val TextColor = Color(0xFFF6F4F1)
val TextDimColor = Color(0xFF8D8896)

// Neon gradient accents
val CyanAccent = Color(0xFF00F2FE)
val MagentaAccent = Color(0xFFF355DA)

private val DarkColorScheme = darkColorScheme(
    primary = MetallicGold,
    secondary = AmberAccent,
    tertiary = VioletAccent,
    background = BgColor,
    surface = CardColor,
    onPrimary = BgColor,
    onSecondary = BgColor,
    onBackground = TextColor,
    onSurface = TextColor
)

@Composable
fun AiLatestFinderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // Always premium dark mode
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
