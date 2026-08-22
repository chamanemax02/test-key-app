package com.sonoralk.app.data.repository

import com.sonoralk.app.data.local.dao.FavoriteDao
import com.sonoralk.app.data.local.dao.PlaylistDao
import com.sonoralk.app.data.local.dao.RecentlyPlayedDao
import com.sonoralk.app.data.model.SearchResultModel
import com.sonoralk.app.data.model.TrackModel
import com.sonoralk.app.data.remote.ApiMapper
import com.sonoralk.app.data.remote.SonoraApiService
import kotlinx.coroutines.flow.Flow

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String, val cached: Boolean = false) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

/**
 * Single source of truth for track/artist/album/search data. Screens never
 * talk to SonoraApiService directly — everything routes through here so
 * caching, error fallback, and the license gate stay in one place.
 */
class MusicRepository(
    private val api: SonoraApiService,
    private val favoriteDao: FavoriteDao,
    private val playlistDao: PlaylistDao,
    private val recentlyPlayedDao: RecentlyPlayedDao
) {

    suspend fun search(query: String): Result<SearchResultModel> {
        return try {
            val dto = api.search(query)
            Result.Success(ApiMapper.toSearchResultModel(dto, query))
        } catch (e: Exception) {
            // Network errors handle කරන්න — never fake data, surface a
            // friendly error the UI can act on (retry / cached fallback).
            Result.Error(e.message ?: "Search failed")
        }
    }

    suspend fun getTrack(id: String): Result<TrackModel> {
        return try {
            val dto = api.getTrack(id)
            val model = ApiMapper.toTrackModel(dto)
            if (model != null) Result.Success(model) else Result.Error("Track not found")
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to load track")
        }
    }

    fun favoriteIds(): Flow<List<String>> = favoriteDao.observeFavoriteIds()

    suspend fun toggleFavorite(track: TrackModel, isFavorite: Boolean) {
        if (isFavorite) favoriteDao.remove(track.id) else favoriteDao.add(track.id, System.currentTimeMillis())
    }

    suspend fun recordRecentlyPlayed(track: TrackModel) {
        recentlyPlayedDao.upsert(track.id, System.currentTimeMillis())
    }

    fun recentlyPlayed(limit: Int = 20) = recentlyPlayedDao.observeRecent(limit)

    fun playlists() = playlistDao.observeAll()
}
