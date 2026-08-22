package com.sonoralk.app.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sonoralk.app.data.model.PlayerState
import com.sonoralk.app.data.model.RepeatMode
import com.sonoralk.app.player.PlaybackRepository
import com.sonoralk.app.theme.ThemeColors
import com.sonoralk.app.theme.ThemeShapes
import com.sonoralk.app.theme.ThemeSpacing

@Composable
fun FullPlayerScreen(playbackRepository: PlaybackRepository, onCollapse: () -> Unit) {
    val playback by playbackRepository.state.collectAsState()
    val track = playback.currentTrack

    Column(
        Modifier
            .fillMaxSize()
            .background(ThemeColors.BackgroundPrimary)
            .padding(ThemeSpacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(ThemeSpacing.lg))
        Box(
            Modifier
                .size(ThemeSpacing.albumArtLarge)
                .background(ThemeColors.CardBackground, ThemeShapes.albumArtLarge)
        )
        Spacer(Modifier.height(ThemeSpacing.lg))
        Text(track?.title ?: "Nothing playing", style = MaterialTheme.typography.headlineMedium, color = ThemeColors.TextPrimary)
        Text(track?.artistName ?: "", color = ThemeColors.TextSecondary)

        Spacer(Modifier.height(ThemeSpacing.lg))
        Slider(
            value = playback.positionMs.toFloat(),
            valueRange = 0f..(playback.durationMs.coerceAtLeast(1L)).toFloat(),
            onValueChange = { playbackRepository.seekTo(it.toLong()) }
        )

        Row(
            Modifier.fillMaxWidth().padding(top = ThemeSpacing.md),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { playbackRepository.toggleShuffle() }) {
                Icon(Icons.Default.Shuffle, contentDescription = "Shuffle",
                    tint = if (playback.shuffleEnabled) ThemeColors.AccentPurple else ThemeColors.TextSecondary)
            }
            IconButton(onClick = { playbackRepository.skipPrevious() }) {
                Icon(Icons.Default.SkipPrevious, contentDescription = "Previous", tint = ThemeColors.TextPrimary)
            }
            IconButton(onClick = { playbackRepository.togglePlayPause() }, modifier = Modifier.size(64.dp)) {
                Icon(
                    imageVector = if (playback.state == PlayerState.PLAYING) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                    contentDescription = "Play/Pause", tint = ThemeColors.TextPrimary,
                    modifier = Modifier.size(56.dp)
                )
            }
            IconButton(onClick = { playbackRepository.skipNext() }) {
                Icon(Icons.Default.SkipNext, contentDescription = "Next", tint = ThemeColors.TextPrimary)
            }
            IconButton(onClick = { playbackRepository.cycleRepeat() }) {
                Icon(
                    imageVector = if (playback.repeatMode == RepeatMode.ONE) Icons.Default.RepeatOne else Icons.Default.Repeat,
                    contentDescription = "Repeat",
                    tint = if (playback.repeatMode != RepeatMode.OFF) ThemeColors.AccentPurple else ThemeColors.TextSecondary
                )
            }
        }

        if (track != null && track.licenseStatus.name == "PREVIEW_ONLY") {
            Spacer(Modifier.height(ThemeSpacing.sm))
            Text("Preview only — full track on Spotify", color = ThemeColors.TextSecondary, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
