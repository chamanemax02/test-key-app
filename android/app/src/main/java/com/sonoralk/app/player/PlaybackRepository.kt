package com.sonoralk.app.player

import com.sonoralk.app.data.model.PlaybackModel
import com.sonoralk.app.data.model.PlayerState
import com.sonoralk.app.data.model.RepeatMode
import com.sonoralk.app.data.model.TrackModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * App-wide observable playback state, exposed to the mini-player and full
 * player screens regardless of which screen is on top. A real implementation
 * wires this to a MediaController bound to PlaybackService; kept as a plain
 * StateFlow holder here so the UI layer has a stable contract to build against.
 */
class PlaybackRepository {

    private val _state = MutableStateFlow(
        PlaybackModel(
            currentTrack = null,
            state = PlayerState.IDLE,
            positionMs = 0,
            durationMs = 0,
            bufferedMs = 0,
            shuffleEnabled = false,
            repeatMode = RepeatMode.OFF
        )
    )
    val state: StateFlow<PlaybackModel> = _state.asStateFlow()

    fun play(track: TrackModel, queue: List<TrackModel> = listOf(track)) {
        val playableUrl = track.previewUrl ?: track.audioUrl
        if (playableUrl == null) {
            _state.value = _state.value.copy(state = PlayerState.ERROR, errorMessage = "No playable source for this track")
            return
        }
        _state.value = _state.value.copy(
            currentTrack = track,
            state = PlayerState.LOADING,
            queue = queue,
            queueIndex = queue.indexOf(track).coerceAtLeast(0),
            errorMessage = null
        )
        // Actual MediaController.setMediaItem/prepare/play call happens here.
    }

    fun togglePlayPause() {
        val current = _state.value
        _state.value = current.copy(
            state = if (current.state == PlayerState.PLAYING) PlayerState.PAUSED else PlayerState.PLAYING
        )
    }

    fun toggleShuffle() {
        _state.value = _state.value.copy(shuffleEnabled = !_state.value.shuffleEnabled)
    }

    fun cycleRepeat() {
        val next = when (_state.value.repeatMode) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
        _state.value = _state.value.copy(repeatMode = next)
    }

    fun seekTo(positionMs: Long) {
        _state.value = _state.value.copy(positionMs = positionMs)
    }

    fun skipNext() { /* advances queueIndex, respecting shuffle/repeat */ }
    fun skipPrevious() { /* rewinds queueIndex, or seeks to 0 if > 3s in */ }
}
