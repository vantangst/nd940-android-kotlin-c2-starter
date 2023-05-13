package com.udacity.asteroidradar.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.extension.getCurrentDate
import com.udacity.asteroidradar.repository.AsteroidRepository
import retrofit2.HttpException
import java.util.*

class RefreshDataWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }

    override suspend fun doWork(): Result {
        val database = getDatabase(applicationContext)
        val repository = AsteroidRepository(database)
        return try {
            repository.refreshFeed(
                Calendar.getInstance().getCurrentDate(),
                Calendar.getInstance().getCurrentDate(Constants.DEFAULT_END_DATE_DAYS),
                Constants.API_KEY
            )
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }
}