package lk.sonora.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import lk.sonora.app.model.Track

@Entity(tableName = "tracks")
data class TrackEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val durationText: String,
    val durationMs: Long,
    val artworkUrl: String,
    val youtubeUrl: String,
    val spotifyUrl: String,
    val previewUrl: String,
    val audioUrl: String,
    val localUri: String,
    val isLocal: Boolean,
    val releaseDate: String,
    val quality: String
) {
    fun toTrack(isFav: Boolean = false): Track {
        return Track(
            id = id,
            title = title,
            artist = artist,
            album = album,
            durationText = durationText,
            durationMs = durationMs,
            artworkUrl = artworkUrl,
            youtubeUrl = youtubeUrl,
            spotifyUrl = spotifyUrl,
            previewUrl = previewUrl,
            audioUrl = audioUrl,
            localUri = localUri,
            isLocal = isLocal,
            isFavorite = isFav,
            releaseDate = releaseDate,
            quality = quality
        )
    }

    companion object {
        fun fromTrack(track: Track): TrackEntity {
            return TrackEntity(
                id = track.id,
                title = track.title,
                artist = track.artist,
                album = track.album,
                durationText = track.durationText,
                durationMs = track.durationMs,
                artworkUrl = track.artworkUrl,
                youtubeUrl = track.youtubeUrl,
                spotifyUrl = track.spotifyUrl,
                previewUrl = track.previewUrl,
                audioUrl = track.audioUrl,
                localUri = track.localUri,
                isLocal = track.isLocal,
                releaseDate = track.releaseDate,
                quality = track.quality
            )
        }
    }
}

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey
    val trackId: String,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "recently_played")
data class RecentlyPlayedEntity(
    @PrimaryKey
    val trackId: String,
    val playedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val artworkUrl: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "playlist_tracks",
    primaryKeys = ["playlistId", "trackId"]
)
data class PlaylistTrackEntity(
    val playlistId: Long,
    val trackId: String,
    val orderIndex: Int = 0,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "queue")
data class QueueEntity(
    @PrimaryKey
    val trackId: String,
    val orderIndex: Int
)
