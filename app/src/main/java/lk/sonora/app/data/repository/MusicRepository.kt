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
            // Direct YouTube InnerTube Search (Zero normal data, works 100% on YouTube packages)
            val directItems = lk.sonora.app.data.remote.YouTubeDirectExtractor.search(query)
            if (directItems.isNotEmpty()) {
                val favoriteIds = db.favoriteDao().getAllFavoriteIds().toSet()
                val tracks = directItems.map { it.copy(isFavorite = favoriteIds.contains(it.id)) }
                db.trackDao().insertTracks(tracks.map { TrackEntity.fromTrack(it) })
                return@withContext ApiResult.Success(tracks)
            }

            // If empty, check cached tracks matching query
            val cached = db.trackDao().searchTracks(query)
            if (cached.isNotEmpty()) {
                val favoriteIds = db.favoriteDao().getAllFavoriteIds().toSet()
                val tracks = cached.map { it.toTrack(isFav = favoriteIds.contains(it.id)) }
                return@withContext ApiResult.Success(tracks)
            }

            ApiResult.Error("No matching songs found. Please check your connection.")
        } catch (e: Exception) {
            ApiResult.Error("Network connection error. Please retry.")
        }
    }

    suspend fun resolveAudioStream(track: Track): ApiResult<Track> = withContext(Dispatchers.IO) {
        // If track already has playable direct audio or local URI, return it immediately
        if (track.playableUrl.isNotBlank()) {
            return@withContext ApiResult.Success(track)
        }

        try {
            // Direct YouTube CDN stream extraction (*.googlevideo.com for YouTube package streaming)
            val directStreamUrl = lk.sonora.app.data.remote.YouTubeDirectExtractor.extractAudioUrl(track.id)
            if (!directStreamUrl.isNullOrBlank()) {
                val updatedTrack = track.copy(audioUrl = directStreamUrl)
                db.trackDao().insertTrack(TrackEntity.fromTrack(updatedTrack))
                return@withContext ApiResult.Success(updatedTrack)
            }

            ApiResult.Error("Unable to load audio stream. Please check connection and retry.")
        } catch (e: Exception) {
            ApiResult.Error("Failed to connect to audio stream. Please retry.")
        }
    }

    suspend fun getDownloadUrl(track: Track): ApiResult<String> = withContext(Dispatchers.IO) {
        if (track.audioUrl.isNotBlank()) {
            return@withContext ApiResult.Success(track.audioUrl)
        }

        when (val streamRes = resolveAudioStream(track)) {
            is ApiResult.Success -> {
                if (streamRes.data.audioUrl.isNotBlank()) {
                    ApiResult.Success(streamRes.data.audioUrl)
                } else {
                    ApiResult.Error("Download stream unavailable for this track")
                }
            }
            is ApiResult.Error -> ApiResult.Error(streamRes.message)
            is ApiResult.Loading -> ApiResult.Error("Resolving download...")
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
