package ru.aasmc.weather.domain.usecases

import ru.aasmc.weather.domain.model.Weather
import ru.aasmc.weather.domain.repositories.Repository
import javax.inject.Inject

class StoreWeather @Inject constructor(
    private val repository: Repository
) {
    suspend operator fun invoke(weather: Weather) =
        repository.storeWeather(weather)
}