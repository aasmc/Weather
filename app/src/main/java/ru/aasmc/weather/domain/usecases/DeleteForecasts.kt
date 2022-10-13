package ru.aasmc.weather.domain.usecases

import ru.aasmc.weather.domain.repositories.Repository
import javax.inject.Inject

class DeleteForecasts @Inject constructor(
    private val repository: Repository
) {
    suspend operator fun invoke() =
        repository.deleteForecast()
}