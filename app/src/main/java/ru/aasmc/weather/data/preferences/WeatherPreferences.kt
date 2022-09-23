package ru.aasmc.weather.data.preferences

import kotlinx.coroutines.flow.Flow
import ru.aasmc.weather.data.model.LocationModel

interface WeatherPreferences {

    fun setup()

    var theme: String
    fun observeTheme() : Flow<String>

    var cityId: Int
    fun observeCityId(): Flow<Int>

    var cacheDuration: String
    fun observeCacheDuration(): Flow<String>

    var temperatureUnit: String
    fun observeTemperatureUnit(): Flow<String>

    var location: LocationModel
    fun observeLocation(): Flow<LocationModel>

    var timeOfInitialWeatherFetch: Long
    fun observeTimeOfInitialWeatherFetch(): Flow<Long>

    var timeOfInitialWeatherForecastFetch: Long
    fun observeTimeOfInitialWeatherForecastFetch(): Flow<Long>
}