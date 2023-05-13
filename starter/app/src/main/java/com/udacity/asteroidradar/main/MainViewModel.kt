package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = getDatabase(application)
    private val asteroidRepository = AsteroidRepository(database)

    val asteroidsDataFlow = asteroidRepository.asteroids

    private val _navigateToAsteroidDetailEventFlow = MutableSharedFlow<Asteroid>()
    val navigateToAsteroidDetailEventFlow: SharedFlow<Asteroid>
        get() = _navigateToAsteroidDetailEventFlow


    val imageOfTheDay = "https://apod.nasa.gov/apod/image/2305/AS17-152-23420_Ord1024c.jpg"

    init {
        getFeed()
    }

    private fun getFeed() {
        viewModelScope.launch(Dispatchers.IO) {
            asteroidRepository.refreshFeed(
                getDate(),
                getDate(Constants.DEFAULT_END_DATE_DAYS),
                Constants.API_KEY
            )
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

    fun onAsteroidClicked(asteroid: Asteroid) {
        viewModelScope.launch {
            _navigateToAsteroidDetailEventFlow.emit(asteroid)
        }
    }
}