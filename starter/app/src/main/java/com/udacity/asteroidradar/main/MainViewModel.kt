package com.udacity.asteroidradar.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = getDatabase(application)
    private val asteroidRepository = AsteroidRepository(database)

    init {
        getFeed()
    }

    private fun getFeed() {
        viewModelScope.launch(Dispatchers.IO) {
            asteroidRepository.refreshFeed(getDate(), getDate(Constants.DEFAULT_END_DATE_DAYS), Constants.API_KEY)
        }
    }

    private fun getDate(plush: Int? = null): String {
        val calendar = Calendar.getInstance()
        plush?.let {
            calendar.add(Calendar.DAY_OF_YEAR, plush)
        }
        val currentTime = calendar.time
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        return dateFormat.format(currentTime)
    }
}