package ru.aasmc.weather.data.local.source

import kotlinx.coroutines.flow.Flow
import ru.aasmc.weather.data.local.dao.WeatherDao
import ru.aasmc.weather.data.local.entity.ForecastDB
import ru.aasmc.weather.data.local.entity.ForecastWithDescription
import ru.aasmc.weather.data.local.entity.WeatherDB
import ru.aasmc.weather.data.local.entity.WeatherWithDescription

interface WeatherLocalDataSource {

    fun observeWeather(): Flow<WeatherDB>

    suspend fun saveWeather(weather: WeatherDB)

    suspend fun deleteWeather()

    fun observeForecastWeather(): Flow<List<ForecastDB>>

    suspend fun saveForecastWeather(weatherForecast: ForecastDB)

    suspend fun saveAllForecasts(vararg forecasts: ForecastDB)

    suspend fun deleteForecastWeather()
}