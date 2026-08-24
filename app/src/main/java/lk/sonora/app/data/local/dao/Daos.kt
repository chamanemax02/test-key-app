package lk.sonora.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import lk.sonora.app.data.local.entity.FavoriteEntity
import lk.sonora.app.data.local.entity.PlaylistEntity
import lk.sonora.app.data.local.entity.PlaylistTrackEntity
import lk.sonora.app.data.local.entity.QueueEntity
import lk.sonora.app.data.local.entity.RecentlyPlayedEntity
import lk.sonora.app.data.local.entity.TrackEntity

@Dao
interface TrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTracks(tracks: List<TrackEntity>)

    @Query("SELECT * FROM tracks WHERE id = :trackId")
    suspend fun getTrackById(trackId: String): TrackEntity?

    @Query("SELECT * FROM tracks WHERE id IN (:trackIds)")
    suspend fun getTracksByIds(trackIds: List<String>): List<TrackEntity>

    @Query("SELECT * FROM tracks WHERE isLocal = 1")
    fun getLocalTracks(): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks WHERE title LIKE '%' || :query || '%' OR artist LIKE '%' || :query || '%'")
    suspend fun searchTracks(query: String): List<TrackEntity>

    @Query("DELETE FROM tracks WHERE isLocal = 1")
    suspend fun clearLocalTracks()
}

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(fav: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE trackId = :trackId")
    suspend fun removeFavorite(trackId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE trackId = :trackId)")
    fun isFavoriteFlow(trackId: String): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE trackId = :trackId)")
    suspend fun isFavorite(trackId: String): Boolean

    @Query("SELECT t.* FROM tracks t INNER JOIN favorites f ON t.id = f.trackId ORDER BY f.addedAt DESC")
    fun getFavoriteTracks(): Flow<List<TrackEntity>>

    @Query("SELECT trackId FROM favorites")
    suspend fun getAllFavoriteIds(): List<String>
}

@Dao
interface RecentlyPlayedDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(recent: RecentlyPlayedEntity)

    @Query("SELECT t.* FROM tracks t INNER JOIN recently_played r ON t.id = r.trackId ORDER BY r.playedAt DESC LIMIT 100")
    fun getRecentlyPlayedTracks(): Flow<List<TrackEntity>>

    @Query("DELETE FROM recently_played")
    suspend fun clearRecentlyPlayed()
}

@Dao
interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    @Query("SELECT * FROM playlists ORDER BY createdAt DESC")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlists WHERE id = :playlistId")
    suspend fun getPlaylistById(playlistId: Long): PlaylistEntity?

    @Query("DELETE FROM playlists WHERE id = :playlistId")
    suspend fun deletePlaylist(playlistId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTrackToPlaylist(entry: PlaylistTrackEntity)

    @Query("DELETE FROM playlist_tracks WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: String)

    @Query("SELECT t.* FROM tracks t INNER JOIN playlist_tracks pt ON t.id = pt.trackId WHERE pt.playlistId = :playlistId ORDER BY pt.orderIndex ASC, pt.addedAt ASC")
    fun getTracksForPlaylist(playlistId: Long): Flow<List<TrackEntity>>

    @Query("SELECT COUNT(*) FROM playlist_tracks WHERE playlistId = :playlistId")
    suspend fun getTrackCount(playlistId: Long): Int
}

@Dao
interface QueueDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setQueue(items: List<QueueEntity>)

    @Query("SELECT t.* FROM tracks t INNER JOIN queue q ON t.id = q.trackId ORDER BY q.orderIndex ASC")
    suspend fun getSavedQueue(): List<TrackEntity>

    @Query("DELETE FROM queue")
    suspend fun clearQueue()
}
