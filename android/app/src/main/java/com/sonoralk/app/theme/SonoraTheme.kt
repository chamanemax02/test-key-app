package com.sonoralk.app.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
fun SonoraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            background = ThemeColors.BackgroundPrimary,
            surface = ThemeColors.CardBackground,
            onBackground = ThemeColors.TextPrimary,
            onSurface = ThemeColors.TextPrimary,
            error = ThemeColors.Error
        )
    } else {
        lightColorScheme(
            background = ThemeColors.LightBackgroundPrimary,
            surface = ThemeColors.LightCardBackground,
            onBackground = ThemeColors.LightTextPrimary,
            onSurface = ThemeColors.LightTextPrimary,
            error = ThemeColors.Error
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ThemeTypography.AppTypography,
        shapes = ThemeShapes.material,
        content = content
    )
}
