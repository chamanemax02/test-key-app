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
            .connectTimeout(12, TimeUnit.SECONDS)
            .readTimeout(12, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    private fun extractCleanVideoId(raw: String): String {
        return when {
            raw.contains("v=") -> raw.substringAfter("v=").substringBefore("&").substringBefore("?")
            raw.contains("youtu.be/") -> raw.substringAfter("youtu.be/").substringBefore("?").substringBefore("&")
            raw.contains("/embed/") -> raw.substringAfter("/embed/").substringBefore("?").substringBefore("&")
            else -> raw.trim()
        }
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

    suspend fun extractAudioUrl(rawId: String): String? = withContext(Dispatchers.IO) {
        val videoId = extractCleanVideoId(rawId)
        if (videoId.isBlank()) return@withContext null

        // 1. Try YouTube TVHTML5 Embedded Player Client (returns direct googlevideo.com URLs without cipher)
        try {
            val payload = JsonObject().apply {
                val context = JsonObject().apply {
                    val client = JsonObject().apply {
                        addProperty("clientName", "TVHTML5_SIMPLY_EMBEDDED_PLAYER")
                        addProperty("clientVersion", "2.0")
                        addProperty("hl", "en")
                        addProperty("gl", "LK")
                    }
                    add("client", client)
                    val thirdParty = JsonObject().apply {
                        addProperty("embedUrl", "https://www.youtube.com")
                    }
                    add("thirdParty", thirdParty)
                }
                add("context", context)
                addProperty("videoId", videoId)
            }

            val request = Request.Builder()
                .url("https://www.youtube.com/youtubei/v1/player?prettyPrint=false")
                .post(payload.toString().toRequestBody(jsonMediaType))
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .header("Origin", "https://www.youtube.com")
                .header("Referer", "https://www.youtube.com/")
                .build()

            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val body = response.body?.string().orEmpty()
                val json = gson.fromJson(body, JsonObject::class.java)
                val streamUrl = parseBestAudioFormat(json)
                if (!streamUrl.isNullOrBlank()) return@withContext streamUrl
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 2. Try ANDROID_TESTSUITE / IOS clients
        val innerTubeClients = listOf(
            Triple("ANDROID_TESTSUITE", "1.9", "3"),
            Triple("IOS", "19.29.1", "5"),
            Triple("ANDROID", "19.09.37", "3")
        )

        for ((clientName, clientVersion, clientHeader) in innerTubeClients) {
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
                    val streamUrl = parseBestAudioFormat(json)
                    if (!streamUrl.isNullOrBlank()) return@withContext streamUrl
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // 3. Fallback: Piped & Invidious Direct Google CDN Endpoints
        val pipedEndpoints = listOf(
            "https://pipedapi.kavin.rocks/streams/$videoId",
            "https://api.piped.yt/streams/$videoId",
            "https://inv.nadeko.net/api/v1/videos/$videoId"
        )

        for (endpoint in pipedEndpoints) {
            try {
                val req = Request.Builder()
                    .url(endpoint)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .build()
                val resp = httpClient.newCall(req).execute()
                if (resp.isSuccessful) {
                    val respBody = resp.body?.string().orEmpty()
                    val json = gson.fromJson(respBody, JsonObject::class.java)

                    // Piped structure
                    val audioStreams = json.getAsJsonArray("audioStreams")
                    if (audioStreams != null && audioStreams.size() > 0) {
                        val firstAudioUrl = audioStreams[0].asJsonObject.get("url")?.asString
                        if (!firstAudioUrl.isNullOrBlank()) return@withContext firstAudioUrl
                    }

                    // Invidious structure
                    val adaptiveFormats = json.getAsJsonArray("adaptiveFormats")
                    if (adaptiveFormats != null) {
                        for (elem in adaptiveFormats) {
                            val format = elem.asJsonObject
                            val type = format.get("type")?.asString.orEmpty()
                            val url = format.get("url")?.asString.orEmpty()
                            if (type.startsWith("audio/") && url.isNotBlank()) {
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

    private fun parseBestAudioFormat(json: JsonObject): String? {
        val streamingData = json.getAsJsonObject("streamingData") ?: return null

        val adaptiveFormats = streamingData.getAsJsonArray("adaptiveFormats")
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
                return bestAudioUrl
            }
        }

        val formats = streamingData.getAsJsonArray("formats")
        if (formats != null && formats.size() > 0) {
            for (formatElem in formats) {
                val format = formatElem.asJsonObject
                val url = format.get("url")?.asString.orEmpty()
                if (url.isNotBlank()) {
                    return url
                }
            }
        }

        return null
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
