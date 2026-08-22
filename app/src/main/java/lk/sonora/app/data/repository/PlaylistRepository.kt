package lk.sonora.app.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import lk.sonora.app.data.local.SonoraDatabase
import lk.sonora.app.data.local.entity.PlaylistEntity
import lk.sonora.app.data.local.entity.PlaylistTrackEntity
import lk.sonora.app.data.local.entity.TrackEntity
import lk.sonora.app.model.Playlist
import lk.sonora.app.model.Track

class PlaylistRepository(private val db: SonoraDatabase) {

    fun getPlaylists(): Flow<List<Playlist>> {
        return db.playlistDao().getAllPlaylists().map { list ->
            list.map { entity ->
                val count = db.playlistDao().getTrackCount(entity.id)
                Playlist(
                    id = entity.id,
                    name = entity.name,
                    description = entity.description,
                    artworkUrl = entity.artworkUrl,
                    createdAt = entity.createdAt,
                    trackCount = count
                )
            }
        }
    }

    suspend fun createPlaylist(name: String, description: String = ""): Long = withContext(Dispatchers.IO) {
        val entity = PlaylistEntity(name = name, description = description)
        db.playlistDao().insertPlaylist(entity)
    }

    suspend fun deletePlaylist(playlistId: Long) = withContext(Dispatchers.IO) {
        db.playlistDao().deletePlaylist(playlistId)
    }

    fun getTracksForPlaylist(playlistId: Long): Flow<List<Track>> {
        return db.playlistDao().getTracksForPlaylist(playlistId).map { entities ->
            val favIds = db.favoriteDao().getAllFavoriteIds().toSet()
            entities.map { it.toTrack(isFav = favIds.contains(it.id)) }
        }
    }

    suspend fun addTrackToPlaylist(playlistId: Long, track: Track) = withContext(Dispatchers.IO) {
        db.trackDao().insertTrack(TrackEntity.fromTrack(track))
        val currentCount = db.playlistDao().getTrackCount(playlistId)
        db.playlistDao().addTrackToPlaylist(
            PlaylistTrackEntity(
                playlistId = playlistId,
                trackId = track.id,
                orderIndex = currentCount
            )
        )
    }

    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: String) = withContext(Dispatchers.IO) {
        db.playlistDao().removeTrackFromPlaylist(playlistId, trackId)
    }
}
