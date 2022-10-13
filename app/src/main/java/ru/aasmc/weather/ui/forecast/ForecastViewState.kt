package ru.aasmc.weather.ui.forecast

import ru.aasmc.weather.domain.model.Forecast

sealed class ForecastViewState {
    object Loading : ForecastViewState()
    object Failure : ForecastViewState()
    data class Success(val forecasts: List<Forecast>) : ForecastViewState()
    data class FilteredForecast(val filteredForecasts: List<Forecast>): ForecastViewState()
    object Empty: ForecastViewState()
}
