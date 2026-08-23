package lk.sonora.app.ui.screens.downloads

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import lk.sonora.app.R
import lk.sonora.app.SonoraApplication
import lk.sonora.app.model.Track
import lk.sonora.app.player.MusicPlayerManager
import lk.sonora.app.theme.BgCardElevated
import lk.sonora.app.theme.BgPrimary
import lk.sonora.app.theme.SpotifyGreen
import lk.sonora.app.theme.TextPrimary
import lk.sonora.app.theme.TextSecondary
import lk.sonora.app.ui.components.EmptyStateView
import lk.sonora.app.ui.components.TrackItemRow

@Composable
fun DownloadsScreen(
    onNavigateToTrack: (Track) -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as SonoraApplication
    val scope = rememberCoroutineScope()

    val downloadedTracks by app.localMusicRepository.getLocalTracks().collectAsState(initial = emptyList())
    var isScanning by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = stringResource(R.string.downloads_title),
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Text(
                    text = "${downloadedTracks.size} offline tracks available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }

            IconButton(
                onClick = {
                    scope.launch {
                        isScanning = true
                        app.localMusicRepository.scanDeviceAudio()
                        isScanning = false
                    }
                }
            ) {
                if (isScanning) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(22.dp),
                        strokeWidth = 2.dp,
                        color = SpotifyGreen
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Scan Downloads",
                        tint = SpotifyGreen
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (downloadedTracks.isEmpty()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 20.dp, bottom = 120.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                item {
                    EmptyStateView(message = stringResource(R.string.downloads_empty))
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            scope.launch {
                                isScanning = true
                                app.localMusicRepository.scanDeviceAudio()
                                isScanning = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SpotifyGreen)
                    ) {
                        Text(text = "Scan Downloaded Songs", color = androidx.compose.ui.graphics.Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 10.dp, bottom = 120.dp)
            ) {
                items(downloadedTracks) { track ->
                    TrackItemRow(
                        track = track,
                        onTrackClick = {
                            MusicPlayerManager.playTrack(track, downloadedTracks)
                            onNavigateToTrack(track)
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
