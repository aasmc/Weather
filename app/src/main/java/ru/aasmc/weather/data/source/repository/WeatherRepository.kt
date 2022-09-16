package ru.aasmc.weather.data.source.repository

import ru.aasmc.weather.data.model.LocationModel
import ru.aasmc.weather.data.model.Weather
import ru.aasmc.weather.data.model.WeatherForecast
import ru.aasmc.weather.util.Result

interface WeatherRepository {

    suspend fun getWeather(location: LocationModel, refresh: Boolean): Result<Weather?>

    suspend fun getForecastWeather(cityId: Int, refresh: Boolean): Result<List<WeatherForecast>?>

    suspend fun getSearchWeather(location: String): Result<Weather?>

    suspend fun storeWeatherData(weather: Weather)

    suspend fun storeForecastData(forecasts: List<WeatherForecast>)

    suspend fun deleteWeatherData()

    suspend fun deleteForecastData()
}