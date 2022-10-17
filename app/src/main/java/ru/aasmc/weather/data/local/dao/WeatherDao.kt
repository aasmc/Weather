package ru.aasmc.weather.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import ru.aasmc.weather.data.local.entity.ForecastDB
import ru.aasmc.weather.data.local.entity.ForecastDescriptionDB
import ru.aasmc.weather.data.local.entity.ForecastWithDescription
import ru.aasmc.weather.data.local.entity.WeatherDB
import ru.aasmc.weather.data.local.entity.WeatherDescriptionDB
import ru.aasmc.weather.data.local.entity.WeatherWithDescription

@Dao
abstract class WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertWeather(weather: WeatherDB)

    @Transaction
    @Query("SELECT * FROM weather_table ORDER BY weather_id DESC LIMIT 1")
    abstract fun observeWeather(): Flow<WeatherDB>

    @Query("SELECT * FROM weather_table ORDER BY weather_id DESC LIMIT 1")
    abstract suspend fun getWeather(): WeatherDB

    @Transaction
    @Query("SELECT * FROM weather_table ORDER BY weather_id DESC")
    abstract fun observeAllWeather(): Flow<List<WeatherDB>>

    @Query("SELECT * FROM weather_table ORDER BY weather_id DESC")
    abstract suspend fun getAllWeather(): List<WeatherDB>

    @Query("DELETE FROM weather_table")
    abstract suspend fun deleteAllWeather()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertForecast(forecast: ForecastDB)

    @Query("SELECT * FROM forecast ORDER BY id")
    abstract fun observeAllWeatherForecasts(): Flow<List<ForecastDB>>

    @Query("DELETE FROM forecast")
    abstract suspend fun deleteAllWeatherForecast()

    @Query("SELECT * FROM forecast ORDER BY id")
    abstract suspend fun getAllForecasts(): List<ForecastDB>
}