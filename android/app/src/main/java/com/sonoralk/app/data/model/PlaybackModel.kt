package com.sonoralk.app.data.model

enum class PlayerState { IDLE, LOADING, PLAYING, PAUSED, BUFFERING, COMPLETED, ERROR }
enum class RepeatMode { OFF, ONE, ALL }

data class PlaybackModel(
    val currentTrack: TrackModel?,
    val state: PlayerState,
    val positionMs: Long,
    val durationMs: Long,
    val bufferedMs: Long,
    val shuffleEnabled: Boolean,
    val repeatMode: RepeatMode,
    val queue: List<TrackModel> = emptyList(),
    val queueIndex: Int = -1,
    val errorMessage: String? = null
)
