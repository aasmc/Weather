package ru.aasmc.weather.data.source.local

import ru.aasmc.weather.data.source.local.entity.DBWeather
import ru.aasmc.weather.data.source.local.entity.DBWeatherForecast

interface WeatherLocalDataSource {
    suspend fun getWeather(): DBWeather?

    suspend fun saveWeather(weather: DBWeather)

    suspend fun deleteWeather()

    suspend fun getForecastWeather(): List<DBWeatherForecast>?

    suspend fun saveForecastWeather(weatherForecast: DBWeatherForecast)

    suspend fun deleteForecastWeather()
}