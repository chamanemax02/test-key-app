package lk.sonora.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import lk.sonora.app.data.local.dao.FavoriteDao
import lk.sonora.app.data.local.dao.PlaylistDao
import lk.sonora.app.data.local.dao.QueueDao
import lk.sonora.app.data.local.dao.RecentlyPlayedDao
import lk.sonora.app.data.local.dao.TrackDao
import lk.sonora.app.data.local.entity.FavoriteEntity
import lk.sonora.app.data.local.entity.PlaylistEntity
import lk.sonora.app.data.local.entity.PlaylistTrackEntity
import lk.sonora.app.data.local.entity.QueueEntity
import lk.sonora.app.data.local.entity.RecentlyPlayedEntity
import lk.sonora.app.data.local.entity.TrackEntity

@Database(
    entities = [
        TrackEntity::class,
        FavoriteEntity::class,
        RecentlyPlayedEntity::class,
        PlaylistEntity::class,
        PlaylistTrackEntity::class,
        QueueEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SonoraDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun recentlyPlayedDao(): RecentlyPlayedDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun queueDao(): QueueDao

    companion object {
        @Volatile
        private var INSTANCE: SonoraDatabase? = null

        fun getInstance(context: Context): SonoraDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SonoraDatabase::class.java,
                    "sonora_lk.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
