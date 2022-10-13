package ru.aasmc.weather.domain.usecases

import kotlinx.coroutines.flow.Flow
import ru.aasmc.weather.domain.model.LocationModel
import ru.aasmc.weather.domain.model.Weather
import ru.aasmc.weather.domain.repositories.Repository
import ru.aasmc.weather.util.Result
import javax.inject.Inject

class ObserveWeather @Inject constructor(
    private val repo: Repository
) {
    operator fun invoke(
        locationModel: LocationModel,
        refresh: Boolean
    ): Flow<Result<Weather?>> =
        repo.getWeather(locationModel, refresh)
}