package ru.aasmc.weather.domain.usecases

import ru.aasmc.weather.domain.model.Forecast
import ru.aasmc.weather.domain.repositories.Repository
import javax.inject.Inject

class StoreForecasts @Inject constructor(
    private val repository: Repository
) {
    suspend operator fun invoke(forecasts: List<Forecast>) =
        repository.storeForecast(forecasts)
}