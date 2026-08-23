package lk.sonora.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import lk.sonora.app.R
import lk.sonora.app.model.PlayerStatus
import lk.sonora.app.player.MusicPlayerManager
import lk.sonora.app.theme.*

@Composable
fun MiniPlayer(
    onExpandClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by MusicPlayerManager.playbackState.collectAsState()
    val track = state.currentTrack ?: return

    var dragOffset by remember { mutableFloatStateOf(0f) }
    val draggableState = rememberDraggableState { delta ->
        dragOffset += delta
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable { onExpandClick() }
            .draggable(
                state = draggableState,
                orientation = Orientation.Horizontal,
                onDragStopped = {
                    if (dragOffset > 100) {
                        MusicPlayerManager.skipPrevious()
                    } else if (dragOffset < -100) {
                        MusicPlayerManager.skipNext()
                    }
                    dragOffset = 0f
                }
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BgGlass),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Playback progress bar on top edge of mini player
            LinearProgressIndicator(
                progress = { state.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.5.dp),
                color = SpotifyGreen,
                trackColor = Color(0x33FFFFFF)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Album artwork
                AsyncImage(
                    model = track.artworkUrl.ifBlank { R.drawable.ic_default_album_art },
                    contentDescription = track.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(BgCardElevated)
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Title & Artist
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = track.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = track.displayArtist,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Waveform or controls
                if (state.status == PlayerStatus.BUFFERING) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = SpotifyGreen,
                        strokeWidth = 2.5.dp
                    )
                } else {
                    IconButton(
                        onClick = { MusicPlayerManager.togglePlayPause() },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(SpotifyGreen)
                    ) {
                        Icon(
                            imageVector = if (state.status == PlayerStatus.PLAYING) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Play/Pause",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                IconButton(onClick = { MusicPlayerManager.skipNext() }) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next",
                        tint = TextSecondary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}
