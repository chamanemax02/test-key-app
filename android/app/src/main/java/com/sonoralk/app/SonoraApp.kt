package com.sonoralk.app

import android.app.Application
import com.sonoralk.app.data.local.db.AppDatabase
import com.sonoralk.app.data.remote.ApiClient
import com.sonoralk.app.data.repository.MusicRepository
import com.sonoralk.app.player.PlaybackRepository

class SonoraApp : Application() {

    lateinit var database: AppDatabase
        private set
    lateinit var musicRepository: MusicRepository
        private set
    lateinit var playbackRepository: PlaybackRepository
        private set

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getInstance(this)
        musicRepository = MusicRepository(
            api = ApiClient.service,
            favoriteDao = database.favoriteDao(),
            playlistDao = database.playlistDao(),
            recentlyPlayedDao = database.recentlyPlayedDao()
        )
        playbackRepository = PlaybackRepository()
    }
}
