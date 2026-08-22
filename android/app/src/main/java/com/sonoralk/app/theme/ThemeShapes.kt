package com.sonoralk.app.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

object ThemeShapes {
    val card = RoundedCornerShape(16.dp)
    val searchBar = RoundedCornerShape(28.dp)
    val chip = RoundedCornerShape(20.dp)
    val albumArt = RoundedCornerShape(12.dp)
    val albumArtLarge = RoundedCornerShape(20.dp)
    val bottomSheet = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)

    val material = Shapes(
        small = RoundedCornerShape(8.dp),
        medium = RoundedCornerShape(16.dp),
        large = RoundedCornerShape(24.dp)
    )
}
