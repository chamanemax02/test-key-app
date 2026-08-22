package com.sonoralk.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sonoralk.app.theme.ThemeColors
import com.sonoralk.app.theme.ThemeShapes
import com.sonoralk.app.theme.ThemeSpacing

@Composable
fun HomeSearchBar(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ThemeColors.CardBackground, ThemeShapes.searchBar)
            .clickable(onClick = onClick)
            .padding(horizontal = ThemeSpacing.md, vertical = ThemeSpacing.sm + 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Search, contentDescription = null, tint = ThemeColors.TextSecondary)
        Spacer(Modifier.width(ThemeSpacing.sm))
        // Placeholder text is Sinhala per spec: "Search for a song, artist, or album..."
        Text("ගීතයක්, artist කෙනෙක් හෝ album එකක් සොයන්න...", color = ThemeColors.TextSecondary)
    }
}
