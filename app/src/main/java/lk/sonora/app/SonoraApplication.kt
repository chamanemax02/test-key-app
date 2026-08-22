package lk.sonora.app

import android.app.Application
import lk.sonora.app.data.local.SonoraDatabase
import lk.sonora.app.data.repository.LocalMusicRepository
import lk.sonora.app.data.repository.MusicRepository
import lk.sonora.app.data.repository.PlaylistRepository
import lk.sonora.app.player.MusicPlayerManager

class SonoraApplication : Application() {

    val database: SonoraDatabase by lazy {
        SonoraDatabase.getInstance(this)
    }

    val musicRepository: MusicRepository by lazy {
        MusicRepository(database)
    }

    val localMusicRepository: LocalMusicRepository by lazy {
        LocalMusicRepository(this, database)
    }

    val playlistRepository: PlaylistRepository by lazy {
        PlaylistRepository(database)
    }

    override fun onCreate() {
        super.onCreate()
        MusicPlayerManager.init(this)
    }
}
