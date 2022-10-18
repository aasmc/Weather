package ru.aasmc.weather.ui.search

import ru.aasmc.weather.domain.model.Weather

sealed class SearchViewState {
    object Loading: SearchViewState()
    data class Failure(val throwable: Throwable): SearchViewState()
    data class WeatherDetails(val weather: Weather): SearchViewState()
    object Hidden: SearchViewState()
}
