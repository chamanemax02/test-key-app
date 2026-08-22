package com.sonoralk.app.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush

/**
 * Centralized color tokens. Never hard-code hex values in screens —
 * reference these instead so a re-theme touches one file.
 */
object ThemeColors {
    val BackgroundPrimary = Color(0xFF080808)
    val BackgroundSecondary = Color(0xFF111111)
    val CardBackground = Color(0xFF181818)

    val TextPrimary = Color(0xFFFFFFFF)
    val TextSecondary = Color(0xFFA7A7A7)

    // Original accent — deliberately not Spotify green.
    val AccentPurple = Color(0xFF8B5CF6)
    val AccentPink = Color(0xFFEC4899)
    val AccentBlue = Color(0xFF3B82F6)

    val AccentGradient = Brush.linearGradient(
        colors = listOf(AccentPurple, AccentPink, AccentBlue)
    )

    val Error = Color(0xFFFF5252)
    val Success = Color(0xFF4ADE80)
    val Divider = Color(0xFF262626)

    val LightBackgroundPrimary = Color(0xFFFFFFFF)
    val LightBackgroundSecondary = Color(0xFFF5F5F5)
    val LightCardBackground = Color(0xFFEFEFEF)
    val LightTextPrimary = Color(0xFF0A0A0A)
    val LightTextSecondary = Color(0xFF5C5C5C)
}
