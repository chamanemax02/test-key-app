package lk.sonora.app.player

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.MediaStyleNotificationHelper
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import lk.sonora.app.MainActivity
import lk.sonora.app.R
import lk.sonora.app.model.Track

class SonoraMediaService : MediaSessionService() {

    private var mediaSession: MediaSession? = null
    private var exoPlayer: ExoPlayer? = null
    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())

    private val mediaReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                ACTION_PLAY_PAUSE -> MusicPlayerManager.togglePlayPause()
                ACTION_NEXT -> MusicPlayerManager.skipNext()
                ACTION_PREV -> MusicPlayerManager.skipPrevious()
                ACTION_FAVORITE -> {
                    val curr = MusicPlayerManager.playbackState.value.currentTrack
                    if (curr != null) {
                        serviceScope.launch(Dispatchers.IO) {
                            val app = applicationContext as? lk.sonora.app.SonoraApplication
                            app?.musicRepository?.toggleFavorite(curr)
                            launch(Dispatchers.Main) { updateNotification() }
                        }
                    }
                }
                ACTION_STOP -> {
                    MusicPlayerManager.togglePlayPause()
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                }
            }
        }
    }

    companion object {
        const val CHANNEL_ID = "SONORA_PLAYBACK_CHANNEL"
        const val NOTIFICATION_ID = 2001

        const val ACTION_PLAY_PAUSE = "lk.sonora.app.ACTION_PLAY_PAUSE"
        const val ACTION_NEXT = "lk.sonora.app.ACTION_NEXT"
        const val ACTION_PREV = "lk.sonora.app.ACTION_PREV"
        const val ACTION_FAVORITE = "lk.sonora.app.ACTION_FAVORITE"
        const val ACTION_STOP = "lk.sonora.app.ACTION_STOP"
    }

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        // Register action receiver
        val filter = IntentFilter().apply {
            addAction(ACTION_PLAY_PAUSE)
            addAction(ACTION_NEXT)
            addAction(ACTION_PREV)
            addAction(ACTION_FAVORITE)
            addAction(ACTION_STOP)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(mediaReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(mediaReceiver, filter)
        }

        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setAllowCrossProtocolRedirects(true)
            .setConnectTimeoutMs(30000)
            .setReadTimeoutMs(30000)
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
            .setDefaultRequestProperties(
                mapOf(
                    "Origin" to "https://www.youtube.com",
                    "Referer" to "https://www.youtube.com/"
                )
            )

        val dataSourceFactory = DefaultDataSource.Factory(this, httpDataSourceFactory)

        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                15000, // minBufferMs
                60000, // maxBufferMs
                1000,  // bufferForPlaybackMs
                2000   // bufferForPlaybackAfterRebufferMs
            )
            .build()

        val player = ExoPlayer.Builder(this)
            .setMediaSourceFactory(DefaultMediaSourceFactory(dataSourceFactory))
            .setLoadControl(loadControl)
            .setWakeMode(C.WAKE_MODE_NETWORK) // Keep CPU & Wi-Fi active during background streaming
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .setUsage(C.USAGE_MEDIA)
                    .build(),
                true // handle audio focus
            )
            .setHandleAudioBecomingNoisy(true)
            .build()

        exoPlayer = player

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        mediaSession = MediaSession.Builder(this, player)
            .setSessionActivity(pendingIntent)
            .build()

        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                updateNotification()
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                updateNotification()
            }

            override fun onMediaItemTransition(mediaItem: androidx.media3.common.MediaItem?, reason: Int) {
                updateNotification()
            }
        })

        YouTubeWebStreamEngine.init(this)

        MusicPlayerManager.attachPlayer(player, this)

        // Observe playback state changes for live notification updates
        serviceScope.launch {
            MusicPlayerManager.playbackState.collect {
                updateNotification()
            }
        }

        // Start initial foreground notification immediately to satisfy Android OS requirements
        val initialNotif = buildNotification(null, false, null)
        try {
            startForeground(NOTIFICATION_ID, initialNotif)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    fun updateNotification() {
        val state = MusicPlayerManager.playbackState.value
        val currentTrack = state.currentTrack
        val isPlaying = state.status == lk.sonora.app.model.PlayerStatus.PLAYING || exoPlayer?.isPlaying == true

        if (currentTrack == null) return

        serviceScope.launch(Dispatchers.IO) {
            var artworkBitmap: Bitmap? = null
            if (currentTrack.artworkUrl.isNotBlank()) {
                try {
                    val loader = ImageLoader(this@SonoraMediaService)
                    val req = ImageRequest.Builder(this@SonoraMediaService)
                        .data(currentTrack.artworkUrl)
                        .allowHardware(false)
                        .build()
                    val result = loader.execute(req)
                    if (result is SuccessResult) {
                        artworkBitmap = (result.drawable as? BitmapDrawable)?.bitmap
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            launch(Dispatchers.Main) {
                val notification = buildNotification(currentTrack, isPlaying, artworkBitmap)
                val manager = getSystemService(NotificationManager::class.java)
                manager?.notify(NOTIFICATION_ID, notification)
            }
        }
    }

    private fun buildNotification(track: Track?, isPlaying: Boolean, artwork: Bitmap?): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val contentPendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val favIntent = PendingIntent.getBroadcast(
            this, 0, Intent(ACTION_FAVORITE), PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val prevIntent = PendingIntent.getBroadcast(
            this, 1, Intent(ACTION_PREV), PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val playPauseIntent = PendingIntent.getBroadcast(
            this, 2, Intent(ACTION_PLAY_PAUSE), PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val nextIntent = PendingIntent.getBroadcast(
            this, 3, Intent(ACTION_NEXT), PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val stopIntent = PendingIntent.getBroadcast(
            this, 4, Intent(ACTION_STOP), PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val title = track?.title ?: getString(R.string.app_name)
        val artist = track?.displayArtist ?: "SONORA LK"

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(artist)
            .setSubText("SONORA LK • kezu")
            .setSmallIcon(R.drawable.ic_sonora_logo)
            .setColor(0xFF1DB954.toInt())
            .setColorized(true)
            .setContentIntent(contentPendingIntent)
            .setDeleteIntent(stopIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(isPlaying)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(
                if (track?.isFavorite == true) android.R.drawable.btn_star_big_on else android.R.drawable.btn_star_big_off,
                "Favorite",
                favIntent
            )
            .addAction(android.R.drawable.ic_media_previous, "Previous", prevIntent)
            .addAction(
                if (isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play,
                if (isPlaying) "Pause" else "Play",
                playPauseIntent
            )
            .addAction(android.R.drawable.ic_media_next, "Next", nextIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Close", stopIntent)

        val session = mediaSession
        if (session != null) {
            val mediaStyle = MediaStyleNotificationHelper.MediaStyle(session)
                .setShowActionsInCompactView(1, 2, 3)
            builder.setStyle(mediaStyle)
        }

        if (artwork != null) {
            builder.setLargeIcon(artwork)
        }

        return builder.build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "SONORA LK Audio Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Controls and media status for ongoing audio playback"
                setShowBadge(false)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        try {
            unregisterReceiver(mediaReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        MusicPlayerManager.detachPlayer()
        YouTubeWebStreamEngine.release()
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }
}
