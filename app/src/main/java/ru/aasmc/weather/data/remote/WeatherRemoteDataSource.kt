package ru.aasmc.weather.data.remote

import ru.aasmc.weather.data.remote.model.NetworkWeather
import ru.aasmc.weather.data.remote.model.NetworkWeatherForecast
import ru.aasmc.weather.domain.model.LocationModel
import ru.aasmc.weather.util.Result

interface WeatherRemoteDataSource {
    suspend fun getWeather(location: LocationModel): Result<NetworkWeather>

    suspend fun getWeatherForecast(cityId: Int): Result<List<NetworkWeatherForecast>>

    suspend fun getSearchWeather(query: String): Result<NetworkWeather>
}