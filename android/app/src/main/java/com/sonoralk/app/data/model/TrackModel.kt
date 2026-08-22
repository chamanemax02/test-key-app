package com.sonoralk.app.data.model

/**
 * Internal, stable representation of a track — decoupled from whatever
 * shape the upstream API returns. Every field is nullable except id/title
 * because we can never assume the source payload is complete.
 */
data class TrackModel(
    val id: String,
    val title: String,
    val artistName: String?,
    val artistId: String?,
    val albumName: String?,
    val albumId: String?,
    val albumImage: String?,
    val durationMs: Long?,
    val spotifyUrl: String?,
    // Preview-only clip URL (typically ~30s). This is the ONLY audio field
    // that may be safely played/cached for Spotify-sourced tracks.
    val previewUrl: String?,
    val isPlayable: Boolean,
    val releaseDate: String?,
    val explicit: Boolean,
    val source: TrackSource,
    // Full-length audio URL — populated ONLY for user-imported/local files
    // or content the backend has explicitly marked licensed. Never used
    // for raw Spotify catalog audio.
    val audioUrl: String?,
    val downloadAllowed: Boolean,
    val licenseStatus: LicenseStatus
)

enum class TrackSource { SPOTIFY_METADATA, LOCAL_FILE, USER_IMPORTED, UNKNOWN }

enum class LicenseStatus { LICENSED, PREVIEW_ONLY, USER_OWNED, UNKNOWN }
