package com.sonoralk.app.data.local.dao

import androidx.room.*
import com.sonoralk.app.data.local.entity.RecentlyPlayedEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentlyPlayedDao {
    @Query("SELECT trackId FROM recently_played ORDER BY playedAt DESC LIMIT :limit")
    fun observeRecent(limit: Int): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(trackId: String, playedAt: Long): Unit =
        upsert(RecentlyPlayedEntity(trackId, playedAt))

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: RecentlyPlayedEntity)
}
