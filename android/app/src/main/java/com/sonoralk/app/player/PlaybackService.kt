package com.sonoralk.app.player

import android.app.PendingIntent
import android.content.Intent
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.sonoralk.app.MainActivity

/**
 * Foreground media service — this is what keeps audio alive when the app
 * is minimized, and what drives the system notification, lock-screen
 * controls, and Bluetooth/headset button events via Media3's MediaSession.
 *
 * NOTE ON SCOPE: this service only ever plays TrackModel.previewUrl (short
 * Spotify previews) or TrackModel.audioUrl for LICENSED/USER_OWNED tracks
 * (local imports). It has no code path for extracting or streaming full
 * Spotify catalog audio — that gate lives upstream in ApiMapper.
 */
class PlaybackService : MediaSessionService() {

    private var mediaSession: MediaSession? = null
    private lateinit var player: ExoPlayer

    override fun onCreate() {
        super.onCreate()

        player = ExoPlayer.Builder(this)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .build(),
                /* handleAudioFocus = */ true // pause on focus loss, duck appropriately
            )
            .setHandleAudioBecomingNoisy(true) // pause when headphones unplugged
            .build()

        val sessionActivityIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        mediaSession = MediaSession.Builder(this, player)
            .setSessionActivity(sessionActivityIntent)
            .setCallback(PlaybackSessionCallback())
            .build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = mediaSession

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        // Keep playing if audio is active; stop the service otherwise —
        // matches user expectation of background playback for owned/licensed audio.
        if (!player.playWhenReady || player.mediaItemCount == 0) {
            stopSelf()
        }
    }
}
