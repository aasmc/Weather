package ru.aasmc.weather.ui.home

import ru.aasmc.weather.domain.model.Weather

sealed class HomeViewState {
    data class WeatherDetails(val weather: Weather): HomeViewState()
    object Loading : HomeViewState()
    object Failure : HomeViewState()
    object Empty: HomeViewState()
}