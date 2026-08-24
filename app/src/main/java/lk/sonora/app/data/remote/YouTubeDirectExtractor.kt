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
import java.net.URLDecoder
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

        // Method 1: YouTube Android Music Client InnerTube
        val clients = listOf(
            Triple("ANDROID_MUSIC", "6.40.52", "67"),
            Triple("ANDROID_TESTSUITE", "1.9", "3"),
            Triple("TVHTML5_SIMPLY_EMBEDDED_PLAYER", "2.0", "85"),
            Triple("IOS", "19.29.1", "5")
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

                val reqBuilder = Request.Builder()
                    .url("https://www.youtube.com/youtubei/v1/player?prettyPrint=false")
                    .post(payload.toString().toRequestBody(jsonMediaType))
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .header("X-YouTube-Client-Name", clientHeader)
                    .header("X-YouTube-Client-Version", clientVersion)

                if (clientName == "TVHTML5_SIMPLY_EMBEDDED_PLAYER") {
                    reqBuilder.header("Origin", "https://www.youtube.com")
                    reqBuilder.header("Referer", "https://www.youtube.com/")
                }

                val response = httpClient.newCall(reqBuilder.build()).execute()
                if (response.isSuccessful) {
                    val body = response.body?.string().orEmpty()
                    val json = gson.fromJson(body, JsonObject::class.java)
                    val url = parseBestAudioStream(json)
                    if (!url.isNullOrBlank()) return@withContext url
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Method 2: Piped and Invidious CDN Instances (return direct googlevideo.com links)
        val endpoints = listOf(
            "https://pipedapi.kavin.rocks/streams/$videoId",
            "https://api.piped.yt/streams/$videoId",
            "https://inv.nadeko.net/api/v1/videos/$videoId",
            "https://invidious.nerdvpn.de/api/v1/videos/$videoId",
            "https://yewtu.be/api/v1/videos/$videoId"
        )

        for (endpoint in endpoints) {
            try {
                val req = Request.Builder()
                    .url(endpoint)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .build()
                val resp = httpClient.newCall(req).execute()
                if (resp.isSuccessful) {
                    val respBody = resp.body?.string().orEmpty()
                    val json = gson.fromJson(respBody, JsonObject::class.java)

                    val audioStreams = json.getAsJsonArray("audioStreams")
                    if (audioStreams != null && audioStreams.size() > 0) {
                        val firstAudioUrl = audioStreams[0].asJsonObject.get("url")?.asString
                        if (!firstAudioUrl.isNullOrBlank()) return@withContext firstAudioUrl
                    }

                    val adaptiveFormats = json.getAsJsonArray("adaptiveFormats")
                    if (adaptiveFormats != null) {
                        for (elem in adaptiveFormats) {
                            val format = elem.asJsonObject
                            val type = format.get("type")?.asString.orEmpty()
                            val u = format.get("url")?.asString.orEmpty()
                            if (type.startsWith("audio/") && u.isNotBlank()) {
                                return@withContext u
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Method 3: Fallback to Koyeb API stream extractor
        try {
            val req = Request.Builder()
                .url("https://chama-movie-api.koyeb.app/api/v1/youtube/download?url=https://www.youtube.com/watch?v=$videoId&api_key=chama_api_7f4ac9c10c749bcedbd4437a066009a2")
                .build()
            val resp = httpClient.newCall(req).execute()
            if (resp.isSuccessful) {
                val json = gson.fromJson(resp.body?.string().orEmpty(), JsonObject::class.java)
                val data = json.getAsJsonObject("data") ?: json.getAsJsonObject("result")
                val downloadUrl = data?.get("direct_url")?.asString ?: data?.get("download_url")?.asString
                if (!downloadUrl.isNullOrBlank()) return@withContext downloadUrl
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        null
    }

    private fun parseBestAudioStream(json: JsonObject): String? {
        val streamingData = json.getAsJsonObject("streamingData") ?: return null

        val adaptiveFormats = streamingData.getAsJsonArray("adaptiveFormats")
        if (adaptiveFormats != null && adaptiveFormats.size() > 0) {
            var bestUrl: String? = null
            var highestBitrate = 0

            for (formatElem in adaptiveFormats) {
                val format = formatElem.asJsonObject
                val mimeType = format.get("mimeType")?.asString.orEmpty()
                val bitrate = format.get("bitrate")?.asInt ?: 0

                var streamUrl = format.get("url")?.asString
                if (streamUrl.isNullOrBlank()) {
                    val cipher = format.get("signatureCipher")?.asString ?: format.get("cipher")?.asString
                    if (!cipher.isNullOrBlank()) {
                        try {
                            val params = cipher.split("&").associate {
                                val p = it.split("=")
                                if (p.size == 2) URLDecoder.decode(p[0], "UTF-8") to URLDecoder.decode(p[1], "UTF-8")
                                else "" to ""
                            }
                            val rawUrl = params["url"]
                            val sig = params["s"] ?: params["sig"]
                            val sp = params["sp"] ?: "signature"
                            if (!rawUrl.isNullOrBlank()) {
                                streamUrl = if (!sig.isNullOrBlank()) "$rawUrl&$sp=$sig" else rawUrl
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                if (mimeType.startsWith("audio/") && !streamUrl.isNullOrBlank()) {
                    if (bitrate > highestBitrate) {
                        highestBitrate = bitrate
                        bestUrl = streamUrl
                    }
                }
            }

            if (!bestUrl.isNullOrBlank()) return bestUrl
        }

        val formats = streamingData.getAsJsonArray("formats")
        if (formats != null && formats.size() > 0) {
            for (formatElem in formats) {
                val format = formatElem.asJsonObject
                val url = format.get("url")?.asString
                if (!url.isNullOrBlank()) return url
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
