package com.sonoralk.app.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sonoralk.app.data.local.dao.*
import com.sonoralk.app.data.local.entity.*

@Database(
    entities = [
        FavoriteEntity::class,
        RecentlyPlayedEntity::class,
        PlaylistEntity::class,
        PlaylistTrackEntity::class,
        LocalTrackEntity::class,
        CachedMetadataEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun recentlyPlayedDao(): RecentlyPlayedDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun localTrackDao(): LocalTrackDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sonora_lk.db"
                ).build().also { INSTANCE = it }
            }
    }
}
