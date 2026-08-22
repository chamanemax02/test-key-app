package com.sonoralk.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sonoralk.app.theme.ThemeColors
import com.sonoralk.app.theme.ThemeShapes
import com.sonoralk.app.theme.ThemeSpacing

/** Single carousel item — album art placeholder + title, used across every
 *  horizontal-scroll section on Home. Real image loading wires in via Coil
 *  once carousel data models are passed instead of a bare index. */
@Composable
fun HomeCarouselCard(index: Int) {
    Column(modifier = Modifier.width(ThemeSpacing.albumArtCarousel)) {
        Box(
            modifier = Modifier
                .size(ThemeSpacing.albumArtCarousel)
                .background(ThemeColors.CardBackground, ThemeShapes.albumArt)
        )
        Spacer(Modifier.height(ThemeSpacing.xs))
        Text("Track $index", color = ThemeColors.TextPrimary, maxLines = 1)
    }
}

@Composable
fun EmptySectionPlaceholder(sectionTitle: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(ThemeColors.CardBackground, ThemeShapes.card),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Text("Nothing here yet", color = ThemeColors.TextSecondary)
    }
}
