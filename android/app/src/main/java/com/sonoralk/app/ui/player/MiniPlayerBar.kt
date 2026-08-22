package com.sonoralk.app.ui.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sonoralk.app.data.model.PlayerState
import com.sonoralk.app.player.PlaybackRepository
import com.sonoralk.app.theme.ThemeColors
import com.sonoralk.app.theme.ThemeShapes
import com.sonoralk.app.theme.ThemeSpacing

/**
 * Persistent bar pinned to the bottom of every main screen. Tapping it
 * expands to FullPlayerScreen; play/pause works directly from here without
 * navigating away — this is the "control from anywhere" requirement.
 */
@Composable
fun MiniPlayerBar(
    playbackRepository: PlaybackRepository,
    onExpand: () -> Unit
) {
    val playback by playbackRepository.state.collectAsState()

    AnimatedVisibility(visible = playback.currentTrack != null) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(ThemeSpacing.miniPlayerHeight)
                .background(ThemeColors.CardBackground, ThemeShapes.card)
                .clickable(onClick = onExpand)
                .padding(horizontal = ThemeSpacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(44.dp)
                    .background(ThemeColors.BackgroundSecondary, ThemeShapes.albumArt)
            )
            Spacer(Modifier.width(ThemeSpacing.sm))
            Column(Modifier.weight(1f)) {
                Text(playback.currentTrack?.title ?: "", color = ThemeColors.TextPrimary, maxLines = 1)
                Text(playback.currentTrack?.artistName ?: "", color = ThemeColors.TextSecondary, maxLines = 1)
            }
            IconButton(onClick = { playbackRepository.togglePlayPause() }) {
                Icon(
                    imageVector = if (playback.state == PlayerState.PLAYING) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Play/Pause",
                    tint = ThemeColors.TextPrimary
                )
            }
        }
    }
}
