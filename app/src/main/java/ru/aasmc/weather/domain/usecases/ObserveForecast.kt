package ru.aasmc.weather.domain.usecases

import kotlinx.coroutines.flow.Flow
import ru.aasmc.weather.domain.model.Forecast
import ru.aasmc.weather.domain.repositories.Repository
import ru.aasmc.weather.util.Result
import javax.inject.Inject

class ObserveForecast @Inject constructor(
    private val repo: Repository
) {
    operator fun invoke(cityId: Int, refresh: Boolean): Flow<Result<List<Forecast>?>> =
        repo.observeForecasts(cityId, refresh)
}