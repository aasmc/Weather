package ru.aasmc.weather.data.remote.model

import com.google.gson.annotations.SerializedName

data class NetworkWeather(
    val uId: Int,
    @SerializedName("id")
    val cityId: Int,
    val name: String,
    val wind: NetworkWind,
    @SerializedName("weather")
    val networkWeatherDescriptions: List<NetworkWeatherDescription>,
    @SerializedName("main")
    val networkWeatherCondition: NetworkWeatherCondition
)
