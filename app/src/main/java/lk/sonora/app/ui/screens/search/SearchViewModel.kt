package lk.sonora.app.ui.screens.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import lk.sonora.app.SonoraApplication
import lk.sonora.app.data.remote.ApiResult
import lk.sonora.app.model.Track

data class SearchUiState(
    val query: String = "",
    val searchResults: List<Track> = emptyList(),
    val searchHistory: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedTab: Int = 0 // 0: All, 1: Tracks, 2: Artists
)

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as SonoraApplication
    private val musicRepo = app.musicRepository

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun onQueryChange(newQuery: String) {
        _uiState.value = _uiState.value.copy(query = newQuery)

        searchJob?.cancel()
        if (newQuery.trim().length < 2) {
            _uiState.value = _uiState.value.copy(searchResults = emptyList(), isLoading = false, errorMessage = null)
            return
        }

        searchJob = viewModelScope.launch {
            delay(400) // 400ms Debounce
            executeSearch(newQuery.trim())
        }
    }

    fun executeSearch(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            addToHistory(query)

            when (val result = musicRepo.searchMusic(query)) {
                is ApiResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        searchResults = result.data,
                        isLoading = false,
                        errorMessage = null
                    )
                }
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        searchResults = emptyList(),
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is ApiResult.Loading -> {}
            }
        }
    }

    private fun addToHistory(query: String) {
        val history = _uiState.value.searchHistory.toMutableList()
        history.remove(query)
        history.add(0, query)
        if (history.size > 20) history.removeLast()
        _uiState.value = _uiState.value.copy(searchHistory = history)
    }

    fun clearHistory() {
        _uiState.value = _uiState.value.copy(searchHistory = emptyList())
    }

    fun selectTab(index: Int) {
        _uiState.value = _uiState.value.copy(selectedTab = index)
    }

    fun toggleFavorite(track: Track) {
        viewModelScope.launch {
            musicRepo.toggleFavorite(track)
        }
    }
}
