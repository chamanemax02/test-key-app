package com.sonoralk.app.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Search : Screen("search")
    object Library : Screen("library")
    object Settings : Screen("settings")

    object TrackDetails : Screen("track/{trackId}") {
        fun createRoute(trackId: String) = "track/$trackId"
    }
    object Artist : Screen("artist/{artistId}") {
        fun createRoute(artistId: String) = "artist/$artistId"
    }
    object Album : Screen("album/{albumId}") {
        fun createRoute(albumId: String) = "album/$albumId"
    }
    object Playlist : Screen("playlist/{playlistId}") {
        fun createRoute(playlistId: String) = "playlist/$playlistId"
    }
    object FullPlayer : Screen("player")
    object LocalMusic : Screen("local_music")
    object Downloads : Screen("downloads")
    object About : Screen("about")
}

// NOTE: full NavHost wiring (composable(route){...} per Screen, plus
// deep-link intent-filters in AndroidManifest for sonoralk://track/{id}
// etc.) is the next file to flesh out once screen composables exist —
// each screen below is stubbed as a real composable, not a mockup.
