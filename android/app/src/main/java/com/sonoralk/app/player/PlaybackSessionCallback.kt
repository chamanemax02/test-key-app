package com.sonoralk.app.player

import androidx.media3.session.MediaSession

/**
 * Handles next/previous/queue commands from notification, lock screen,
 * Bluetooth, and headset buttons. Kept as a separate class so queue/shuffle/
 * repeat logic is testable independent of the Android service lifecycle.
 */
class PlaybackSessionCallback : MediaSession.Callback {
    // Default MediaSession.Callback behavior already routes standard
    // play/pause/seek/skip commands to the underlying ExoPlayer.
    // Override onConnect/onPostConnect here if custom queue commands
    // (shuffle toggle, repeat cycle) need dedicated session commands.
}
