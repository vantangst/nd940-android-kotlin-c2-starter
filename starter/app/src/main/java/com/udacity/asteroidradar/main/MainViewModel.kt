package com.udacity.asteroidradar.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.MediaType
import com.udacity.asteroidradar.domain.Planetary
import com.udacity.asteroidradar.extension.getCurrentDate
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = getDatabase(application)
    private val asteroidRepository = AsteroidRepository(database)

    val asteroidsDataFlow = asteroidRepository.asteroids

    private val _navigateToAsteroidDetailEventFlow = MutableSharedFlow<Asteroid>()
    val navigateToAsteroidDetailEventFlow: SharedFlow<Asteroid>
        get() = _navigateToAsteroidDetailEventFlow

    private val _pictureOfTheDayDataFlow = MutableStateFlow(Planetary())
    val pictureOfTheDayDataFlow: StateFlow<Planetary>
        get() = _pictureOfTheDayDataFlow

    init {
        refreshFeed()
        getImageOfADay()
    }

    private fun refreshFeed() {
        viewModelScope.launch(Dispatchers.IO) {
            asteroidRepository.refreshFeed(
                Calendar.getInstance().getCurrentDate(),
                Calendar.getInstance().getCurrentDate(Constants.DEFAULT_END_DATE_DAYS),
                Constants.API_KEY
            )
        }
    }


    private fun getImageOfADay() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                asteroidRepository.getPlanetaryApod(Constants.API_KEY).let {
                    if (it.media_type == MediaType.Image.value) {
                        _pictureOfTheDayDataFlow.emit(it)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("MainViewModel", "getImageOfADay ERROR: ${e.localizedMessage}")
            }
        }
    }

    fun onAsteroidClicked(asteroid: Asteroid) {
        viewModelScope.launch {
            _navigateToAsteroidDetailEventFlow.emit(asteroid)
        }
    }
}