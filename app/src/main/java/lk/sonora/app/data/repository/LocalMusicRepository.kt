package lk.sonora.app.data.repository

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import lk.sonora.app.data.local.SonoraDatabase
import lk.sonora.app.data.local.entity.TrackEntity
import lk.sonora.app.model.Track

class LocalMusicRepository(
    private val context: Context,
    private val db: SonoraDatabase
) {

    fun getLocalTracks(): Flow<List<Track>> {
        return db.trackDao().getLocalTracks().map { entities ->
            val favIds = db.favoriteDao().getAllFavoriteIds().toSet()
            entities.map { it.toTrack(isFav = favIds.contains(it.id)) }
        }
    }

    suspend fun scanDeviceAudio(): List<Track> = withContext(Dispatchers.IO) {
        val audioList = mutableListOf<Track>()
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        try {
            val cursor = context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                sortOrder
            )

            cursor?.use { c ->
                val idCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val durationCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

                while (c.moveToNext()) {
                    val id = c.getLong(idCol)
                    val title = c.getString(titleCol) ?: "Unknown Title"
                    val artist = c.getString(artistCol) ?: "Local Artist"
                    val album = c.getString(albumCol) ?: "Local Music"
                    val durationMs = c.getLong(durationCol)

                    val contentUri: Uri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        id
                    )

                    val min = (durationMs / 1000) / 60
                    val sec = (durationMs / 1000) % 60
                    val durationText = String.format("%d:%02d", min, sec)

                    val track = Track(
                        id = "local_$id",
                        title = title,
                        artist = if (artist == "<unknown>") "Local Artist" else artist,
                        album = album,
                        durationText = durationText,
                        durationMs = durationMs,
                        artworkUrl = "", // Clean default placeholder ensures no ENOENT crashes
                        localUri = contentUri.toString(),
                        isLocal = true
                    )
                    audioList.add(track)
                }
            }

            if (audioList.isNotEmpty()) {
                db.trackDao().clearLocalTracks()
                db.trackDao().insertTracks(audioList.map { TrackEntity.fromTrack(it) })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        audioList
    }
}
