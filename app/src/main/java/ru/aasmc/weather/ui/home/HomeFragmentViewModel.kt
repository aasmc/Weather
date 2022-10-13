package ru.aasmc.weather.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.aasmc.weather.domain.model.LocationModel
import ru.aasmc.weather.domain.model.Weather
import ru.aasmc.weather.domain.usecases.ObserveWeather
import ru.aasmc.weather.util.LocationLiveData
import ru.aasmc.weather.util.Result
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeFragmentViewModel @Inject constructor(
    private val observeWeather: ObserveWeather,
) : ViewModel() {
    @Inject
    lateinit var locationLiveData: LocationLiveData

    private val _homeViewState: MutableStateFlow<HomeViewState> =
        MutableStateFlow(HomeViewState.Empty)
    val homeViewState: StateFlow<HomeViewState> = _homeViewState.asStateFlow()

    init {
        currentSystemTime()
    }

    fun handleEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.ObserveWeatherEvent -> {
                handleObserveEvent(event.location)
            }
            is HomeEvent.RefreshWeather -> {
                handleRefreshEvent(event.location)
            }
        }
    }

    private fun handleObserveEvent(location: LocationModel) {
        viewModelScope.launch {
            observeWeather(location, false)
                .collect { result ->
                    updateViewState(
                        location,
                        result,
                        true
                    ) { weather ->
                        _homeViewState.update {
                            HomeViewState.WeatherDetails(weather)
                        }
                    }
                }
        }
    }

    private fun handleRefreshEvent(location: LocationModel) {
        viewModelScope.launch {
            observeWeather(location, true)
                .collect { result ->
                    updateViewState(
                        location,
                        result,
                        false
                    ) { weather ->
                        _homeViewState.update {
                            HomeViewState.WeatherDetails(weather)
                        }
                    }
                }
        }
    }

    private fun updateViewState(
        location: LocationModel,
        result: Result<Weather?>,
        shouldRefreshOnFailure: Boolean,
        onResult: (Weather) -> Unit
    ) {
        when (result) {
            is Result.Error -> {
                _homeViewState.update {
                    HomeViewState.Failure
                }
            }
            Result.Loading -> {
                _homeViewState.update {
                    HomeViewState.Loading
                }
            }
            is Result.Success -> {
                if (result.data != null) {
                    onResult(result.data)
                } else if (shouldRefreshOnFailure) {
                    handleRefreshEvent(location)
                } else {
                    _homeViewState.update {
                        HomeViewState.Empty
                    }
                }
            }
        }
    }

    val time = currentSystemTime()

    fun fetchLocationLiveData() = locationLiveData


    private fun currentSystemTime(): String {
        val currentTime = System.currentTimeMillis()
        val date = Date(currentTime)
        val dateFormat = SimpleDateFormat("EEEE MMM d, hh:mm aaa")
        return dateFormat.format(date)
    }
}