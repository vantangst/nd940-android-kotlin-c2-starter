package com.udacity.asteroidradar.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Planetary(
    val url: String = "",
    val media_type: String = "",
    val title: String = ""
) : Parcelable

enum class MediaType(val value: String) {
    Image("image"),
    Video("video")
}