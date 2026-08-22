package lk.sonora.app.data.remote

import lk.sonora.app.data.remote.dto.DownloadResponseDto
import lk.sonora.app.data.remote.dto.SearchResponseDto
import lk.sonora.app.data.remote.dto.TrackResponseDto
import lk.sonora.app.data.remote.dto.YouTubeDownloadResponseDto
import lk.sonora.app.data.remote.dto.YouTubeSearchResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SonoraApiService {

    // YouTube Search
    @GET("youtube/search")
    suspend fun searchYouTube(
        @Query("q") query: String,
        @Query("api_key") apiKey: String
    ): Response<YouTubeSearchResponseDto>

    // YouTube Audio Stream / 320kbps Download URL
    @GET("youtube/download")
    suspend fun getYouTubeAudioStream(
        @Query("url") url: String,
        @Query("api_key") apiKey: String
    ): Response<YouTubeDownloadResponseDto>

    // Spotify Legacy Endpoints
    @GET("spotify/search")
    suspend fun searchSpotify(
        @Query("q") query: String,
        @Query("api_key") apiKey: String
    ): Response<SearchResponseDto>

    @GET("spotify/track")
    suspend fun getTrackMetadata(
        @Query("url") url: String,
        @Query("api_key") apiKey: String
    ): Response<TrackResponseDto>

    @GET("spotify/download")
    suspend fun getSpotifyDownloadUrl(
        @Query("q") spotifyUrl: String,
        @Query("quality") quality: String = "320kbps",
        @Query("api_key") apiKey: String
    ): Response<DownloadResponseDto>
}

sealed class ApiResult<out T> {
    data class Success<out T>(val data: T) : ApiResult<T>()
    data class Error(val message: String, val code: Int? = null) : ApiResult<Nothing>()
    object Loading : ApiResult<Nothing>()
}
