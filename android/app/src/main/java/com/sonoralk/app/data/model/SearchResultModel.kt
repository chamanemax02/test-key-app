package com.sonoralk.app.data.model

data class SearchResultModel(
    val tracks: List<TrackModel> = emptyList(),
    val artists: List<ArtistModel> = emptyList(),
    val albums: List<AlbumModel> = emptyList(),
    val playlists: List<PlaylistModel> = emptyList(),
    val query: String
)
