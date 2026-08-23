package lk.sonora.app.ui.screens.player

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import lk.sonora.app.R
import lk.sonora.app.model.PlayerStatus
import lk.sonora.app.model.RepeatMode
import lk.sonora.app.model.Track
import lk.sonora.app.player.MusicPlayerManager
import lk.sonora.app.theme.AccentPink
import lk.sonora.app.theme.AccentPurple
import lk.sonora.app.theme.BgCard
import lk.sonora.app.theme.BgCardElevated
import lk.sonora.app.theme.BgPrimary
import lk.sonora.app.theme.ColorFavorite
import lk.sonora.app.theme.SonoraGradient
import lk.sonora.app.theme.TextMuted
import lk.sonora.app.theme.TextPrimary
import lk.sonora.app.theme.TextSecondary
import lk.sonora.app.ui.components.EqualizerSheet
import lk.sonora.app.ui.components.TrackItemRow
import lk.sonora.app.ui.components.WaveformVisualizer

@Composable
fun FullPlayerScreen(
    viewModel: PlayerViewModel,
    onBackClick: () -> Unit
) {
    val state by MusicPlayerManager.playbackState.collectAsState()
    val queue by MusicPlayerManager.queue.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val track = state.currentTrack ?: return
    val context = LocalContext.current

    var sliderPosition by remember { mutableFloatStateOf(-1f) }
    var isEqualizerVisible by remember { mutableStateOf(false) }
    val isDragging = sliderPosition >= 0f

    val displayPosition = if (isDragging) {
        (sliderPosition * state.durationMs).toLong()
    } else {
        state.currentPositionMs
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF1A261C),
                        Color(0xFF121813),
                        BgPrimary
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar (Collapse Button + Now Playing Title + Sound Equalizer + Queue Button)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Collapse",
                        tint = TextPrimary,
                        modifier = Modifier.size(34.dp)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.player_now_playing).uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 2.sp),
                        color = TextSecondary
                    )
                    Text(
                        text = track.displayAlbum,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Sound Equalizer Button
                    IconButton(onClick = { isEqualizerVisible = true }) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = "Equalizer & Sound Effects",
                            tint = if (isEqualizerVisible) SpotifyGreen else TextPrimary,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    // Queue Button
                    IconButton(onClick = { viewModel.toggleQueueVisibility() }) {
                        Icon(
                            imageVector = Icons.Default.QueueMusic,
                            contentDescription = "Queue",
                            tint = if (uiState.isQueueVisible) SpotifyGreen else TextPrimary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Large Album Artwork
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .aspectRatio(1f)
                    .shadow(20.dp, shape = RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .background(BgCardElevated)
            ) {
                AsyncImage(
                    model = track.artworkUrl.ifBlank { R.drawable.ic_default_album_art },
                    contentDescription = track.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Title, Artist, and Favorite button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = track.title,
                        style = MaterialTheme.typography.headlineMedium.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = track.displayArtist,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(onClick = { viewModel.toggleFavorite(track) }) {
                    Icon(
                        imageVector = if (track.isFavorite || uiState.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (track.isFavorite || uiState.isFavorite) SpotifyGreen else TextSecondary,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            // Error banner if playback stream encountered an error
            if (state.status == PlayerStatus.ERROR && !state.errorMessage.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x33EF4444))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = state.errorMessage ?: "Playback error",
                        fontSize = 12.sp,
                        color = Color(0xFFEF4444),
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    IconButton(
                        onClick = { MusicPlayerManager.playTrack(track) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Retry",
                            tint = TextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Seekbar
            Slider(
                value = if (state.durationMs > 0) (displayPosition.toFloat() / state.durationMs.toFloat()).coerceIn(0f, 1f) else 0f,
                onValueChange = { sliderPosition = it },
                onValueChangeFinished = {
                    if (sliderPosition >= 0f) {
                        val seekTarget = (sliderPosition * state.durationMs).toLong()
                        MusicPlayerManager.seekTo(seekTarget)
                        sliderPosition = -1f
                    }
                },
                colors = SliderDefaults.colors(
                    thumbColor = TextPrimary,
                    activeTrackColor = SpotifyGreen,
                    inactiveTrackColor = Color(0x33FFFFFF)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // Time Labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatMs(displayPosition),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Text(
                    text = formatMs(state.durationMs),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Playback Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shuffle
                IconButton(onClick = { MusicPlayerManager.toggleShuffle() }) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = "Shuffle",
                        tint = if (state.isShuffleEnabled) SpotifyGreen else TextMuted,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Previous
                IconButton(onClick = { MusicPlayerManager.skipPrevious() }) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous",
                        tint = TextPrimary,
                        modifier = Modifier.size(38.dp)
                    )
                }

                // Play / Pause / Buffering
                if (state.status == PlayerStatus.BUFFERING) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(SpotifyGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color.Black,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                } else {
                    IconButton(
                        onClick = { MusicPlayerManager.togglePlayPause() },
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(SpotifyGreen)
                    ) {
                        Icon(
                            imageVector = if (state.status == PlayerStatus.PLAYING) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Play/Pause",
                            tint = Color.Black,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                // Next
                IconButton(onClick = { MusicPlayerManager.skipNext() }) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next",
                        tint = TextPrimary,
                        modifier = Modifier.size(38.dp)
                    )
                }

                // Repeat Mode
                IconButton(onClick = { MusicPlayerManager.cycleRepeatMode() }) {
                    Icon(
                        imageVector = when (state.repeatMode) {
                            RepeatMode.ONE -> Icons.Default.RepeatOne
                            RepeatMode.ALL -> Icons.Default.Repeat
                            RepeatMode.OFF -> Icons.Default.Repeat
                        },
                        contentDescription = "Repeat",
                        tint = if (state.repeatMode != RepeatMode.OFF) SpotifyGreen else TextMuted,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons: Download, Share, Open Spotify/YouTube
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Download Button
                IconButton(onClick = { viewModel.downloadTrack(track) }) {
                    if (uiState.isDownloading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = SpotifyGreen,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Download",
                            tint = TextSecondary
                        )
                    }
                }

                // Share Button
                IconButton(onClick = {
                    val shareUrl = if (track.youtubeUrl.isNotBlank()) track.youtubeUrl else track.spotifyUrl
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "🎵 Listen to '${track.title}' by ${track.artist} on SONORA LK!\n$shareUrl"
                        )
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share Track"))
                }) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = TextSecondary
                    )
                }

                // Open in YouTube/Spotify
                val extUrl = if (track.youtubeUrl.isNotBlank()) track.youtubeUrl else track.spotifyUrl
                if (extUrl.isNotBlank()) {
                    IconButton(onClick = {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(extUrl))
                        context.startActivity(browserIntent)
                    }) {
                        Icon(
                            imageVector = Icons.Default.OpenInBrowser,
                            contentDescription = "Open Stream",
                            tint = TextSecondary
                        )
                    }
                }
            }
        }

        // Equalizer & Sound Effects Sheet
        AnimatedVisibility(
            visible = isEqualizerVisible,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            EqualizerSheet(
                onDismiss = { isEqualizerVisible = false }
            )
        }

        // Queue Overlay Sheet
        AnimatedVisibility(
            visible = uiState.isQueueVisible,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(450.dp),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                colors = CardDefaults.cardColors(containerColor = BgCard)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.player_queue),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        IconButton(onClick = { viewModel.toggleQueueVisibility() }) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Close Queue",
                                tint = TextSecondary
                            )
                        }
                    }

                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(queue) { queueTrack ->
                            TrackItemRow(
                                track = queueTrack,
                                onTrackClick = {
                                    MusicPlayerManager.playTrack(queueTrack)
                                }
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }
                }
            }
        }
    }
}

fun formatMs(ms: Long): String {
    val totalSeconds = (ms / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%d:%02d", minutes, seconds)
}
