package lk.sonora.app.player

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.OptIn
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import lk.sonora.app.model.PlaybackState
import lk.sonora.app.model.PlayerStatus
import lk.sonora.app.model.RepeatMode
import lk.sonora.app.model.Track

object MusicPlayerManager {

    private var exoPlayer: ExoPlayer? = null
    private var appContext: Context? = null
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var positionJob: Job? = null

    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    private val _queue = MutableStateFlow<List<Track>>(emptyList())
    val queue: StateFlow<List<Track>> = _queue.asStateFlow()

    private var currentIndex: Int = -1

    fun init(context: Context) {
        appContext = context.applicationContext
        startServiceIfNeeded()
    }

    private fun startServiceIfNeeded() {
        val ctx = appContext ?: return
        val intent = Intent(ctx, SonoraMediaService::class.java)
        try {
            ContextCompat.startForegroundService(ctx, intent)
        } catch (e: Exception) {
            ctx.startService(intent)
        }
    }

    fun attachPlayer(player: ExoPlayer, serviceContext: Context) {
        exoPlayer = player
        setupPlayerListeners()
    }

    fun detachPlayer() {
        exoPlayer = null
        stopPositionTracker()
    }

    private fun setupPlayerListeners() {
        exoPlayer?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_IDLE -> {
                        _playbackState.update { it.copy(status = PlayerStatus.IDLE) }
                        stopPositionTracker()
                    }
                    Player.STATE_BUFFERING -> {
                        _playbackState.update { it.copy(status = PlayerStatus.BUFFERING) }
                    }
                    Player.STATE_READY -> {
                        val isPlaying = exoPlayer?.isPlaying == true
                        val dur = exoPlayer?.duration?.coerceAtLeast(0L) ?: 0L
                        _playbackState.update {
                            it.copy(
                                status = if (isPlaying) PlayerStatus.PLAYING else PlayerStatus.PAUSED,
                                durationMs = if (dur > 0) dur else it.durationMs
                            )
                        }
                        if (isPlaying) startPositionTracker() else stopPositionTracker()
                    }
                    Player.STATE_ENDED -> {
                        _playbackState.update { it.copy(status = PlayerStatus.COMPLETED) }
                        stopPositionTracker()
                        handleTrackEnded()
                    }
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _playbackState.update {
                    it.copy(status = if (isPlaying) PlayerStatus.PLAYING else PlayerStatus.PAUSED)
                }
                if (isPlaying) startPositionTracker() else stopPositionTracker()
            }

            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                _playbackState.update {
                    it.copy(
                        status = PlayerStatus.ERROR,
                        errorMessage = error.localizedMessage ?: "Playback error"
                    )
                }
                stopPositionTracker()
            }
        })
    }

    fun playTrack(track: Track, newQueue: List<Track>? = null) {
        startServiceIfNeeded()
        if (newQueue != null) {
            _queue.value = newQueue
            currentIndex = newQueue.indexOfFirst { it.id == track.id }.coerceAtLeast(0)
        } else {
            val q = _queue.value.toMutableList()
            val existingIndex = q.indexOfFirst { it.id == track.id }
            if (existingIndex == -1) {
                q.add(track)
                _queue.value = q
                currentIndex = q.size - 1
            } else {
                currentIndex = existingIndex
            }
        }

        playCurrentTrack(track)
    }

    @OptIn(UnstableApi::class)
    private fun playCurrentTrack(track: Track) {
        val player = exoPlayer ?: return
        val url = track.playableUrl

        if (url.isBlank()) {
            _playbackState.update {
                it.copy(
                    currentTrack = track,
                    status = PlayerStatus.ERROR,
                    errorMessage = "Audio playback source is unavailable."
                )
            }
            return
        }

        val mediaMetadata = MediaMetadata.Builder()
            .setTitle(track.title)
            .setArtist(track.artist)
            .setAlbumTitle(track.album)
            .setArtworkUri(if (track.artworkUrl.isNotBlank()) Uri.parse(track.artworkUrl) else null)
            .build()

        val mediaItem = MediaItem.Builder()
            .setUri(Uri.parse(url))
            .setMediaId(track.id)
            .setMediaMetadata(mediaMetadata)
            .build()

        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()

        _playbackState.update {
            it.copy(
                currentTrack = track,
                status = PlayerStatus.BUFFERING,
                currentPositionMs = 0L,
                durationMs = track.durationMs,
                errorMessage = null
            )
        }
    }

    fun togglePlayPause() {
        val player = exoPlayer ?: return
        if (player.isPlaying) {
            player.pause()
        } else {
            if (player.playbackState == Player.STATE_IDLE || player.playbackState == Player.STATE_ENDED) {
                val curr = _playbackState.value.currentTrack
                if (curr != null) playCurrentTrack(curr)
            } else {
                player.play()
            }
        }
    }

    fun seekTo(positionMs: Long) {
        exoPlayer?.seekTo(positionMs)
        _playbackState.update { it.copy(currentPositionMs = positionMs) }
    }

    fun skipNext() {
        val q = _queue.value
        if (q.isEmpty()) return

        if (_playbackState.value.isShuffleEnabled) {
            currentIndex = (q.indices).random()
        } else {
            currentIndex = (currentIndex + 1) % q.size
        }
        val nextTrack = q[currentIndex]
        playCurrentTrack(nextTrack)
    }

    fun skipPrevious() {
        val player = exoPlayer
        if (player != null && player.currentPosition > 3000) {
            player.seekTo(0)
            return
        }

        val q = _queue.value
        if (q.isEmpty()) return

        currentIndex = if (currentIndex - 1 < 0) q.size - 1 else currentIndex - 1
        val prevTrack = q[currentIndex]
        playCurrentTrack(prevTrack)
    }

    fun toggleShuffle() {
        _playbackState.update { it.copy(isShuffleEnabled = !it.isShuffleEnabled) }
    }

    fun cycleRepeatMode() {
        val nextMode = when (_playbackState.value.repeatMode) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
        _playbackState.update { it.copy(repeatMode = nextMode) }
    }

    private fun handleTrackEnded() {
        when (_playbackState.value.repeatMode) {
            RepeatMode.ONE -> {
                val current = _playbackState.value.currentTrack
                if (current != null) playCurrentTrack(current)
            }
            RepeatMode.ALL -> skipNext()
            RepeatMode.OFF -> {
                if (currentIndex < _queue.value.size - 1) {
                    skipNext()
                }
            }
        }
    }

    fun addToQueue(track: Track) {
        val current = _queue.value.toMutableList()
        current.add(track)
        _queue.value = current
    }

    fun removeFromQueue(trackId: String) {
        val current = _queue.value.toMutableList()
        current.removeAll { it.id == trackId }
        _queue.value = current
    }

    fun clearQueue() {
        _queue.value = emptyList()
        currentIndex = -1
    }

    private fun startPositionTracker() {
        positionJob?.cancel()
        positionJob = scope.launch {
            while (isActive) {
                val player = exoPlayer
                if (player != null && player.isPlaying) {
                    val pos = player.currentPosition.coerceAtLeast(0L)
                    val dur = player.duration.coerceAtLeast(0L)
                    val buf = player.bufferedPosition.coerceAtLeast(0L)
                    _playbackState.update {
                        it.copy(
                            currentPositionMs = pos,
                            durationMs = if (dur > 0) dur else it.durationMs,
                            bufferedPositionMs = buf
                        )
                    }
                }
                delay(250)
            }
        }
    }

    private fun stopPositionTracker() {
        positionJob?.cancel()
        positionJob = null
    }
}
