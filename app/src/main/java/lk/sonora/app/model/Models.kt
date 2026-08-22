package lk.sonora.app.model

data class Artist(
    val id: String,
    val name: String,
    val imageUrl: String = "",
    val monthlyListeners: String = "",
    val bio: String = ""
)

data class Album(
    val id: String,
    val title: String,
    val artist: String,
    val artworkUrl: String = "",
    val releaseYear: String = "",
    val trackCount: Int = 0
)

data class Playlist(
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val artworkUrl: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val trackCount: Int = 0
)

data class SearchResult(
    val query: String,
    val tracks: List<Track> = emptyList(),
    val artists: List<Artist> = emptyList(),
    val albums: List<Album> = emptyList()
)

enum class PlayerStatus {
    IDLE,
    BUFFERING,
    PLAYING,
    PAUSED,
    COMPLETED,
    ERROR
}

enum class RepeatMode {
    OFF,
    ALL,
    ONE
}

data class PlaybackState(
    val currentTrack: Track? = null,
    val status: PlayerStatus = PlayerStatus.IDLE,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val bufferedPositionMs: Long = 0L,
    val isShuffleEnabled: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.OFF,
    val errorMessage: String? = null
) {
    val progress: Float
        get() = if (durationMs > 0) (currentPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f
}
