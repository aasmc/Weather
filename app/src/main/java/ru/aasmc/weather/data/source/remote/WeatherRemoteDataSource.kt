package ru.aasmc.weather.data.source.remote

import ru.aasmc.weather.data.model.LocationModel
import ru.aasmc.weather.data.model.NetworkWeather
import ru.aasmc.weather.data.model.NetworkWeatherForecast
import ru.aasmc.weather.util.Result

interface WeatherRemoteDataSource {
    suspend fun getWeather(location: LocationModel): Result<NetworkWeather>

    suspend fun getWeatherForecast(cityId: Int): Result<List<NetworkWeatherForecast>>

    suspend fun getSearchWeather(query: String): Result<NetworkWeather>
}