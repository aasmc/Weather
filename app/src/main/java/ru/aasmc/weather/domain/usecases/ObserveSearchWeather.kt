package ru.aasmc.weather.domain.usecases

import kotlinx.coroutines.flow.Flow
import ru.aasmc.weather.domain.model.Weather
import ru.aasmc.weather.domain.repositories.Repository
import ru.aasmc.weather.util.Result
import javax.inject.Inject

class ObserveSearchWeather @Inject constructor(
    private val repository: Repository
) {
    operator fun invoke(location: String): Flow<Result<Weather?>> =
        repository.getSearchWeather(location)
}