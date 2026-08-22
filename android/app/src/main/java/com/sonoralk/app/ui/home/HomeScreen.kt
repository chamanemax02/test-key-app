package com.sonoralk.app.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sonoralk.app.data.model.TrackModel
import com.sonoralk.app.data.repository.MusicRepository
import com.sonoralk.app.theme.ThemeSpacing
import com.sonoralk.app.theme.ThemeTypography
import java.time.LocalTime

@Composable
fun HomeScreen(
    repository: MusicRepository,
    onTrackClick: (TrackModel) -> Unit,
    onNavigate: (String) -> Unit
) {
    val greeting = remember { greetingForCurrentTime() }
    val recentlyPlayed by repository.recentlyPlayed().collectAsState(initial = emptyList())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = ThemeSpacing.screenPadding),
        contentPadding = PaddingValues(bottom = ThemeSpacing.miniPlayerHeight + ThemeSpacing.lg)
    ) {
        item {
            Spacer(Modifier.height(ThemeSpacing.lg))
            Text(greeting, style = ThemeTypography.AppTypography.headlineLarge)
            Spacer(Modifier.height(ThemeSpacing.md))
        }
        item { HomeSearchBar(onClick = { onNavigate("search") }) }
        item { Spacer(Modifier.height(ThemeSpacing.lg)) }

        item { HomeSection(title = "Recently Played", items = recentlyPlayed.size) }
        item { HomeSection(title = "Made For You", items = 8) }
        item { HomeSection(title = "Trending Now", items = 10) }
        item { HomeSection(title = "Popular Artists", items = 8) }
        item { HomeSection(title = "Your Playlists", items = 6) }
        item { HomeSection(title = "Local Music", items = 0) }
        item { HomeSection(title = "Recommended", items = 10) }
    }
}

private fun greetingForCurrentTime(): String {
    // Sri Lanka local time (Asia/Colombo, UTC+5:30) drives the greeting.
    val colomboHour = java.time.ZonedDateTime.now(java.time.ZoneId.of("Asia/Colombo")).hour
    return when (colomboHour) {
        in 5..11 -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        else -> "Good Evening"
    }
}

@Composable
private fun HomeSection(title: String, items: Int) {
    Column(Modifier.padding(bottom = ThemeSpacing.lg)) {
        Text(title, style = ThemeTypography.AppTypography.titleLarge)
        Spacer(Modifier.height(ThemeSpacing.sm))
        if (items == 0) {
            EmptySectionPlaceholder(title)
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(ThemeSpacing.sm)) {
                items(items) { index -> HomeCarouselCard(index) }
            }
        }
    }
}
