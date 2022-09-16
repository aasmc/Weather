package ru.aasmc.weather.data.source.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.aasmc.weather.data.model.LocationModel
import ru.aasmc.weather.data.model.Weather
import ru.aasmc.weather.data.model.WeatherForecast
import ru.aasmc.weather.data.source.local.WeatherLocalDataSource
import ru.aasmc.weather.data.source.remote.WeatherRemoteDataSource
import ru.aasmc.weather.di.scope.IoDispatcher
import ru.aasmc.weather.mapper.WeatherForecastMapperLocal
import ru.aasmc.weather.mapper.WeatherForecastMapperRemote
import ru.aasmc.weather.mapper.WeatherMapperLocal
import ru.aasmc.weather.mapper.WeatherMapperRemote
import ru.aasmc.weather.util.Result
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val remoteDataSource: WeatherRemoteDataSource,
    private val localDataSource: WeatherLocalDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : WeatherRepository {
    override suspend fun getWeather(
        location: LocationModel,
        refresh: Boolean
    ): Result<Weather?> = withContext(ioDispatcher) {
        if (refresh) {
            val mapper = WeatherMapperRemote()
            when (val response = remoteDataSource.getWeather(location)) {
                is Result.Success -> {
                    if (response.data != null) {
                        Result.Success(mapper.transformToDomain(response.data))
                    } else {
                        Result.Success(null)
                    }
                }
                is Result.Error -> {
                    Result.Error(response.exception)
                }
                else -> Result.Loading
            }
        } else {
            val mapper = WeatherMapperLocal()
            val forecast = localDataSource.getWeather()
            if (forecast != null) {
                Result.Success(mapper.transformToDomain(forecast))
            } else {
                Result.Success(null)
            }
        }
    }

    override suspend fun getForecastWeather(
        cityId: Int,
        refresh: Boolean
    ): Result<List<WeatherForecast>?> = withContext(ioDispatcher) {
        if (refresh) {
            val mapper = WeatherForecastMapperRemote()
            when (val response = remoteDataSource.getWeatherForecast(cityId)) {
                is Result.Success -> {
                    if (response.data != null) {
                        Result.Success(mapper.transformToDomain(response.data))
                    } else {
                        Result.Success(null)
                    }
                }

                is Result.Error -> {
                    Result.Error(response.exception)
                }
                else -> Result.Loading
            }
        } else {
            val mapper = WeatherForecastMapperLocal()
            val forecast = localDataSource.getForecastWeather()
            if (forecast != null) {
                Result.Success(mapper.transformToDomain(forecast))
            } else {
                Result.Success(null)
            }
        }
    }

    override suspend fun getSearchWeather(location: String): Result<Weather?> =
        withContext(ioDispatcher) {
            val mapper = WeatherMapperRemote()
            return@withContext when(val response =
                remoteDataSource.getSearchWeather(location)) {
                is Result.Success -> {
                    if (response.data != null) {
                        Result.Success(mapper.transformToDomain(response.data))
                    } else {
                        Result.Success(null)
                    }
                }
                is Result.Error -> {
                    Result.Error(response.exception)
                }
                else -> Result.Loading
            }
        }

    override suspend fun storeWeatherData(weather: Weather) = withContext(ioDispatcher) {
        val mapper = WeatherMapperLocal()
        localDataSource.saveWeather(mapper.transformToDto(weather))
    }

    override suspend fun storeForecastData(forecasts: List<WeatherForecast>) =
        withContext(ioDispatcher) {
            val mapper = WeatherForecastMapperLocal()
            mapper.transformToDto(forecasts).let { listOfDpForecast ->
                listOfDpForecast.forEach {
                    localDataSource.saveForecastWeather(it)
                }
            }
        }

    override suspend fun deleteWeatherData() {
        localDataSource.deleteWeather()
    }

    override suspend fun deleteForecastData() {
        localDataSource.deleteForecastWeather()
    }
}