package ru.aasmc.weather.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.aasmc.weather.data.model.LocationModel
import ru.aasmc.weather.data.model.Weather
import ru.aasmc.weather.data.source.repository.WeatherRepository
import ru.aasmc.weather.util.LocationLiveData
import ru.aasmc.weather.util.Result
import ru.aasmc.weather.util.asLiveData
import ru.aasmc.weather.util.convertKelvinToCelsius
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeFragmentViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {
    @Inject
    lateinit var locationLiveData: LocationLiveData

    init {
        currentSystemTime()
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading = _isLoading.asLiveData()

    private val _dataFetchState = MutableLiveData<Boolean>()
    val dataFetchState = _dataFetchState.asLiveData()

    private val _weather = MutableLiveData<Weather?>()
    val weather = _weather.asLiveData()

    val time = currentSystemTime()

    fun fetchLocationLiveData() = locationLiveData

    /**
     *This attempts to get the [Weather] from the local data source,
     * if the result is null, it gets from the remote source.
     * @see refreshWeather
     */
    fun getWeather(location: LocationModel) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            when (val result = repository.getWeather(location, false)) {
                is Result.Error -> {
                    _isLoading.value = false
                    _dataFetchState.value = false
                }
                Result.Loading -> {
                    _isLoading.postValue(true)
                }
                is Result.Success -> {
                    _isLoading.value = false
                    if (result.data != null) {
                        val weather = result.data
                        _dataFetchState.value = true
                        _weather.value = weather
                    } else {
                        refreshWeather(location)
                    }
                }
            }
        }
    }

    /**
     * This is called when the user swipes down to refresh.
     * This enables the [Weather] for the current [location] to be received.
     */
    fun refreshWeather(location: LocationModel) {
        _isLoading.value = true
        viewModelScope.launch {
            when (val result = repository.getWeather(location, true)) {
                is Result.Error -> {
                    _isLoading.value = false
                    _dataFetchState.value = false
                }
                Result.Loading -> _isLoading.postValue(false)
                is Result.Success -> {
                    _isLoading.value = false
                    if (result.data != null) {
                        val weather = result.data.apply {
                            this.networkWeatherCondition.temp =
                                convertKelvinToCelsius(this.networkWeatherCondition.temp)
                        }
                        _dataFetchState.value = true
                        _weather.value = weather

                        repository.deleteWeatherData()
                        repository.storeWeatherData(weather)
                    } else {
                        _weather.postValue(null)
                        _dataFetchState.postValue(false)
                    }
                }
            }
        }
    }

    fun currentSystemTime(): String {
        val currentTime = System.currentTimeMillis()
        val date = Date(currentTime)
        val dateFormat = SimpleDateFormat("EEEE MMM d, hh:mm aaa")
        return dateFormat.format(date)
    }
}