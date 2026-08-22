package lk.sonora.app.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import lk.sonora.app.data.local.SonoraDatabase
import lk.sonora.app.data.local.entity.FavoriteEntity
import lk.sonora.app.data.local.entity.RecentlyPlayedEntity
import lk.sonora.app.data.local.entity.TrackEntity
import lk.sonora.app.data.remote.ApiClient
import lk.sonora.app.data.remote.ApiResult
import lk.sonora.app.model.Track

class MusicRepository(private val db: SonoraDatabase) {

    private val api = ApiClient.getService()
    private val apiKey = ApiClient.apiKey

    suspend fun searchMusic(query: String): ApiResult<List<Track>> = withContext(Dispatchers.IO) {
        try {
            val response = api.search(query = query, apiKey = apiKey)
            if (response.isSuccessful && response.body()?.status == true) {
                val items = response.body()?.result.orEmpty()
                val favoriteIds = db.favoriteDao().getAllFavoriteIds().toSet()
                val tracks = items.map { dto ->
                    val track = dto.toTrack()
                    track.copy(isFavorite = favoriteIds.contains(track.id))
                }
                // Cache tracks into Room
                db.trackDao().insertTracks(tracks.map { TrackEntity.fromTrack(it) })
                ApiResult.Success(tracks)
            } else {
                ApiResult.Error(response.body()?.detail ?: "Failed to find matching songs", response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error(e.localizedMessage ?: "Network connection error")
        }
    }

    suspend fun getTrackMetadata(spotifyUrl: String): ApiResult<Track> = withContext(Dispatchers.IO) {
        try {
            val response = api.getTrackMetadata(url = spotifyUrl, apiKey = apiKey)
            if (response.isSuccessful && response.body()?.status == true) {
                val detail = response.body()?.result
                if (detail != null) {
                    val track = detail.toTrack()
                    val isFav = db.favoriteDao().isFavorite(track.id)
                    val fullTrack = track.copy(isFavorite = isFav)
                    db.trackDao().insertTrack(TrackEntity.fromTrack(fullTrack))
                    ApiResult.Success(fullTrack)
                } else {
                    ApiResult.Error("Track details not found")
                }
            } else {
                ApiResult.Error(response.body()?.detail ?: "Failed to load track metadata", response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error(e.localizedMessage ?: "Network error")
        }
    }

    suspend fun getDownloadUrl(spotifyUrl: String, quality: String = "320kbps"): ApiResult<String> = withContext(Dispatchers.IO) {
        try {
            val response = api.getDownloadUrl(spotifyUrl = spotifyUrl, quality = quality, apiKey = apiKey)
            if (response.isSuccessful && response.body()?.status == true) {
                val dlUrl = response.body()?.result?.downloadUrl
                if (!dlUrl.isNullOrBlank()) {
                    ApiResult.Success(dlUrl)
                } else {
                    ApiResult.Error("Download stream unavailable for this track")
                }
            } else {
                ApiResult.Error(response.body()?.detail ?: "Download not authorized", response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error(e.localizedMessage ?: "Failed to retrieve download link")
        }
    }

    fun getFavoriteTracks(): Flow<List<Track>> {
        return db.favoriteDao().getFavoriteTracks().map { entities ->
            entities.map { it.toTrack(isFav = true) }
        }
    }

    fun isFavorite(trackId: String): Flow<Boolean> {
        return db.favoriteDao().isFavoriteFlow(trackId)
    }

    suspend fun toggleFavorite(track: Track) = withContext(Dispatchers.IO) {
        val exists = db.favoriteDao().isFavorite(track.id)
        if (exists) {
            db.favoriteDao().removeFavorite(track.id)
        } else {
            db.trackDao().insertTrack(TrackEntity.fromTrack(track))
            db.favoriteDao().addFavorite(FavoriteEntity(trackId = track.id))
        }
    }

    fun getRecentlyPlayed(): Flow<List<Track>> {
        return db.recentlyPlayedDao().getRecentlyPlayedTracks().map { entities ->
            val favIds = db.favoriteDao().getAllFavoriteIds().toSet()
            entities.map { it.toTrack(isFav = favIds.contains(it.id)) }
        }
    }

    suspend fun recordRecentlyPlayed(track: Track) = withContext(Dispatchers.IO) {
        db.trackDao().insertTrack(TrackEntity.fromTrack(track))
        db.recentlyPlayedDao().insertOrUpdate(RecentlyPlayedEntity(trackId = track.id))
    }

    suspend fun clearAllData() = withContext(Dispatchers.IO) {
        db.recentlyPlayedDao().clearRecentlyPlayed()
        db.queueDao().clearQueue()
    }
}
