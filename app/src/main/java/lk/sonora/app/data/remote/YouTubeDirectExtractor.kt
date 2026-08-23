package lk.sonora.app.data.remote

import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import lk.sonora.app.model.Track
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

object YouTubeDirectExtractor {

    private val gson = Gson()
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    suspend fun search(query: String): List<Track> = withContext(Dispatchers.IO) {
        val tracks = mutableListOf<Track>()
        try {
            val payload = JsonObject().apply {
                val context = JsonObject().apply {
                    val client = JsonObject().apply {
                        addProperty("clientName", "WEB")
                        addProperty("clientVersion", "2.20240101.01.00")
                        addProperty("hl", "en")
                        addProperty("gl", "LK")
                    }
                    add("client", client)
                }
                add("context", context)
                addProperty("query", query)
            }

            val request = Request.Builder()
                .url("https://www.youtube.com/youtubei/v1/search?prettyPrint=false")
                .post(payload.toString().toRequestBody(jsonMediaType))
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .header("X-YouTube-Client-Name", "1")
                .header("X-YouTube-Client-Version", "2.20240101.01.00")
                .build()

            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val body = response.body?.string().orEmpty()
                val json = gson.fromJson(body, JsonObject::class.java)

                val sectionList = json.getAsJsonObject("contents")
                    ?.getAsJsonObject("twoColumnSearchResultsRenderer")
                    ?.getAsJsonObject("primaryContents")
                    ?.getAsJsonObject("sectionListRenderer")
                    ?.getAsJsonArray("contents")

                sectionList?.forEach { sectionElem ->
                    val itemSection = sectionElem.asJsonObject.getAsJsonObject("itemSectionRenderer")
                    val items = itemSection?.getAsJsonArray("contents")
                    items?.forEach { itemElem ->
                        val videoRenderer = itemElem.asJsonObject.getAsJsonObject("videoRenderer")
                        if (videoRenderer != null) {
                            val videoId = videoRenderer.get("videoId")?.asString.orEmpty()
                            val title = videoRenderer.getAsJsonObject("title")
                                ?.getAsJsonArray("runs")?.get(0)?.asJsonObject?.get("text")?.asString
                                ?: videoRenderer.getAsJsonObject("title")?.get("simpleText")?.asString.orEmpty()

                            val artist = videoRenderer.getAsJsonObject("ownerText")
                                ?.getAsJsonArray("runs")?.get(0)?.asJsonObject?.get("text")?.asString
                                ?: videoRenderer.getAsJsonObject("shortBylineText")
                                    ?.getAsJsonArray("runs")?.get(0)?.asJsonObject?.get("text")?.asString.orEmpty()

                            val durationText = videoRenderer.getAsJsonObject("lengthText")?.get("simpleText")?.asString ?: "3:30"
                            val thumbnail = "https://i.ytimg.com/vi/$videoId/hqdefault.jpg"

                            if (videoId.isNotBlank() && title.isNotBlank()) {
                                tracks.add(
                                    Track(
                                        id = videoId,
                                        title = cleanTitle(title),
                                        artist = artist.ifBlank { "YouTube Music" },
                                        album = "YouTube Music",
                                        durationText = durationText,
                                        durationMs = parseDuration(durationText),
                                        artworkUrl = thumbnail,
                                        youtubeUrl = "https://www.youtube.com/watch?v=$videoId",
                                        audioUrl = "",
                                        isLocal = false
                                    )
                                )
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        tracks
    }

    suspend fun extractAudioUrl(videoId: String): String? = withContext(Dispatchers.IO) {
        val clients = listOf(
            Triple("ANDROID_TESTSUITE", "1.9", "3"),
            Triple("IOS", "19.29.1", "5"),
            Triple("ANDROID", "19.09.37", "3")
        )

        for ((clientName, clientVersion, clientHeader) in clients) {
            try {
                val payload = JsonObject().apply {
                    val context = JsonObject().apply {
                        val client = JsonObject().apply {
                            addProperty("clientName", clientName)
                            addProperty("clientVersion", clientVersion)
                            addProperty("hl", "en")
                            addProperty("gl", "LK")
                        }
                        add("client", client)
                    }
                    add("context", context)
                    addProperty("videoId", videoId)
                    addProperty("contentCheckOk", true)
                    addProperty("racyCheckOk", true)
                }

                val request = Request.Builder()
                    .url("https://www.youtube.com/youtubei/v1/player?prettyPrint=false")
                    .post(payload.toString().toRequestBody(jsonMediaType))
                    .header("User-Agent", "com.google.android.youtube/19.09.37 (Linux; U; Android 14)")
                    .header("X-YouTube-Client-Name", clientHeader)
                    .header("X-YouTube-Client-Version", clientVersion)
                    .build()

                val response = httpClient.newCall(request).execute()
                if (response.isSuccessful) {
                    val body = response.body?.string().orEmpty()
                    val json = gson.fromJson(body, JsonObject::class.java)
                    val streamingData = json.getAsJsonObject("streamingData")

                    val adaptiveFormats = streamingData?.getAsJsonArray("adaptiveFormats")
                    if (adaptiveFormats != null && adaptiveFormats.size() > 0) {
                        var bestAudioUrl: String? = null
                        var highestBitrate = 0

                        for (formatElem in adaptiveFormats) {
                            val format = formatElem.asJsonObject
                            val mimeType = format.get("mimeType")?.asString.orEmpty()
                            val url = format.get("url")?.asString.orEmpty()
                            val bitrate = format.get("bitrate")?.asInt ?: 0

                            if (mimeType.startsWith("audio/") && url.isNotBlank()) {
                                if (bitrate > highestBitrate) {
                                    highestBitrate = bitrate
                                    bestAudioUrl = url
                                }
                            }
                        }

                        if (!bestAudioUrl.isNullOrBlank()) {
                            return@withContext bestAudioUrl
                        }
                    }

                    val formats = streamingData?.getAsJsonArray("formats")
                    if (formats != null && formats.size() > 0) {
                        for (formatElem in formats) {
                            val format = formatElem.asJsonObject
                            val url = format.get("url")?.asString.orEmpty()
                            if (url.isNotBlank()) {
                                return@withContext url
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        null
    }

    private fun cleanTitle(raw: String): String {
        return raw.replace(Regex("(?i)\\[official.*?\\]|\\(official.*?\\)|\\(lyrics\\)|\\[lyrics\\]|\\(audio\\)|\\[audio\\]|\\(video\\)|\\[video\\]"), "")
            .trim()
    }

    private fun parseDuration(duration: String): Long {
        val parts = duration.split(":")
        return when (parts.size) {
            2 -> {
                val min = parts[0].toLongOrNull() ?: 0L
                val sec = parts[1].toLongOrNull() ?: 0L
                ((min * 60) + sec) * 1000L
            }
            3 -> {
                val hr = parts[0].toLongOrNull() ?: 0L
                val min = parts[1].toLongOrNull() ?: 0L
                val sec = parts[2].toLongOrNull() ?: 0L
                ((hr * 3600) + (min * 60) + sec) * 1000L
            }
            else -> 210000L
        }
    }
}
