package ru.aasmc.weather.domain.usecases

import ru.aasmc.weather.domain.model.LocationModel
import ru.aasmc.weather.domain.repositories.Repository
import javax.inject.Inject

class GetWeather @Inject constructor(
    private val repository: Repository
) {
    suspend operator fun invoke(
        locationModel: LocationModel,
        refresh: Boolean
    ) = repository.getWeather(locationModel, refresh)
}