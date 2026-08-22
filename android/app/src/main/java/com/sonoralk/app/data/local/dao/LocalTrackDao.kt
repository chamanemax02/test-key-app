package com.sonoralk.app.data.local.dao

import androidx.room.*
import com.sonoralk.app.data.local.entity.LocalTrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalTrackDao {
    @Query("SELECT * FROM local_tracks ORDER BY title ASC")
    fun observeAll(): Flow<List<LocalTrackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tracks: List<LocalTrackEntity>)

    @Query("DELETE FROM local_tracks WHERE id = :id")
    suspend fun delete(id: String)
}
