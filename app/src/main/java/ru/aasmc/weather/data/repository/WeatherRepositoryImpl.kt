package ru.aasmc.weather.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import ru.aasmc.weather.data.local.database.TransactionRunner
import ru.aasmc.weather.data.local.mapper.WeatherForecastMapperLocal
import ru.aasmc.weather.data.local.mapper.WeatherMapperLocal
import ru.aasmc.weather.data.local.source.WeatherLocalDataSource
import ru.aasmc.weather.data.remote.WeatherRemoteDataSource
import ru.aasmc.weather.data.remote.mapper.WeatherForecastMapperRemote
import ru.aasmc.weather.data.remote.mapper.WeatherMapperRemote
import ru.aasmc.weather.domain.model.Forecast
import ru.aasmc.weather.domain.model.LocationModel
import ru.aasmc.weather.domain.model.Weather
import ru.aasmc.weather.domain.repositories.Repository
import ru.aasmc.weather.util.Result
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val remoteDataSource: WeatherRemoteDataSource,
    private val localDataSource: WeatherLocalDataSource,
    private val transactionRunner: TransactionRunner
) : Repository {

    override fun getWeather(
        locationModel: LocationModel,
        refresh: Boolean
    ): Flow<Result<Weather?>> = flow {
        emit(Result.Loading)
        val localMapper = WeatherMapperLocal()
        val mapperRemote = WeatherMapperRemote()
        if (refresh) {
            when (val response = remoteDataSource.getWeather(locationModel)) {
                is Result.Success -> {
                    if (response.data != null) {
                        val data = response.data
                        val domain = mapperRemote.mapToDomain(data)
                        transactionRunner.invoke {
                            localDataSource.saveWeather(localMapper.mapFromDomain(domain))
                        }
                    }
                }
                is Result.Error -> {
                    emit(Result.Error(response.exception))
                }
                else -> {
                    emit(Result.Loading)
                }
            }
        }
        emitAll(
            localDataSource.observeWeather()
                .map {
                    Result.Success(localMapper.mapToDomain(it))
                }
                .catch {
                    Result.Error(
                        it.cause
                            ?: RuntimeException("Unknown error while fetching weather from DB")
                    )
                }
        )
    }


    override fun getForecast(
        cityId: Int,
        refresh: Boolean
    ): Flow<Result<List<Forecast>?>> = flow {
        emit(Result.Loading)
        val localMapper = WeatherForecastMapperLocal()
        val mapperRemote = WeatherForecastMapperRemote()
        if (refresh) {
            when (val response = remoteDataSource.getWeatherForecast(cityId)) {
                is Result.Error -> emit(Result.Error(response.exception))
                Result.Loading -> emit(Result.Loading)
                is Result.Success -> {
                    if (response.data != null) {
                        val data = response.data
                        val domain = mapperRemote.mapToDomain(data)
                        val dbForecasts = localMapper.mapFromDomain(domain)
                        transactionRunner.invoke {
                            localDataSource.saveAllForecasts(*dbForecasts.toTypedArray())
                        }
                    }
                }
            }
        }
        emitAll(
            localDataSource.observeForecastWeather()
                .map {
                    Result.Success(localMapper.mapToDomain(it))
                }
                .catch {
                    Result.Error(
                        it.cause
                            ?: RuntimeException("Unknown error while retrieving forecast from DB")
                    )
                }
        )
    }

    override fun getSearchWeather(location: String): Flow<Result<Weather?>> = flow {
        emit(Result.Loading)

        val mapper = WeatherMapperRemote()
        when (val response = remoteDataSource.getSearchWeather(location)) {
            is Result.Success -> {
                if (response.data != null) {
                    emit(Result.Success(mapper.mapToDomain(response.data)))
                } else {
                    emit(Result.Success(null))
                }
            }
            is Result.Error -> {
                emit(Result.Error(response.exception))
            }
            else -> emit(Result.Loading)
        }
    }

    override suspend fun storeWeather(weather: Weather) {
        val mapper = WeatherMapperLocal()
        localDataSource.saveWeather(mapper.mapFromDomain(weather))
    }

    override suspend fun storeForecast(forecasts: List<Forecast>) {
        val mapper = WeatherForecastMapperLocal()
        mapper.mapFromDomain(forecasts).let { dbForecasts ->
            dbForecasts.forEach {
                localDataSource.saveForecastWeather(it)
            }
        }
    }

    override suspend fun deleteWeather() {
        localDataSource.deleteWeather()
    }

    override suspend fun deleteForecast() {
        localDataSource.deleteForecastWeather()
    }

}