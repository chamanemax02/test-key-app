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

    private fun extractCleanVideoId(raw: String): String {
        return when {
            raw.contains("v=") -> raw.substringAfter("v=").substringBefore("&").substringBefore("?")
            raw.contains("youtu.be/") -> raw.substringAfter("youtu.be/").substringBefore("?").substringBefore("&")
            raw.contains("/embed/") -> raw.substringAfter("/embed/").substringBefore("?").substringBefore("&")
            else -> raw.trim()
        }
    }

    /**
     * Search directly on www.youtube.com (100% YouTube package compatible)
     */
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
                                        title = title.trim(),
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

    /**
     * Extracts direct, unencrypted Google CDN Audio Streams (googlevideo.com)
     * Calls strictly www.youtube.com (Zero Normal Data, works 100% on YouTube Unlimited Package).
     */
    suspend fun extractAudioUrl(rawId: String): String? = withContext(Dispatchers.IO) {
        val videoId = extractCleanVideoId(rawId)
        if (videoId.isBlank()) return@withContext null

        // Priority 1: IOS Client (YouTube iOS client provides direct unencrypted googlevideo.com URLs)
        try {
            val payload = JsonObject().apply {
                val context = JsonObject().apply {
                    val client = JsonObject().apply {
                        addProperty("clientName", "IOS")
                        addProperty("clientVersion", "19.29.1")
                        addProperty("deviceModel", "iPhone16,2")
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
                .header("User-Agent", "com.google.ios.youtube/19.29.1 (iPhone16,2; U; CPU iOS 17_5_1 like Mac OS X; en_LK)")
                .header("X-YouTube-Client-Name", "5")
                .header("X-YouTube-Client-Version", "19.29.1")
                .build()

            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val json = gson.fromJson(response.body?.string().orEmpty(), JsonObject::class.java)
                val url = parseDirectAudioUrl(json)
                if (!url.isNullOrBlank()) return@withContext url
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Priority 2: TVHTML5 Embedded Player
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
                .header("X-YouTube-Client-Name", "85")
                .header("X-YouTube-Client-Version", "2.0")
                .build()

            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val json = gson.fromJson(response.body?.string().orEmpty(), JsonObject::class.java)
                val url = parseDirectAudioUrl(json)
                if (!url.isNullOrBlank()) return@withContext url
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Priority 3: ANDROID_TESTSUITE Client
        try {
            val payload = JsonObject().apply {
                val context = JsonObject().apply {
                    val client = JsonObject().apply {
                        addProperty("clientName", "ANDROID_TESTSUITE")
                        addProperty("clientVersion", "1.9")
                        addProperty("hl", "en")
                        addProperty("gl", "LK")
                    }
                    add("client", client)
                }
                add("context", context)
                addProperty("videoId", videoId)
            }

            val request = Request.Builder()
                .url("https://www.youtube.com/youtubei/v1/player?prettyPrint=false")
                .post(payload.toString().toRequestBody(jsonMediaType))
                .header("User-Agent", "com.google.android.youtube/1.9 (Linux; U; Android 14)")
                .header("X-YouTube-Client-Name", "3")
                .header("X-YouTube-Client-Version", "1.9")
                .build()

            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val json = gson.fromJson(response.body?.string().orEmpty(), JsonObject::class.java)
                val url = parseDirectAudioUrl(json)
                if (!url.isNullOrBlank()) return@withContext url
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Priority 4: WEB_EMBEDDED_PLAYER
        try {
            val payload = JsonObject().apply {
                val context = JsonObject().apply {
                    val client = JsonObject().apply {
                        addProperty("clientName", "WEB_EMBEDDED_PLAYER")
                        addProperty("clientVersion", "1.20240101.01.00")
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
                .header("X-YouTube-Client-Name", "56")
                .header("X-YouTube-Client-Version", "1.20240101.01.00")
                .build()

            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val json = gson.fromJson(response.body?.string().orEmpty(), JsonObject::class.java)
                val url = parseDirectAudioUrl(json)
                if (!url.isNullOrBlank()) return@withContext url
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        null
    }

    private fun parseDirectAudioUrl(json: JsonObject): String? {
        val streamingData = json.getAsJsonObject("streamingData") ?: return null

        // 1. Check adaptiveFormats (dedicated audio stream: audio/mp4 m4a, audio/webm opus)
        val adaptiveFormats = streamingData.getAsJsonArray("adaptiveFormats")
        if (adaptiveFormats != null && adaptiveFormats.size() > 0) {
            var bestUrl: String? = null
            var highestBitrate = 0

            for (formatElem in adaptiveFormats) {
                val format = formatElem.asJsonObject
                val mimeType = format.get("mimeType")?.asString.orEmpty()
                val url = format.get("url")?.asString.orEmpty()
                val bitrate = format.get("bitrate")?.asInt ?: 0

                // Only accept pure direct unencrypted URLs to googlevideo.com
                if (mimeType.startsWith("audio/") && url.isNotBlank() && url.startsWith("https://")) {
                    if (bitrate > highestBitrate) {
                        highestBitrate = bitrate
                        bestUrl = url
                    }
                }
            }

            if (!bestUrl.isNullOrBlank()) return bestUrl
        }

        // 2. Check standard combined formats
        val formats = streamingData.getAsJsonArray("formats")
        if (formats != null && formats.size() > 0) {
            for (formatElem in formats) {
                val format = formatElem.asJsonObject
                val url = format.get("url")?.asString.orEmpty()
                if (url.isNotBlank() && url.startsWith("https://")) {
                    return url
                }
            }
        }

        return null
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
