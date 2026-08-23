package lk.sonora.app.ui.screens.home

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import lk.sonora.app.R
import lk.sonora.app.model.Track
import lk.sonora.app.player.MusicPlayerManager
import lk.sonora.app.theme.AccentPurple
import lk.sonora.app.theme.BgCard
import lk.sonora.app.theme.BgCardElevated
import lk.sonora.app.theme.BgPrimary
import lk.sonora.app.theme.SonoraGradient
import lk.sonora.app.theme.TextMuted
import lk.sonora.app.theme.TextPrimary
import lk.sonora.app.theme.TextSecondary
import lk.sonora.app.ui.components.ErrorStateView
import lk.sonora.app.ui.components.SectionHeader
import lk.sonora.app.ui.components.TrackItemRow
import lk.sonora.app.ui.components.TrackShimmerItem

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToSearch: () -> Unit,
    onNavigateToTrack: (Track) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val recentlyPlayed by viewModel.recentlyPlayed.collectAsState()
    val localTracks by viewModel.localTracks.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 20.dp, bottom = 120.dp)
    ) {
        // Header & Greeting
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = uiState.greeting,
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Text(
                        text = stringResource(R.string.app_tagline),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(BgCardElevated)
                ) {
                    AsyncImage(
                        model = R.drawable.app_logo,
                        contentDescription = "Logo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
        }

        // Search Bar (Interactive Trigger)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToSearch() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BgCard)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = SpotifyGreen
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.home_search_placeholder),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Recently Played Carousel (if available)
        if (recentlyPlayed.isNotEmpty()) {
            item {
                SectionHeader(title = stringResource(R.string.home_recently_played))
            }
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    items(recentlyPlayed) { track ->
                        HomeSquareCard(
                            track = track,
                            onClick = {
                                MusicPlayerManager.playTrack(track, recentlyPlayed)
                                onNavigateToTrack(track)
                            }
                        )
                    }
                }
            }
        }

        // Loading Skeleton or Error
        if (uiState.isLoading) {
            item {
                SectionHeader(title = stringResource(R.string.home_trending))
            }
            items(4) {
                TrackShimmerItem()
            }
        } else if (uiState.errorMessage != null && uiState.trendingTracks.isEmpty()) {
            item {
                ErrorStateView(
                    errorMessage = uiState.errorMessage ?: "Failed to load music",
                    onRetry = { viewModel.loadHomeContent() }
                )
            }
        }

        // Trending Tracks Section
        if (uiState.trendingTracks.isNotEmpty()) {
            item {
                SectionHeader(title = stringResource(R.string.home_trending))
            }
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    items(uiState.trendingTracks) { track ->
                        HomeSquareCard(
                            track = track,
                            onClick = {
                                MusicPlayerManager.playTrack(track, uiState.trendingTracks)
                                onNavigateToTrack(track)
                            }
                        )
                    }
                }
            }
        }

        // Made For You / Recommended
        if (uiState.madeForYouTracks.isNotEmpty()) {
            item {
                SectionHeader(title = stringResource(R.string.home_made_for_you))
            }
            items(uiState.madeForYouTracks.take(6)) { track ->
                TrackItemRow(
                    track = track,
                    onTrackClick = {
                        MusicPlayerManager.playTrack(track, uiState.madeForYouTracks)
                        onNavigateToTrack(track)
                    },
                    onFavoriteClick = { viewModel.toggleFavorite(track) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Local Device Music Section
        if (localTracks.isNotEmpty()) {
            item {
                SectionHeader(title = stringResource(R.string.home_local_music))
            }
            items(localTracks.take(5)) { track ->
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

@Composable
fun HomeSquareCard(
    track: Track,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BgCard)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BgCardElevated)
            ) {
                AsyncImage(
                    model = track.artworkUrl.ifBlank { R.drawable.ic_default_album_art },
                    contentDescription = track.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Small play icon overlay
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(6.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(SpotifyGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = androidx.compose.ui.graphics.Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = track.title,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = track.displayArtist,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
