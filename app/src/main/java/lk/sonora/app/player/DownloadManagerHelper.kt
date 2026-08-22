package lk.sonora.app.player

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import lk.sonora.app.model.Track
import java.io.File

object DownloadManagerHelper {

    fun downloadTrack(
        context: Context,
        track: Track,
        downloadUrl: String
    ): Long {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val cleanArtist = track.artist.replace("[^a-zA-Z0-9.-]".toRegex(), "_")
        val cleanTitle = track.title.replace("[^a-zA-Z0-9.-]".toRegex(), "_")
        val filename = "${cleanArtist}_-_${cleanTitle}_320kbps.mp3"

        val request = DownloadManager.Request(Uri.parse(downloadUrl))
            .setTitle(track.title)
            .setDescription("${track.artist} • SONORA LK")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_MUSIC,
                "SONORA LK/$filename"
            )
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        return downloadManager.enqueue(request)
    }
}
