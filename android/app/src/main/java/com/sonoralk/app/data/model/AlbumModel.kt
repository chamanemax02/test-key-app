package com.sonoralk.app.data.model

data class AlbumModel(
    val id: String,
    val name: String,
    val artistName: String?,
    val imageUrl: String?,
    val releaseDate: String?,
    val trackCount: Int?,
    val spotifyUrl: String?
)
