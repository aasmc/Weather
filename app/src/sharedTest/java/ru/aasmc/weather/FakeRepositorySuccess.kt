package ru.aasmc.weather

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.aasmc.weather.domain.model.Forecast
import ru.aasmc.weather.domain.model.LocationModel
import ru.aasmc.weather.domain.model.Weather
import ru.aasmc.weather.domain.repositories.Repository
import ru.aasmc.weather.util.Result

class FakeRepositorySuccess : Repository {
    override fun observeWeather(
        locationModel: LocationModel,
        refresh: Boolean
    ): Flow<Result<Weather?>> = flow {
        emit(Result.Success(fakeWeather))
    }

    override suspend fun getWeather(
        locationModel: LocationModel,
        refresh: Boolean
    ): Result<Weather?> {
        return Result.Success(fakeWeather)
    }

    override fun observeForecasts(
        cityId: Int,
        refresh: Boolean
    ): Flow<Result<List<Forecast>?>> = flow {
        emit(Result.Success(
            listOf(
                fakeWeatherForecast,
                fakeWeatherForecast,
                fakeWeatherForecast,
                fakeWeatherForecast
            )
        ))
    }

    override suspend fun getForecasts(
        cityId: Int,
        refresh: Boolean
    ): Result<List<Forecast>?> {
        return Result.Success(
            listOf(
                fakeWeatherForecast,
                fakeWeatherForecast,
                fakeWeatherForecast,
                fakeWeatherForecast
            )
        )
    }

    override fun observeSearchWeather(location: String): Flow<Result<Weather?>> {
        TODO("Not yet implemented")
    }

    override suspend fun getSearchWeather(location: String): Result<Weather?> {
        TODO("Not yet implemented")
    }

    override suspend fun storeWeather(weather: Weather) {
        TODO("Not yet implemented")
    }

    override suspend fun storeForecast(forecasts: List<Forecast>) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteWeather() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteForecast() {
        TODO("Not yet implemented")
    }

    override suspend fun getAllForecasts(): List<Forecast> {
        TODO("Not yet implemented")
    }
}