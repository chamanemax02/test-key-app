package lk.sonora.app.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Spotify Brand Palette
val SpotifyGreen = Color(0xFF1DB954)
val SpotifyGreenLight = Color(0xFF1ED760)
val SpotifyGreenDark = Color(0xFF169C46)

// Surface & Background Colors
val BgPrimary = Color(0xFF121212)
val BgSecondary = Color(0xFF181818)
val BgCard = Color(0xFF181818)
val BgCardElevated = Color(0xFF242424)
val BgGlass = Color(0xE6181818)
val BgGlassBorder = Color(0x1FFFFFFF)

// Accent Colors
val AccentGreen = SpotifyGreen
val AccentGreenLight = SpotifyGreenLight
val AccentPurple = SpotifyGreen
val AccentPink = SpotifyGreenLight
val AccentBlue = SpotifyGreen
val AccentCyan = SpotifyGreenLight

// Text Colors
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFFB3B3B3)
val TextMuted = Color(0xFF727272)

// Semantic
val ColorFavorite = SpotifyGreen
val ColorSuccess = Color(0xFF1DB954)
val ColorError = Color(0xFFEF4444)
val ColorWarning = Color(0xFFF59E0B)

// Spotify Gradient Brushes
val SonoraGradient = Brush.horizontalGradient(
    colors = listOf(SpotifyGreen, SpotifyGreenLight)
)

val SonoraCardGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF1E2E22), Color(0xFF161E18), Color(0xFF121212))
)
