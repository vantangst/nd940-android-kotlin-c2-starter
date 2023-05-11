package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AsteroidDao {
    @Query("select * from asteroid_table")
    fun getAsteroids(): LiveData<List<AsteroidEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: AsteroidEntity)
}

@Database(entities = [AsteroidEntity::class], version = 1)
abstract class RadarDatabase : RoomDatabase() {
    abstract val asteroid: AsteroidDao
}

private lateinit var INSTANCE: RadarDatabase

fun getDatabase(context: Context): RadarDatabase {
    synchronized(RadarDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                RadarDatabase::class.java,
                "radar_database").build()
        }
    }
    return INSTANCE
}