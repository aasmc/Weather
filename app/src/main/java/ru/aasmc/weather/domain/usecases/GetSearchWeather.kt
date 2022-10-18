package ru.aasmc.weather.domain.usecases

import ru.aasmc.weather.domain.repositories.Repository
import javax.inject.Inject

class GetSearchWeather @Inject constructor(
    private val repository: Repository
) {
    suspend operator fun invoke(location: String) =
        repository.getSearchWeather(location)
}