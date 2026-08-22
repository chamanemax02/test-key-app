package lk.sonora.app.ui.screens.library

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
import lk.sonora.app.model.Playlist
import lk.sonora.app.model.Track

data class LibraryUiState(
    val selectedTab: Int = 0, // 0: Favorites, 1: Playlists, 2: Local Music
    val isScanning: Boolean = false,
    val showCreatePlaylistDialog: Boolean = false
)

class LibraryViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as SonoraApplication
    private val musicRepo = app.musicRepository
    private val playlistRepo = app.playlistRepository
    private val localRepo = app.localMusicRepository

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    val favoriteTracks: StateFlow<List<Track>> = musicRepo.getFavoriteTracks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val playlists: StateFlow<List<Playlist>> = playlistRepo.getPlaylists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val localTracks: StateFlow<List<Track>> = localRepo.getLocalTracks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectTab(tabIndex: Int) {
        _uiState.value = _uiState.value.copy(selectedTab = tabIndex)
    }

    fun showCreateDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showCreatePlaylistDialog = show)
    }

    fun createPlaylist(name: String, description: String = "") {
        if (name.isBlank()) return
        viewModelScope.launch {
            playlistRepo.createPlaylist(name.trim(), description.trim())
            showCreateDialog(false)
        }
    }

    fun deletePlaylist(playlistId: Long) {
        viewModelScope.launch {
            playlistRepo.deletePlaylist(playlistId)
        }
    }

    fun scanLocalAudio() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isScanning = true)
            localRepo.scanDeviceAudio()
            _uiState.value = _uiState.value.copy(isScanning = false)
        }
    }

    fun toggleFavorite(track: Track) {
        viewModelScope.launch {
            musicRepo.toggleFavorite(track)
        }
    }
}
