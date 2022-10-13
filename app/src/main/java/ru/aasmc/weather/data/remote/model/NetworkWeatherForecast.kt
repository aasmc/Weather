package ru.aasmc.weather.data.remote.model

import com.google.gson.annotations.SerializedName

data class NetworkWeatherForecast(
    val id: Int,
    @SerializedName("dt_txt")
    val date: String,

    val wind: NetworkWind,

    @SerializedName("weather")
    val networkWeatherDescription: List<NetworkWeatherDescription>,

    @SerializedName("main")
    val networkWeatherCondition: NetworkWeatherCondition
)