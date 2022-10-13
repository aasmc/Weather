package ru.aasmc.weather.ui.search

sealed class SearchEvent {
    data class SearchForWeather(val name: String): SearchEvent()
}
