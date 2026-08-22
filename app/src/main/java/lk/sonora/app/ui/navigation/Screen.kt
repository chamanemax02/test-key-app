package lk.sonora.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import lk.sonora.app.R

sealed class Screen(
    val route: String,
    val titleResId: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : Screen("home", R.string.nav_home, Icons.Filled.Home, Icons.Outlined.Home)
    object Search : Screen("search", R.string.nav_search, Icons.Filled.Search, Icons.Outlined.Search)
    object Library : Screen("library", R.string.nav_library, Icons.Filled.LibraryMusic, Icons.Outlined.LibraryMusic)
    object Downloads : Screen("downloads", R.string.nav_downloads, Icons.Filled.Download, Icons.Outlined.Download)
    object Settings : Screen("settings", R.string.nav_settings, Icons.Filled.Settings, Icons.Outlined.Settings)

    companion object {
        val bottomNavItems = listOf(Home, Search, Library, Downloads, Settings)
    }
}
