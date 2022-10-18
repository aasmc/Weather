package ru.aasmc.weather.domain.usecases

import ru.aasmc.weather.domain.repositories.Repository
import javax.inject.Inject

class GetForecasts @Inject constructor(
    private val repository: Repository
) {
    suspend operator fun invoke(cityId: Int, refresh: Boolean) =
        repository.getForecasts(cityId, refresh)
}