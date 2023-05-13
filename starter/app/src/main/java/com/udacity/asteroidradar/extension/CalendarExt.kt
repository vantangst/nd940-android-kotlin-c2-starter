package com.udacity.asteroidradar.extension

import com.udacity.asteroidradar.Constants
import java.text.SimpleDateFormat
import java.util.*

fun Calendar.getCurrentDate(plush: Int? = null): String {
    plush?.let {
        this.add(Calendar.DAY_OF_YEAR, plush)
    }
    val currentTime = this.time
    val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
    return dateFormat.format(currentTime)
}