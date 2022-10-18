package ru.aasmc.weather.ui.forecast

import ru.aasmc.weather.domain.model.Forecast

sealed class ForecastViewState {
    object Loading : ForecastViewState()
    data class Failure(val exception: Throwable) : ForecastViewState()
    data class Success(val forecasts: List<Forecast>) : ForecastViewState()
    data class FilteredForecast(val filteredForecasts: List<Forecast>): ForecastViewState()
    object Empty: ForecastViewState()
}
