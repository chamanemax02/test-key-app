package lk.sonora.app

import lk.sonora.app.data.remote.dto.parseDurationToMs
import lk.sonora.app.model.PlaybackState
import lk.sonora.app.model.PlayerStatus
import lk.sonora.app.model.Track
import org.junit.Assert.assertEquals
import org.junit.Test

class TrackModelTest {

    @Test
    fun testTrackPlayableUrlFallback() {
        val localTrack = Track(
            id = "1",
            title = "Song A",
            artist = "Artist A",
            localUri = "content://media/external/audio/1",
            previewUrl = "https://preview.url",
            audioUrl = "https://audio.url"
        )
        assertEquals("content://media/external/audio/1", localTrack.playableUrl)

        val directAudioTrack = Track(
            id = "2",
            title = "Song B",
            artist = "Artist B",
            previewUrl = "https://preview.url",
            audioUrl = "https://audio.url"
        )
        assertEquals("https://audio.url", directAudioTrack.playableUrl)

        val previewOnlyTrack = Track(
            id = "3",
            title = "Song C",
            artist = "Artist C",
            previewUrl = "https://preview.url"
        )
        assertEquals("https://preview.url", previewOnlyTrack.playableUrl)
    }

    @Test
    fun testDurationParsing() {
        assertEquals(233000L, parseDurationToMs("3:53"))
        assertEquals(236000L, parseDurationToMs("3:56"))
        assertEquals(3661000L, parseDurationToMs("1:01:01"))
        assertEquals(0L, parseDurationToMs(""))
    }

    @Test
    fun testPlaybackStateProgress() {
        val state = PlaybackState(
            status = PlayerStatus.PLAYING,
            currentPositionMs = 50000L,
            durationMs = 100000L
        )
        assertEquals(0.5f, state.progress, 0.001f)
    }
}
