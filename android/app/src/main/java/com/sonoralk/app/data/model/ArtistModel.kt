package com.sonoralk.app.data.model

data class ArtistModel(
    val id: String,
    val name: String,
    val imageUrl: String?,
    val genres: List<String> = emptyList(),
    val spotifyUrl: String?
)
