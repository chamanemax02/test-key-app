package lk.sonora.app.model

data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val album: String = "",
    val durationText: String = "0:00",
    val durationMs: Long = 0L,
    val artworkUrl: String = "",
    val youtubeUrl: String = "",
    val spotifyUrl: String = "",
    val previewUrl: String = "",
    val audioUrl: String = "",
    val localUri: String = "",
    val isLocal: Boolean = false,
    val isFavorite: Boolean = false,
    val downloadAllowed: Boolean = true,
    val releaseDate: String = "",
    val quality: String = "320kbps"
) {
    val cleanDisplayTitle: String
        get() {
            var t = title.trim()
            if (t.contains(" - ")) {
                val parts = t.split(" - ")
                if (parts.size >= 2 && parts[1].trim().isNotBlank()) {
                    t = parts[1].trim()
                }
            }
            return t.replace(Regex("(?i)\\[official.*?\\]|\\(official.*?\\)|\\(lyrics.*?\\)|\\[lyrics.*?\\]|\\(audio\\)|\\[audio\\]|\\(video\\)|\\[video\\]|\\|.*|\\b4k\\b|\\bhd\\b|\\bmv\\b"), "")
                .trim()
                .ifBlank { title }
        }

    val displayArtist: String
        get() = artist.ifBlank { "Unknown Artist" }

    val displayAlbum: String
        get() = album.ifBlank { "Single" }

    val playableUrl: String
        get() = when {
            localUri.isNotBlank() -> localUri
            audioUrl.isNotBlank() -> audioUrl
            previewUrl.isNotBlank() -> previewUrl
            else -> ""
        }

    val streamTargetUrl: String
        get() = when {
            youtubeUrl.isNotBlank() -> youtubeUrl
            spotifyUrl.isNotBlank() -> spotifyUrl
            else -> "https://www.youtube.com/watch?v=$id"
        }
}
