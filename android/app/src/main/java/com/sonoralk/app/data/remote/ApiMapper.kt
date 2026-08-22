package com.sonoralk.app.data.remote

import com.sonoralk.app.data.model.*

/**
 * Sole boundary between "whatever the API sent us" and the app's internal
 * models. Every field access is null-safe; nothing here assumes the
 * upstream contract is stable. This is also the ONLY place that decides
 * whether audio is safe to expose for playback/download.
 */
object ApiMapper {

    fun toTrackModel(dto: TrackResponseDto?): TrackModel? {
        if (dto == null || dto.id == null || dto.name == null) return null

        val licenseStatus = when (dto.licenseStatus?.lowercase()) {
            "licensed" -> LicenseStatus.LICENSED
            "preview_only" -> LicenseStatus.PREVIEW_ONLY
            "user_owned" -> LicenseStatus.USER_OWNED
            else -> LicenseStatus.UNKNOWN
        }

        // Safety gate: for Spotify-sourced metadata we NEVER surface a full
        // audioUrl, even if the upstream response includes one — only an
        // explicitly licensed/user-owned track may carry it through.
        val safeAudioUrl = if (licenseStatus == LicenseStatus.LICENSED || licenseStatus == LicenseStatus.USER_OWNED) {
            dto.audioUrl
        } else null

        val safeDownloadAllowed = (dto.downloadAllowed == true) &&
            (licenseStatus == LicenseStatus.LICENSED || licenseStatus == LicenseStatus.USER_OWNED)

        return TrackModel(
            id = dto.id,
            title = dto.name,
            artistName = dto.artist,
            artistId = dto.artistId,
            albumName = dto.album,
            albumId = dto.albumId,
            albumImage = dto.image,
            durationMs = dto.durationMs,
            spotifyUrl = dto.externalUrl,
            previewUrl = dto.previewUrl,
            isPlayable = dto.previewUrl != null || safeAudioUrl != null,
            releaseDate = dto.releaseDate,
            explicit = dto.explicit ?: false,
            source = TrackSource.SPOTIFY_METADATA,
            audioUrl = safeAudioUrl,
            downloadAllowed = safeDownloadAllowed,
            licenseStatus = licenseStatus
        )
    }

    fun toArtistModel(dto: ArtistResponseDto?): ArtistModel? {
        if (dto == null || dto.id == null || dto.name == null) return null
        return ArtistModel(
            id = dto.id,
            name = dto.name,
            imageUrl = dto.image,
            genres = dto.genres ?: emptyList(),
            spotifyUrl = dto.externalUrl
        )
    }

    fun toAlbumModel(dto: AlbumResponseDto?): AlbumModel? {
        if (dto == null || dto.id == null || dto.name == null) return null
        return AlbumModel(
            id = dto.id,
            name = dto.name,
            artistName = dto.artist,
            imageUrl = dto.image,
            releaseDate = dto.releaseDate,
            trackCount = dto.trackCount,
            spotifyUrl = dto.externalUrl
        )
    }

    fun toSearchResultModel(dto: SearchResponseDto, query: String): SearchResultModel {
        return SearchResultModel(
            tracks = dto.tracks.orEmpty().mapNotNull { toTrackModel(it) },
            artists = dto.artists.orEmpty().mapNotNull { toArtistModel(it) },
            albums = dto.albums.orEmpty().mapNotNull { toAlbumModel(it) },
            query = query
        )
    }
}
