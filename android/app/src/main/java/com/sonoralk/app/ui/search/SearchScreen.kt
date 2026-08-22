package com.sonoralk.app.ui.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sonoralk.app.data.model.SearchResultModel
import com.sonoralk.app.data.model.TrackModel
import com.sonoralk.app.data.repository.MusicRepository
import com.sonoralk.app.data.repository.Result
import com.sonoralk.app.theme.ThemeColors
import com.sonoralk.app.theme.ThemeSpacing
import kotlinx.coroutines.delay

private const val DEBOUNCE_MS = 400L

@Composable
fun SearchScreen(
    repository: MusicRepository,
    onTrackClick: (TrackModel) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<SearchResultModel?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Debounce: wait for the user to pause typing before firing a request,
    // rather than issuing one network call per keystroke.
    LaunchedEffect(query) {
        if (query.isBlank()) {
            result = null; errorMessage = null; return@LaunchedEffect
        }
        isLoading = true
        delay(DEBOUNCE_MS)
        when (val r = repository.search(query)) {
            is Result.Success -> { result = r.data; errorMessage = null }
            is Result.Error -> { errorMessage = r.message }
            Result.Loading -> {}
        }
        isLoading = false
    }

    Column(Modifier.fillMaxSize().padding(ThemeSpacing.screenPadding)) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("ගීතයක්, artist කෙනෙක් හෝ album එකක් සොයන්න...") },
            singleLine = true,
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { query = "" }) {
                        Icon(androidx.compose.material.icons.Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            }
        )

        Spacer(Modifier.height(ThemeSpacing.md))

        when {
            isLoading -> SearchSkeletonList()
            errorMessage != null -> SearchErrorState(
                message = errorMessage!!,
                onRetry = { val q = query; query = ""; query = q }
            )
            result != null && result!!.tracks.isEmpty() && result!!.artists.isEmpty() && result!!.albums.isEmpty() -> {
                SearchEmptyState()
            }
            result != null -> SearchResultsList(result!!, onTrackClick)
        }
    }
}

@Composable
private fun SearchResultsList(result: SearchResultModel, onTrackClick: (TrackModel) -> Unit) {
    LazyColumn {
        if (result.tracks.isNotEmpty()) {
            item { SectionLabel("Tracks") }
            items(result.tracks) { track -> TrackResultCard(track, onClick = { onTrackClick(track) }) }
        }
        if (result.artists.isNotEmpty()) {
            item { SectionLabel("Artists") }
            items(result.artists) { artist -> Text(artist.name, color = ThemeColors.TextPrimary) }
        }
        if (result.albums.isNotEmpty()) {
            item { SectionLabel("Albums") }
            items(result.albums) { album -> Text(album.name, color = ThemeColors.TextPrimary) }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, color = ThemeColors.TextSecondary, modifier = Modifier.padding(vertical = ThemeSpacing.sm))
}

@Composable
private fun SearchEmptyState() {
    Column(Modifier.fillMaxWidth().padding(top = ThemeSpacing.xl), horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
        Text("ඔබ සෙවූ ගීතය හමු නොවීය.", color = ThemeColors.TextSecondary)
    }
}

@Composable
private fun SearchErrorState(message: String, onRetry: () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(top = ThemeSpacing.xl), horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
        Text(message, color = ThemeColors.Error)
        Spacer(Modifier.height(ThemeSpacing.sm))
        TextButton(onClick = onRetry) { Text("නැවත උත්සාහ කරන්න") }
    }
}

@Composable
private fun SearchSkeletonList() {
    Column {
        repeat(6) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(vertical = 4.dp)
                    .background(ThemeColors.CardBackground, com.sonoralk.app.theme.ThemeShapes.card)
            )
        }
    }
}
