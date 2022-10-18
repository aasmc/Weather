package ru.aasmc.weather.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.aasmc.weather.domain.model.LocationModel
import ru.aasmc.weather.domain.model.Weather
import ru.aasmc.weather.domain.usecases.GetWeather
import ru.aasmc.weather.util.LocationLiveData
import ru.aasmc.weather.util.Result
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeFragmentViewModel @Inject constructor(
    private val getWeather: GetWeather
) : ViewModel() {
    @Inject
    lateinit var locationLiveData: LocationLiveData

    private val _homeViewState: MutableStateFlow<HomeViewState> =
        MutableStateFlow(HomeViewState.Empty)
    val homeViewState: StateFlow<HomeViewState> = _homeViewState.asStateFlow()

    init {
        currentSystemTime()
    }

    private var refreshJob: Job? = null
    private var getWeatherJob: Job? = null

    fun handleEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.ObserveWeatherEvent -> {
                handleGetWeatherEvent(event.location)
            }
            is HomeEvent.RefreshWeather -> {
                handleRefreshWeatherEvent(event.location)
            }
        }
    }

    private fun handleGetWeatherEvent(location: LocationModel) {
        getWeatherJob?.cancel()
        getWeatherJob = viewModelScope.launch {
            val weatherRes = getWeather(location, false)
            updateWeatherViewState(location, weatherRes, true) { w->
                _homeViewState.update {
                    HomeViewState.WeatherDetails(w)
                }
            }
        }
    }

    private fun handleRefreshWeatherEvent(location: LocationModel) {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            val weatherRes = getWeather(location, true)
            updateWeatherViewState(location, weatherRes, false) { w->
                _homeViewState.update {
                    HomeViewState.WeatherDetails(w)
                }
            }
        }
    }

    private fun updateWeatherViewState(
        location: LocationModel,
        result: Result<Weather?>,
        shouldRefreshOnFailure: Boolean,
        onResult: (Weather) -> Unit
    ) {
        _homeViewState.update {
            HomeViewState.Loading
        }

        when (result) {
            is Result.Error -> {
                _homeViewState.update {
                    HomeViewState.Failure(result.exception)
                }
            }
            Result.Loading -> {}
            is Result.Success -> {
                if (result.data != null) {
                    onResult(result.data)
                } else if (shouldRefreshOnFailure) {
                    handleRefreshWeatherEvent(location)
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