package lk.sonora.app.data.remote

import lk.sonora.app.data.remote.dto.DownloadResponseDto
import lk.sonora.app.data.remote.dto.SearchResponseDto
import lk.sonora.app.data.remote.dto.TrackResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SonoraApiService {

    @GET("search")
    suspend fun search(
        @Query("q") query: String,
        @Query("api_key") apiKey: String
    ): Response<SearchResponseDto>

    @GET("track")
    suspend fun getTrackMetadata(
        @Query("url") url: String,
        @Query("api_key") apiKey: String
    ): Response<TrackResponseDto>

    @GET("download")
    suspend fun getDownloadUrl(
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
