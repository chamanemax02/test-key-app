package lk.sonora.app.ui.screens.search

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
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
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import lk.sonora.app.R
import lk.sonora.app.model.Track
import lk.sonora.app.player.MusicPlayerManager
import lk.sonora.app.theme.AccentPink
import lk.sonora.app.theme.AccentPurple
import lk.sonora.app.theme.BgCard
import lk.sonora.app.theme.BgPrimary
import lk.sonora.app.theme.TextMuted
import lk.sonora.app.theme.TextPrimary
import lk.sonora.app.theme.TextSecondary
import lk.sonora.app.ui.components.EmptyStateView
import lk.sonora.app.ui.components.ErrorStateView
import lk.sonora.app.ui.components.SectionHeader
import lk.sonora.app.ui.components.TrackItemRow
import lk.sonora.app.ui.components.TrackShimmerItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onNavigateToTrack: (Track) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Search Input Bar
        OutlinedTextField(
            value = uiState.query,
            onValueChange = { viewModel.onQueryChange(it) },
            placeholder = {
                Text(
                    text = stringResource(R.string.home_search_placeholder),
                    color = TextMuted,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = SpotifyGreen
                )
            },
            trailingIcon = {
                if (uiState.query.isNotEmpty()) {
                    IconButton(onClick = { viewModel.onQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = TextSecondary
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = BgCard,
                focusedBorderColor = SpotifyGreen,
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Category Tabs
        val tabs = listOf(
            stringResource(R.string.search_tab_all),
            stringResource(R.string.search_tab_tracks),
            stringResource(R.string.search_tab_artists)
        )
        TabRow(
            selectedTabIndex = uiState.selectedTab,
            containerColor = BgPrimary,
            contentColor = TextPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[uiState.selectedTab]),
                    color = SpotifyGreen,
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

        Spacer(modifier = Modifier.height(8.dp))

        // Search Results / History / Shimmer / Empty State
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 10.dp, bottom = 120.dp)
        ) {
            // Search History when query is empty
            if (uiState.query.isBlank()) {
                if (uiState.searchHistory.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = stringResource(R.string.search_recent_searches),
                            onSeeAllClick = { viewModel.clearHistory() }
                        )
                    }
                    item {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(vertical = 6.dp)
                        ) {
                            items(uiState.searchHistory) { historyItem ->
                                AssistChip(
                                    onClick = {
                                        viewModel.onQueryChange(historyItem)
                                        viewModel.executeSearch(historyItem)
                                    },
                                    label = { Text(text = historyItem, color = TextPrimary) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.History,
                                            contentDescription = null,
                                            tint = SpotifyGreen
                                        )
                                    },
                                    colors = AssistChipDefaults.assistChipColors(containerColor = BgCard),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                        }
                    }
                } else {
                    item {
                        EmptyStateView(message = stringResource(R.string.search_empty_prompt))
                    }
                }
            } else if (uiState.isLoading) {
                items(6) {
                    TrackShimmerItem()
                }
            } else if (uiState.errorMessage != null) {
                item {
                    ErrorStateView(
                        errorMessage = uiState.errorMessage ?: "Error searching tracks",
                        onRetry = { viewModel.executeSearch(uiState.query) }
                    )
                }
            } else if (uiState.searchResults.isEmpty()) {
                item {
                    EmptyStateView(message = stringResource(R.string.search_no_results))
                }
            } else {
                items(uiState.searchResults) { track ->
                    TrackItemRow(
                        track = track,
                        onTrackClick = {
                            MusicPlayerManager.playTrack(track, uiState.searchResults)
                            onNavigateToTrack(track)
                        },
                        onFavoriteClick = { viewModel.toggleFavorite(track) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
