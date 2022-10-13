package ru.aasmc.weather.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import ru.aasmc.weather.data.remote.model.NetworkWeather
import ru.aasmc.weather.data.remote.model.NetworkWeatherForecastResponse

interface WeatherApiService {
    @GET("/data/2.5/weather")
    suspend fun getSpecificWeather(
        @Query("q") location: String
    ): Response<NetworkWeather>

    // This function gets the weather information for the user's location.
    @GET("/data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double
    ): Response<NetworkWeather>

    // This function gets the weather forecast information for the user's location.
    @GET("data/2.5/forecast")
    suspend fun getWeatherForecast(
        @Query("id") cityId: Int
    ): Response<NetworkWeatherForecastResponse>
}