package ru.aasmc.weather.domain.repositories

import kotlinx.coroutines.flow.Flow
import ru.aasmc.weather.domain.model.Forecast
import ru.aasmc.weather.domain.model.LocationModel
import ru.aasmc.weather.domain.model.Weather
import ru.aasmc.weather.util.Result

interface Repository {

    fun getWeather(locationModel: LocationModel, refresh: Boolean): Flow<Result<Weather?>>

    fun getForecast(cityId: Int, refresh: Boolean): Flow<Result<List<Forecast>?>>

    fun getSearchWeather(location: String): Flow<Result<Weather?>>

    suspend fun storeWeather(weather: Weather)

    suspend fun storeForecast(forecasts: List<Forecast>)

    suspend fun deleteWeather()

    suspend fun deleteForecast()
}