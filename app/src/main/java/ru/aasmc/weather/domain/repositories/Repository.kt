package ru.aasmc.weather.domain.repositories

import kotlinx.coroutines.flow.Flow
import ru.aasmc.weather.domain.model.Forecast
import ru.aasmc.weather.domain.model.LocationModel
import ru.aasmc.weather.domain.model.Weather
import ru.aasmc.weather.util.Result

interface Repository {

    fun observeWeather(locationModel: LocationModel, refresh: Boolean): Flow<Result<Weather?>>

    suspend fun getWeather(locationModel: LocationModel, refresh: Boolean): Result<Weather?>

    fun observeForecasts(cityId: Int, refresh: Boolean): Flow<Result<List<Forecast>?>>

    suspend fun getForecasts(cityId: Int, refresh: Boolean): Result<List<Forecast>?>

    fun observeSearchWeather(location: String): Flow<Result<Weather?>>

    suspend fun getSearchWeather(location: String): Result<Weather?>

    suspend fun storeWeather(weather: Weather)

    suspend fun storeForecast(forecasts: List<Forecast>)

    suspend fun deleteWeather()

    suspend fun deleteForecast()

    suspend fun getAllForecasts(): List<Forecast>
}