package com.sonoralk.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sonoralk.app.navigation.Screen
import com.sonoralk.app.theme.SonoraTheme
import com.sonoralk.app.ui.home.HomeScreen
import com.sonoralk.app.ui.search.SearchScreen
import com.sonoralk.app.ui.player.MiniPlayerBar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SonoraTheme {
                SonoraApp()
            }
        }
    }
}

@Composable
fun SonoraApp() {
    val navController = rememberNavController()
    val app = androidx.compose.ui.platform.LocalContext.current.applicationContext as com.sonoralk.app.SonoraApp

    Surface(color = com.sonoralk.app.theme.ThemeColors.BackgroundPrimary) {
        Box(modifier = androidx.compose.ui.Modifier.fillMaxSize()) {
            NavHost(navController = navController, startDestination = Screen.Home.route) {
                composable(Screen.Home.route) {
                    HomeScreen(
                        repository = app.musicRepository,
                        onTrackClick = { track -> app.playbackRepository.play(track) },
                        onNavigate = { route -> navController.navigate(route) }
                    )
                }
                composable(Screen.Search.route) {
                    SearchScreen(
                        repository = app.musicRepository,
                        onTrackClick = { track -> app.playbackRepository.play(track) }
                    )
                }
                // Library, Settings, TrackDetails, Artist, Album, Playlist,
                // FullPlayer, LocalMusic, Downloads, About routes follow the
                // same composable(...) pattern — added as each screen is built.
            }

            MiniPlayerBar(
                playbackRepository = app.playbackRepository,
                onExpand = { navController.navigate(Screen.FullPlayer.route) }
            )
        }
    }
}
