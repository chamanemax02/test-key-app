package lk.sonora.app.ui.screens.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import lk.sonora.app.SonoraApplication
import lk.sonora.app.data.remote.ApiResult
import lk.sonora.app.model.Track
import java.util.Calendar

data class HomeUiState(
    val greeting: String = "Good Morning",
    val trendingTracks: List<Track> = emptyList(),
    val madeForYouTracks: List<Track> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as SonoraApplication
    private val musicRepo = app.musicRepository
    private val localRepo = app.localMusicRepository

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val recentlyPlayed: StateFlow<List<Track>> = musicRepo.getRecentlyPlayed()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val localTracks: StateFlow<List<Track>> = localRepo.getLocalTracks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        updateGreeting()
        loadHomeContent()
    }

    private fun updateGreeting() {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greeting = when (hour) {
            in 4..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            else -> "Good Evening"
        }
        _uiState.value = _uiState.value.copy(greeting = greeting)
    }

    fun loadHomeContent() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            // Search popular & trending Sinhala/English artists for discovery
            val trendingRes = musicRepo.searchMusic("Trending Sri Lanka")
            val madeForYouRes = musicRepo.searchMusic("Sinhala Acoustic Hits")

            val trending = if (trendingRes is ApiResult.Success) trendingRes.data else emptyList()
            val madeForYou = if (madeForYouRes is ApiResult.Success) madeForYouRes.data else emptyList()

            _uiState.value = _uiState.value.copy(
                trendingTracks = trending,
                madeForYouTracks = madeForYou,
                isLoading = false,
                errorMessage = if (trending.isEmpty() && madeForYou.isEmpty() && trendingRes is ApiResult.Error) {
                    trendingRes.message
                } else null
            )
        }
    }

    fun toggleFavorite(track: Track) {
        viewModelScope.launch {
            musicRepo.toggleFavorite(track)
        }
    }
}
