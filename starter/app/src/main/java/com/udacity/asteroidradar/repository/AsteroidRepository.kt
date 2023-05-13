package com.udacity.asteroidradar.repository

import android.util.Log
import com.udacity.asteroidradar.api.RadarApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.RadarDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.Planetary
import com.udacity.asteroidradar.domain.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AsteroidRepository(private val database: RadarDatabase) {

    fun getAsteroids(startDate: String? = null, endDate: String? = null): Flow<List<Asteroid>> {
        val asteroids = startDate?.let { startTime ->
            endDate?.let { endTime ->
                database.asteroid.getAsteroids(startTime, endTime)
            } ?: run {
                database.asteroid.getAsteroids(startTime)
            }
        } ?: run {
            database.asteroid.getAsteroids()
        }
        return asteroids.map { it.asDomainModel() }
    }

    suspend fun refreshFeed(startDate: String, endDate: String, apiKey: String) {
        withContext(Dispatchers.IO) {
            try {
                val feedJsonString = RadarApi.feed.getFeed(startDate, endDate, apiKey)
                val feedJson = JSONObject(feedJsonString)
                Log.d("AsteroidRepository", "refreshFeed: $feedJsonString")
                val listData = parseAsteroidsJsonResult(feedJson)
                database.asteroid.insertAll(*listData.asDatabaseModel())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun getPlanetaryApod(apiKey: String): Planetary {
        return withContext(Dispatchers.IO) {
            RadarApi.nasa.getPlanetaryApod(apiKey)
        }
    }
}