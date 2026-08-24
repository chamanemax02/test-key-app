package lk.sonora.app.player

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import lk.sonora.app.model.PlayerStatus

object YouTubeAudioPlayerBridge {

    private var webView: WebView? = null
    private val mainHandler = Handler(Looper.getMainLooper())
    private var isReady = false
    private var pendingVideoId: String? = null

    @SuppressLint("SetJavaScriptEnabled")
    fun attachWebView(wv: WebView) {
        this.webView = wv
        mainHandler.post {
            try {
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
                            playVideo(it)
                            pendingVideoId = null
                        }
                    }
                }

                wv.addJavascriptInterface(BridgeInterface, "SonoraBridge")
                wv.loadDataWithBaseURL("https://www.youtube.com", buildHtml(), "text/html", "UTF-8", null)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun playVideo(videoId: String) {
        mainHandler.post {
            if (!isReady || webView == null) {
                pendingVideoId = videoId
                return@post
            }
            webView?.evaluateJavascript("playSong('$videoId');", null)
        }
    }

    fun resume() {
        mainHandler.post {
            webView?.evaluateJavascript("resumeSong();", null)
        }
    }

    fun pause() {
        mainHandler.post {
            webView?.evaluateJavascript("pauseSong();", null)
        }
    }

    fun seekTo(positionMs: Long) {
        mainHandler.post {
            val sec = positionMs / 1000f
            webView?.evaluateJavascript("seekSong($sec);", null)
        }
    }

    private fun buildHtml(): String {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>body{margin:0;padding:0;background:#000;overflow:hidden;width:100%;height:100%;}</style>
            </head>
            <body>
                <div id="player"></div>
                <script>
                    var tag = document.createElement('script');
                    tag.src = "https://www.youtube.com/iframe_api";
                    var firstScriptTag = document.getElementsByTagName('script')[0];
                    firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);

                    var player;
                    var timeTimer;

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
                                'fs': 0
                            },
                            events: {
                                'onReady': onPlayerReady,
                                'onStateChange': onPlayerStateChange,
                                'onError': onPlayerError
                            }
                        });
                    }

                    function onPlayerReady(event) {
                        SonoraBridge.onReady();
                        if (timeTimer) clearInterval(timeTimer);
                        timeTimer = setInterval(function() {
                            if (player && player.getCurrentTime) {
                                var cur = player.getCurrentTime();
                                var dur = player.getDuration ? player.getDuration() : 0;
                                SonoraBridge.onTime(cur, dur);
                            }
                        }, 250);
                    }

                    function onPlayerStateChange(event) {
                        var dur = player && player.getDuration ? player.getDuration() : 0;
                        SonoraBridge.onState(event.data, dur);
                    }

                    function onPlayerError(event) {
                        SonoraBridge.onError(event.data);
                    }

                    function playSong(vid) {
                        if (player && player.loadVideoById) {
                            player.loadVideoById({
                                videoId: vid,
                                suggestedQuality: 'small'
                            });
                        }
                    }

                    function pauseSong() {
                        if (player && player.pauseVideo) {
                            player.pauseVideo();
                        }
                    }

                    function resumeSong() {
                        if (player && player.playVideo) {
                            player.playVideo();
                        }
                    }

                    function seekSong(sec) {
                        if (player && player.seekTo) {
                            player.seekTo(sec, true);
                        }
                    }
                </script>
            </body>
            </html>
        """.trimIndent()
    }

    object BridgeInterface {
        @JavascriptInterface
        fun onReady() {
            // Bridge is ready
        }

        @JavascriptInterface
        fun onState(state: Int, durationSec: Float) {
            val durMs = (durationSec * 1000).toLong()
            when (state) {
                0 -> { // ENDED
                    MusicPlayerManager.onAudioEnded()
                }
                1 -> { // PLAYING
                    MusicPlayerManager.onAudioStatusChange(PlayerStatus.PLAYING, durMs)
                }
                2 -> { // PAUSED
                    MusicPlayerManager.onAudioStatusChange(PlayerStatus.PAUSED, durMs)
                }
                3 -> { // BUFFERING
                    MusicPlayerManager.onAudioStatusChange(PlayerStatus.BUFFERING, durMs)
                }
            }
        }

        @JavascriptInterface
        fun onTime(curSec: Float, durSec: Float) {
            val curMs = (curSec * 1000).toLong()
            val durMs = (durSec * 1000).toLong()
            MusicPlayerManager.onAudioProgress(curMs, durMs)
        }

        @JavascriptInterface
        fun onError(code: Int) {
            // Notice received
        }
    }
}
