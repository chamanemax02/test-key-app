package com.sonoralk.app.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sonoralk.app.data.model.TrackModel
import com.sonoralk.app.theme.ThemeColors
import com.sonoralk.app.theme.ThemeShapes
import com.sonoralk.app.theme.ThemeSpacing

@Composable
fun TrackResultCard(track: TrackModel, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = ThemeSpacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(ThemeColors.CardBackground, ThemeShapes.albumArt)
        )
        Spacer(Modifier.width(ThemeSpacing.sm))
        Column(Modifier.weight(1f)) {
            Text(track.title, color = ThemeColors.TextPrimary, maxLines = 1)
            Text(
                listOfNotNull(track.artistName, track.albumName).joinToString(" • "),
                color = ThemeColors.TextSecondary, maxLines = 1
            )
        }
        // "More" opens: Play / Add to queue / Add to playlist / Favorite /
        // Share / Open original source — wired to a bottom sheet in a
        // follow-up pass once the action-sheet component exists.
        IconButton(onClick = { /* show action sheet */ }) {
            Icon(Icons.Default.MoreVert, contentDescription = "More", tint = ThemeColors.TextSecondary)
        }
    }
}
