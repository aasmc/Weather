package ru.aasmc.weather.ui.home

import ru.aasmc.weather.domain.model.Weather

sealed class HomeViewState {
    data class WeatherDetails(val weather: Weather): HomeViewState()
    object Loading : HomeViewState()
    data class Failure(val throwable: Throwable) : HomeViewState()
    object Empty: HomeViewState()
}