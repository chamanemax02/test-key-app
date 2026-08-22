package lk.sonora.app.ui.screens.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import lk.sonora.app.R
import lk.sonora.app.model.Playlist
import lk.sonora.app.model.Track
import lk.sonora.app.player.MusicPlayerManager
import lk.sonora.app.theme.AccentPink
import lk.sonora.app.theme.AccentPurple
import lk.sonora.app.theme.BgCard
import lk.sonora.app.theme.BgCardElevated
import lk.sonora.app.theme.BgPrimary
import lk.sonora.app.theme.TextMuted
import lk.sonora.app.theme.TextPrimary
import lk.sonora.app.theme.TextSecondary
import lk.sonora.app.ui.components.EmptyStateView
import lk.sonora.app.ui.components.TrackItemRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel,
    onNavigateToTrack: (Track) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val favorites by viewModel.favoriteTracks.collectAsState()
    val playlists by viewModel.playlists.collectAsState()
    val localTracks by viewModel.localTracks.collectAsState()

    var newPlaylistName by remember { mutableStateOf("") }
    var newPlaylistDesc by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Title & Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.library_title),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )

            if (uiState.selectedTab == 1) {
                IconButton(onClick = { viewModel.showCreateDialog(true) }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New Playlist",
                        tint = AccentPurple,
                        modifier = Modifier.size(30.dp)
                    )
                }
            } else if (uiState.selectedTab == 2) {
                IconButton(onClick = { viewModel.scanLocalAudio() }) {
                    if (uiState.isScanning) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = AccentPink,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Scan Audio",
                            tint = AccentPurple
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Library Tabs (Favorites, Playlists, Local)
        val tabs = listOf(
            stringResource(R.string.library_tab_favorites),
            stringResource(R.string.library_tab_playlists),
            stringResource(R.string.library_tab_local)
        )
        TabRow(
            selectedTabIndex = uiState.selectedTab,
            containerColor = BgPrimary,
            contentColor = TextPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[uiState.selectedTab]),
                    color = AccentPurple,
                    height = 3.dp
                )
            }
        ) {
            tabs.forEachIndexed { index, tabTitle ->
                Tab(
                    selected = uiState.selectedTab == index,
                    onClick = { viewModel.selectTab(index) },
                    text = {
                        Text(
                            text = tabTitle,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = if (uiState.selectedTab == index) TextPrimary else TextSecondary
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tab Content
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 8.dp, bottom = 120.dp)
        ) {
            when (uiState.selectedTab) {
                0 -> { // Favorites
                    if (favorites.isEmpty()) {
                        item {
                            EmptyStateView(message = stringResource(R.string.library_empty_favorites))
                        }
                    } else {
                        items(favorites) { track ->
                            TrackItemRow(
                                track = track,
                                onTrackClick = {
                                    MusicPlayerManager.playTrack(track, favorites)
                                    onNavigateToTrack(track)
                                },
                                onFavoriteClick = { viewModel.toggleFavorite(track) }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
                1 -> { // Playlists
                    if (playlists.isEmpty()) {
                        item {
                            EmptyStateView(message = stringResource(R.string.library_empty_playlists))
                        }
                    } else {
                        items(playlists) { playlist ->
                            PlaylistCard(
                                playlist = playlist,
                                onClick = { /* View playlist details */ },
                                onDelete = { viewModel.deletePlaylist(playlist.id) }
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
                2 -> { // Local Music
                    if (localTracks.isEmpty()) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                EmptyStateView(message = stringResource(R.string.library_empty_local))
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { viewModel.scanLocalAudio() },
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Folder,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = stringResource(R.string.library_scan_device))
                                }
                            }
                        }
                    } else {
                        items(localTracks) { track ->
                            TrackItemRow(
                                track = track,
                                onTrackClick = {
                                    MusicPlayerManager.playTrack(track, localTracks)
                                    onNavigateToTrack(track)
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }

    // Create Playlist Dialog
    if (uiState.showCreatePlaylistDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showCreateDialog(false) },
            title = {
                Text(
                    text = stringResource(R.string.library_create_playlist),
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = newPlaylistName,
                        onValueChange = { newPlaylistName = it },
                        placeholder = { Text(text = stringResource(R.string.library_playlist_name), color = TextMuted) },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = BgCardElevated,
                            focusedBorderColor = AccentPurple,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.createPlaylist(newPlaylistName, newPlaylistDesc)
                        newPlaylistName = ""
                        newPlaylistDesc = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPurple)
                ) {
                    Text(text = stringResource(R.string.library_playlist_create))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showCreateDialog(false) }) {
                    Text(text = stringResource(R.string.library_playlist_cancel), color = TextSecondary)
                }
            },
            containerColor = BgCard
        )
    }
}

@Composable
fun PlaylistCard(
    playlist: Playlist,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = BgCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(BgCardElevated),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.QueueMusic,
                    contentDescription = null,
                    tint = AccentPink,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = playlist.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                Text(
                    text = "${playlist.trackCount} Tracks",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }
    }
}
