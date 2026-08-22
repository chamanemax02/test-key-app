package lk.sonora.app.model

data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val album: String = "",
    val durationText: String = "0:00",
    val durationMs: Long = 0L,
    val artworkUrl: String = "",
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
    val displayArtist: String
        get() = artist.ifBlank { "Unknown Artist" }

    val displayAlbum: String
        get() = album.ifBlank { "Single" }

    val playableUrl: String
        get() = when {
            localUri.isNotBlank() -> localUri
            previewUrl.isNotBlank() -> previewUrl
            audioUrl.isNotBlank() -> audioUrl
            else -> ""
        }
}
