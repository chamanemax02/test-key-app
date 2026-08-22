package com.sonoralk.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Last-known-good API responses, kept so the UI has something to show
 *  when the network is down instead of a blank screen. */
@Entity(tableName = "cached_metadata")
data class CachedMetadataEntity(
    @PrimaryKey val cacheKey: String,
    val jsonPayload: String,
    val cachedAt: Long
)
