package lk.sonora.app.player

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import lk.sonora.app.model.PlayerStatus

object YouTubeWebStreamEngine {

    private var webView: WebView? = null
    private val mainHandler = Handler(Looper.getMainLooper())
    private var isReady = false
    private var pendingVideoId: String? = null

    @SuppressLint("SetJavaScriptEnabled")
    fun init(context: Context) {
        if (webView != null) return
        mainHandler.post {
            try {
                val wv = WebView(context.applicationContext)
                val settings = wv.settings
                settings.javaScriptEnabled = true
                settings.mediaPlaybackRequiresUserGesture = false
                settings.domStorageEnabled = true
                settings.databaseEnabled = true
                settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                settings.userAgentString = "Mozilla/5.0 (Linux; Android 13; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"

                wv.webChromeClient = WebChromeClient()
                wv.webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        isReady = true
                        pendingVideoId?.let {
                            loadVideo(it)
                            pendingVideoId = null
                        }
                    }
                }

                wv.addJavascriptInterface(SonoraJsBridge, "SonoraBridge")
                wv.loadDataWithBaseURL("https://www.youtube.com", buildHtml(), "text/html", "UTF-8", null)
                webView = wv
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadVideo(videoId: String) {
        mainHandler.post {
            if (!isReady || webView == null) {
                pendingVideoId = videoId
                return@post
            }
            webView?.evaluateJavascript("playTrack('$videoId');", null)
        }
    }

    fun play() {
        mainHandler.post {
            webView?.evaluateJavascript("resumeTrack();", null)
        }
    }

    fun pause() {
        mainHandler.post {
            webView?.evaluateJavascript("pauseTrack();", null)
        }
    }

    fun seekTo(positionMs: Long) {
        mainHandler.post {
            val sec = positionMs / 1000f
            webView?.evaluateJavascript("seekTrack($sec);", null)
        }
    }

    fun release() {
        mainHandler.post {
            try {
                webView?.destroy()
                webView = null
                isReady = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun buildHtml(): String {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                <style>
                    body { margin: 0; padding: 0; background: #000; overflow: hidden; width: 100%; height: 100%; }
                    #player { width: 100%; height: 100%; position: absolute; top:0; left:0; }
                </style>
            </head>
            <body>
                <div id="player"></div>
                <script>
                    var tag = document.createElement('script');
                    tag.src = "https://www.youtube.com/iframe_api";
                    var firstScriptTag = document.getElementsByTagName('script')[0];
                    firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);

                    var player;
                    var timeInterval;

                    function onYouTubeIframeAPIReady() {
                        player = new YT.Player('player', {
                            height: '100%',
                            width: '100%',
                            playerVars: {
                                'autoplay': 1,
                                'playsinline': 1,
                                'controls': 0,
                                'rel': 0,
                                'origin': 'https://www.youtube.com',
                                'enablejsapi': 1,
                                'fs': 0,
                                'disablekb': 1
                            },
                            events: {
                                'onReady': onPlayerReady,
                                'onStateChange': onPlayerStateChange,
                                'onError': onPlayerError
                            }
                        });
                    }

                    function onPlayerReady(event) {
                        SonoraBridge.onPlayerReady();
                        if (timeInterval) clearInterval(timeInterval);
                        timeInterval = setInterval(function() {
                            if (player && player.getCurrentTime) {
                                var cur = player.getCurrentTime();
                                var dur = player.getDuration ? player.getDuration() : 0;
                                SonoraBridge.onTimeUpdate(cur, dur);
                            }
                        }, 250);
                    }

                    function onPlayerStateChange(event) {
                        var dur = player && player.getDuration ? player.getDuration() : 0;
                        SonoraBridge.onStateChange(event.data, dur);
                    }

                    function onPlayerError(event) {
                        SonoraBridge.onError(event.data);
                    }

                    function playTrack(vid) {
                        if (player && player.loadVideoById) {
                            player.loadVideoById(vid);
                        }
                    }

                    function pauseTrack() {
                        if (player && player.pauseVideo) {
                            player.pauseVideo();
                        }
                    }

                    function resumeTrack() {
                        if (player && player.playVideo) {
                            player.playVideo();
                        }
                    }

                    function seekTrack(sec) {
                        if (player && player.seekTo) {
                            player.seekTo(sec, true);
                        }
                    }
                </script>
            </body>
            </html>
        """.trimIndent()
    }

    object SonoraJsBridge {
        @JavascriptInterface
        fun onPlayerReady() {
            // Player initialized
        }

        @JavascriptInterface
        fun onStateChange(state: Int, durationSec: Float) {
            val durMs = (durationSec * 1000).toLong()
            when (state) {
                -1 -> { // UNSTARTED
                    MusicPlayerManager.onEngineStatusUpdate(PlayerStatus.BUFFERING, durMs)
                }
                0 -> { // ENDED
                    MusicPlayerManager.onEngineTrackEnded()
                }
                1 -> { // PLAYING
                    MusicPlayerManager.onEngineStatusUpdate(PlayerStatus.PLAYING, durMs)
                }
                2 -> { // PAUSED
                    MusicPlayerManager.onEngineStatusUpdate(PlayerStatus.PAUSED, durMs)
                }
                3 -> { // BUFFERING
                    MusicPlayerManager.onEngineStatusUpdate(PlayerStatus.BUFFERING, durMs)
                }
                5 -> { // CUED
                    MusicPlayerManager.onEngineStatusUpdate(PlayerStatus.PAUSED, durMs)
                }
            }
        }

        @JavascriptInterface
        fun onTimeUpdate(currentSec: Float, durationSec: Float) {
            val posMs = (currentSec * 1000).toLong()
            val durMs = (durationSec * 1000).toLong()
            MusicPlayerManager.onEnginePositionUpdate(posMs, durMs)
        }

        @JavascriptInterface
        fun onError(errorCode: Int) {
            MusicPlayerManager.onEngineError("YouTube Player Notice ($errorCode)")
        }
    }
}
