package lk.sonora.app.ui.screens.player

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import lk.sonora.app.SonoraApplication
import lk.sonora.app.data.remote.ApiResult
import lk.sonora.app.model.Track
import lk.sonora.app.player.DownloadManagerHelper

data class PlayerUiState(
    val isDownloading: Boolean = false,
    val isFavorite: Boolean = false,
    val isQueueVisible: Boolean = false
)

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as SonoraApplication
    private val musicRepo = app.musicRepository

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    fun toggleFavorite(track: Track) {
        viewModelScope.launch {
            musicRepo.toggleFavorite(track)
            _uiState.value = _uiState.value.copy(isFavorite = !track.isFavorite)
        }
    }

    fun downloadTrack(track: Track) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isDownloading = true)
            Toast.makeText(app, "Preparing high quality download...", Toast.LENGTH_SHORT).show()

            when (val result = musicRepo.getDownloadUrl(track)) {
                is ApiResult.Success -> {
                    DownloadManagerHelper.downloadTrack(app, track, result.data)
                    Toast.makeText(app, "Download started: ${track.title}", Toast.LENGTH_LONG).show()
                    _uiState.value = _uiState.value.copy(isDownloading = false)
                }
                is ApiResult.Error -> {
                    Toast.makeText(app, "Download failed: ${result.message}", Toast.LENGTH_LONG).show()
                    _uiState.value = _uiState.value.copy(isDownloading = false)
                }
                is ApiResult.Loading -> {}
            }
        }
    }

    fun toggleQueueVisibility() {
        _uiState.value = _uiState.value.copy(isQueueVisible = !_uiState.value.isQueueVisible)
    }
}
