package lk.sonora.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import lk.sonora.app.player.MusicPlayerManager
import lk.sonora.app.theme.BgPrimary
import lk.sonora.app.theme.SonoraTheme
import lk.sonora.app.ui.components.MiniPlayer
import lk.sonora.app.ui.navigation.BottomNavBar
import lk.sonora.app.ui.navigation.NavGraph
import lk.sonora.app.ui.screens.player.FullPlayerScreen
import lk.sonora.app.ui.screens.player.PlayerViewModel

class MainActivity : ComponentActivity() {

    private val playerViewModel: PlayerViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermissions()

        setContent {
            SonoraTheme {
                val navController = rememberNavController()
                val playbackState by MusicPlayerManager.playbackState.collectAsState()
                var isFullPlayerExpanded by remember { mutableStateOf(false) }

                // Intercept Android hardware/gesture back press when Full Player is open
                BackHandler(enabled = isFullPlayerExpanded) {
                    isFullPlayerExpanded = false
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BgPrimary)
                ) {
                    Scaffold(
                        bottomBar = {
                            BottomNavBar(navController = navController)
                        },
                        containerColor = BgPrimary
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            NavGraph(
                                navController = navController,
                                onNavigateToTrack = {
                                    isFullPlayerExpanded = true
                                }
                            )

                            // Persistent Mini Player above Bottom Bar
                            if (playbackState.currentTrack != null && !isFullPlayerExpanded) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(bottom = 6.dp)
                                ) {
                                    MiniPlayer(
                                        onExpandClick = { isFullPlayerExpanded = true }
                                    )
                                }
                            }
                        }
                    }

                    // Expandable Full Player Screen
                    AnimatedVisibility(
                        visible = isFullPlayerExpanded && playbackState.currentTrack != null,
                        enter = slideInVertically(initialOffsetY = { it }),
                        exit = slideOutVertically(targetOffsetY = { it })
                    ) {
                        FullPlayerScreen(
                            viewModel = playerViewModel,
                            onBackClick = { isFullPlayerExpanded = false }
                        )
                    }
                }
            }
        }
    }

    private fun checkPermissions() {
        val permissions = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_MEDIA_AUDIO)
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        if (permissions.isNotEmpty()) {
            requestPermissionLauncher.launch(permissions.toTypedArray())
        }
    }
}
