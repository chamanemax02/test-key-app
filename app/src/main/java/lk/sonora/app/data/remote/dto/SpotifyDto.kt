package lk.sonora.app.data.remote.dto

import com.google.gson.annotations.SerializedName
import lk.sonora.app.model.Track

// Search Response
data class SearchResponseDto(
    @SerializedName("status") val status: Boolean = false,
    @SerializedName("query") val query: String? = null,
    @SerializedName("result") val result: List<SearchItemDto>? = null,
    @SerializedName("detail") val detail: String? = null
)

data class SearchItemDto(
    @SerializedName("title") val title: String? = null,
    @SerializedName("artist") val artist: String? = null,
    @SerializedName("duration") val duration: String? = null,
    @SerializedName("thumbnail") val thumbnail: String? = null,
    @SerializedName("spotify_url") val spotifyUrl: String? = null,
    @SerializedName("youtube_url") val youtubeUrl: String? = null,
    @SerializedName("video_id") val videoId: String? = null
) {
    fun toTrack(): Track {
        val safeTitle = title?.trim() ?: "Unknown Title"
        val safeArtist = artist?.trim() ?: "Unknown Artist"
        val id = videoId ?: spotifyUrl?.substringAfterLast("/") ?: safeTitle.hashCode().toString()

        return Track(
            id = id,
            title = safeTitle,
            artist = safeArtist,
            album = "",
            durationText = duration ?: "0:00",
            durationMs = parseDurationToMs(duration),
            artworkUrl = thumbnail ?: "",
            spotifyUrl = spotifyUrl ?: "",
            previewUrl = "",
            audioUrl = "",
            localUri = "",
            isLocal = false
        )
    }
}

// Track Metadata Response
data class TrackResponseDto(
    @SerializedName("status") val status: Boolean = false,
    @SerializedName("result") val result: TrackDetailDto? = null,
    @SerializedName("detail") val detail: String? = null
)

data class TrackDetailDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("artist") val artist: String? = null,
    @SerializedName("album") val album: String? = null,
    @SerializedName("duration") val duration: String? = null,
    @SerializedName("duration_ms") val durationMs: Long? = null,
    @SerializedName("release_date") val releaseDate: String? = null,
    @SerializedName("thumbnail") val thumbnail: String? = null,
    @SerializedName("spotify_url") val spotifyUrl: String? = null,
    @SerializedName("preview_url") val previewUrl: String? = null
) {
    fun toTrack(): Track {
        return Track(
            id = id ?: spotifyUrl?.substringAfterLast("/") ?: (title ?: "").hashCode().toString(),
            title = title?.trim() ?: "Unknown Title",
            artist = artist?.trim() ?: "Unknown Artist",
            album = album?.trim() ?: "",
            durationText = duration ?: "0:00",
            durationMs = durationMs ?: parseDurationToMs(duration),
            artworkUrl = thumbnail ?: "",
            spotifyUrl = spotifyUrl ?: "",
            previewUrl = previewUrl ?: "",
            audioUrl = "",
            localUri = "",
            isLocal = false,
            releaseDate = releaseDate ?: ""
        )
    }
}

// Download Response
data class DownloadResponseDto(
    @SerializedName("status") val status: Boolean = false,
    @SerializedName("result") val result: DownloadDetailDto? = null,
    @SerializedName("detail") val detail: String? = null
)

data class DownloadDetailDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("artist") val artist: String? = null,
    @SerializedName("duration") val duration: String? = null,
    @SerializedName("release_date") val releaseDate: String? = null,
    @SerializedName("thumbnail") val thumbnail: String? = null,
    @SerializedName("spotify_url") val spotifyUrl: String? = null,
    @SerializedName("preview_url") val previewUrl: String? = null,
    @SerializedName("quality") val quality: String? = null,
    @SerializedName("download_url") val downloadUrl: String? = null,
    @SerializedName("filename") val filename: String? = null
)

fun parseDurationToMs(duration: String?): Long {
    if (duration.isNullOrBlank()) return 0L
    val parts = duration.split(":")
    return when (parts.size) {
        2 -> {
            val min = parts[0].toLongOrNull() ?: 0L
            val sec = parts[1].toLongOrNull() ?: 0L
            ((min * 60) + sec) * 1000L
        }
        3 -> {
            val hr = parts[0].toLongOrNull() ?: 0L
            val min = parts[1].toLongOrNull() ?: 0L
            val sec = parts[2].toLongOrNull() ?: 0L
            ((hr * 3600) + (min * 60) + sec) * 1000L
        }
        else -> 0L
    }
}
