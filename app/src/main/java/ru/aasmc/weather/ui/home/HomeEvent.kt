package ru.aasmc.weather.ui.home

import ru.aasmc.weather.domain.model.LocationModel

sealed class HomeEvent {
    data class ObserveWeatherEvent(val location: LocationModel): HomeEvent()
    data class RefreshWeather(val location: LocationModel): HomeEvent()
}
