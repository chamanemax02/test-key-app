package com.sonoralk.app.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit interface pointed at OUR backend proxy (see /backend), never
 * directly at the third-party provider. The proxy holds the real API key;
 * the app only ever talks to our own domain.
 */
interface SonoraApiService {

    @GET("v1/search")
    suspend fun search(@Query("q") query: String, @Query("type") type: String = "all"): SearchResponseDto

    @GET("v1/track")
    suspend fun getTrack(@Query("id") id: String): TrackResponseDto

    @GET("v1/artist")
    suspend fun getArtist(@Query("id") id: String): ArtistResponseDto

    @GET("v1/album")
    suspend fun getAlbum(@Query("id") id: String): AlbumResponseDto
}

// --- Raw DTOs: intentionally loose/nullable, mirroring "the upstream shape
// might change" risk. Never used outside the mapper layer. ---

data class SearchResponseDto(
    val tracks: List<TrackResponseDto>? = null,
    val artists: List<ArtistResponseDto>? = null,
    val albums: List<AlbumResponseDto>? = null
)

data class TrackResponseDto(
    val id: String? = null,
    val name: String? = null,
    val artist: String? = null,
    val artistId: String? = null,
    val album: String? = null,
    val albumId: String? = null,
    val image: String? = null,
    val durationMs: Long? = null,
    val externalUrl: String? = null,
    val previewUrl: String? = null,
    val releaseDate: String? = null,
    val explicit: Boolean? = null,
    val downloadAllowed: Boolean? = null,
    val licenseStatus: String? = null,
    val audioUrl: String? = null
)

data class ArtistResponseDto(
    val id: String? = null,
    val name: String? = null,
    val image: String? = null,
    val genres: List<String>? = null,
    val externalUrl: String? = null
)

data class AlbumResponseDto(
    val id: String? = null,
    val name: String? = null,
    val artist: String? = null,
    val image: String? = null,
    val releaseDate: String? = null,
    val trackCount: Int? = null,
    val externalUrl: String? = null
)
