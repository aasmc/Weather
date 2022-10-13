package ru.aasmc.weather.ui.forecast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shrikanthravi.collapsiblecalendarview.data.Day
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.aasmc.weather.domain.model.Forecast
import ru.aasmc.weather.domain.usecases.ObserveForecast
import ru.aasmc.weather.domain.usecases.StoreForecasts
import ru.aasmc.weather.util.Result
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ForecastFragmentViewModel @Inject constructor(
    private val observeForecast: ObserveForecast,
    private val storeForecasts: StoreForecasts
) : ViewModel() {


    private val _forecastViewState: MutableStateFlow<ForecastViewState> =
        MutableStateFlow(ForecastViewState.Empty)
    val forecastViewState: StateFlow<ForecastViewState> = _forecastViewState.asStateFlow()

    fun handleEvent(forecastEvent: ForecastEvent) {
        when (forecastEvent) {
            is ForecastEvent.ObserveForecast -> {
                observeForecast(forecastEvent.cityId)
            }
            is ForecastEvent.UpdateWeatherForecast -> {
                updateForecast(forecastEvent.selectedDay, forecastEvent.cityId)
            }
            is ForecastEvent.RefreshForecast -> {
                refreshForecast(forecastEvent.cityId)
            }
        }
    }

    private fun observeForecast(cityId: Int) {
        viewModelScope.launch {
            observeForecast(cityId, false)
                .collect { result ->
                    updateResult(result, true, cityId) { forecasts ->
                        _forecastViewState.update {
                            ForecastViewState.Success(forecasts)
                        }
                    }
                }
        }
    }

    private fun refreshForecast(cityId: Int) {
        viewModelScope.launch {
            observeForecast(cityId, true)
                .collect { result ->
                    updateResult(result, false, cityId) { forecasts ->
                        _forecastViewState.update {
                            ForecastViewState.Success(forecasts)
                        }
                    }
                }
        }
    }

    private fun updateForecast(selectedDay: Day, cityId: Int) {
        viewModelScope.launch {
            observeForecast(cityId, true)
                .collect { result ->
                    updateResult(result, false, cityId) { forecasts ->
                        selectedDay.let {
                            val checkerDay = it.day
                            val checkerMonth = it.month
                            val checkerYear = it.year

                            val filteredList =
                                forecasts.filter { weatherForecast ->
                                    val format =
                                        SimpleDateFormat(
                                            "d MMM y, h:mma",
                                            Locale.getDefault()
                                        )
                                    val backupFormat = SimpleDateFormat(
                                        "yyyy-MM-dd HH:mm:ss", Locale.getDefault()
                                    )
                                    val formattedDate = try {
                                        format.parse(weatherForecast.date)
                                    } catch (e: Exception) {
                                        backupFormat.parse(weatherForecast.date)
                                    }
                                    val weatherForecastDay = formattedDate?.date
                                    val weatherForecastMonth =
                                        formattedDate?.month
                                    val weatherForecastYear = formattedDate?.year
                                    // This checks if the selected day, month and year are equal. The year requires an addition of 1900 to get the correct year.
                                    weatherForecastDay == checkerDay && weatherForecastMonth == checkerMonth && weatherForecastYear?.plus(
                                        1900
                                    ) == checkerYear
                                }
                            storeForecasts(filteredList)
                            _forecastViewState.update {
                                ForecastViewState.FilteredForecast(filteredList)
                            }
                        }
                    }
                }
        }
    }


    private suspend fun updateResult(
        result: Result<List<Forecast>?>,
        shouldRefreshOnFailure: Boolean,
        cityId: Int,
        onResult: suspend (List<Forecast>) -> Unit
    ) {
        when (result) {
            is Result.Error -> {
                _forecastViewState.update {
                    ForecastViewState.Failure
                }
            }
            Result.Loading -> {
                _forecastViewState.update {
                    ForecastViewState.Loading
                }
            }
            is Result.Success -> {
                if (result.data != null && result.data.isNotEmpty()) {
                    onResult(result.data)
                } else if (shouldRefreshOnFailure) {
                    refreshForecast(cityId)
                } else {
                    _forecastViewState.update {
                        ForecastViewState.Empty
                    }
                }
            }
        }
    }
}