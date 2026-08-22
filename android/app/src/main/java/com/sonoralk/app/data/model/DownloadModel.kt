package com.sonoralk.app.data.model

enum class DownloadState { NOT_DOWNLOADED, QUEUED, DOWNLOADING, COMPLETED, FAILED }

data class DownloadModel(
    val trackId: String,
    val state: DownloadState,
    val progressPercent: Int = 0,
    val localFilePath: String? = null,
    // Gate checked before a download can ever start — see DownloadManager.
    val downloadAllowed: Boolean,
    val licenseStatus: LicenseStatus
)
