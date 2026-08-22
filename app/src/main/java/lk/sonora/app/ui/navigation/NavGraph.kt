package lk.sonora.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import lk.sonora.app.model.Track
import lk.sonora.app.ui.screens.downloads.DownloadsScreen
import lk.sonora.app.ui.screens.home.HomeScreen
import lk.sonora.app.ui.screens.home.HomeViewModel
import lk.sonora.app.ui.screens.library.LibraryScreen
import lk.sonora.app.ui.screens.library.LibraryViewModel
import lk.sonora.app.ui.screens.search.SearchScreen
import lk.sonora.app.ui.screens.search.SearchViewModel
import lk.sonora.app.ui.screens.settings.SettingsScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    onNavigateToTrack: (Track) -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            val homeViewModel: HomeViewModel = viewModel()
            HomeScreen(
                viewModel = homeViewModel,
                onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                onNavigateToTrack = onNavigateToTrack
            )
        }

        composable(Screen.Search.route) {
            val searchViewModel: SearchViewModel = viewModel()
            SearchScreen(
                viewModel = searchViewModel,
                onNavigateToTrack = onNavigateToTrack
            )
        }

        composable(Screen.Library.route) {
            val libraryViewModel: LibraryViewModel = viewModel()
            LibraryScreen(
                viewModel = libraryViewModel,
                onNavigateToTrack = onNavigateToTrack
            )
        }

        composable(Screen.Downloads.route) {
            DownloadsScreen(
                onNavigateToTrack = onNavigateToTrack
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen()
        }
    }
}
