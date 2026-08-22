package lk.sonora.app.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val BgPrimary = Color(0xFF080808)
val BgSecondary = Color(0xFF111111)
val BgCard = Color(0xFF181818)
val BgCardElevated = Color(0xFF222222)
val BgGlass = Color(0xCC181818)
val BgGlassBorder = Color(0x33FFFFFF)

// Accent Colors
val AccentPurple = Color(0xFF8B5CF6)
val AccentPink = Color(0xFFEC4899)
val AccentBlue = Color(0xFF3B82F6)
val AccentCyan = Color(0xFF06B6D4)

// Text Colors
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFFA7A7A7)
val TextMuted = Color(0xFF666666)

// Semantic
val ColorFavorite = Color(0xFFF43F5E)
val ColorSuccess = Color(0xFF10B981)
val ColorError = Color(0xFFEF4444)
val ColorWarning = Color(0xFFF59E0B)

// Sonora Gradient Brushes
val SonoraGradient = Brush.horizontalGradient(
    colors = listOf(AccentPurple, AccentPink, AccentBlue)
)

val SonoraCardGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF1C1330), Color(0xFF13111E), Color(0xFF0D0D0D))
)
