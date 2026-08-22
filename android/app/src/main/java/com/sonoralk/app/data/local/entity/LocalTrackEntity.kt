package com.sonoralk.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Index of audio files the user has imported from device storage. */
@Entity(tableName = "local_tracks")
data class LocalTrackEntity(
    @PrimaryKey val id: String,
    val filePath: String,
    val title: String,
    val artist: String?,
    val album: String?,
    val durationMs: Long,
    val albumArtPath: String?,
    val importedAt: Long
)
