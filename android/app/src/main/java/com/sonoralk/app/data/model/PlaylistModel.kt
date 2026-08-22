package com.sonoralk.app.data.model

data class PlaylistModel(
    val id: String,
    val name: String,
    val coverImage: String?,
    val trackIds: List<String> = emptyList(),
    val isUserCreated: Boolean = true,
    val createdAt: Long
)
