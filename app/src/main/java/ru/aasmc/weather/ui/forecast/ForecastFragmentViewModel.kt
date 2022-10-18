package ru.aasmc.weather.ui.forecast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shrikanthravi.collapsiblecalendarview.data.Day
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.aasmc.weather.domain.model.Forecast
import ru.aasmc.weather.domain.usecases.GetAllForecasts
import ru.aasmc.weather.domain.usecases.GetForecasts
import ru.aasmc.weather.domain.usecases.ObserveForecast
import ru.aasmc.weather.util.Result
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ForecastFragmentViewModel @Inject constructor(
    private val getForecasts: GetForecasts
) : ViewModel() {


    private val _forecastViewState: MutableStateFlow<ForecastViewState> =
        MutableStateFlow(ForecastViewState.Empty)
    val forecastViewState: StateFlow<ForecastViewState> = _forecastViewState.asStateFlow()

    private var getForecastsJob: Job? = null
    private var refreshJob: Job? = null
    private var updateJob: Job? = null

    fun handleEvent(forecastEvent: ForecastEvent) {
        when (forecastEvent) {
            is ForecastEvent.ObserveForecast -> {
                getForecasts(forecastEvent.cityId)
            }
            is ForecastEvent.UpdateWeatherForecast -> {
                updateForecast(forecastEvent.selectedDay, forecastEvent.cityId)
            }
            is ForecastEvent.RefreshForecast -> {
                refreshForecasts(forecastEvent.cityId)
            }
        }
    }

    private fun getForecasts(cityId: Int) {
        getForecastsJob?.cancel()
        getForecastsJob = viewModelScope.launch {
            val result = getForecasts(cityId, false)
            updateViewState(
                result,
                true,
                cityId
            ) { res ->
                _forecastViewState.update {
                    ForecastViewState.Success(res)
                }
            }
        }
    }

    private fun updateViewState(
        result: Result<List<Forecast>?>,
        shouldRefreshOnFailure: Boolean,
        cityId: Int,
        onResult: (List<Forecast>) -> Unit
    ) {
        _forecastViewState.update {
            ForecastViewState.Loading
        }
        when (result) {
            is Result.Error -> {
                _forecastViewState.update {
                    ForecastViewState.Failure(result.exception)
                }
            }
            Result.Loading -> {}
            is Result.Success -> {
                if (result.data != null && result.data.isNotEmpty()) {
                    onResult(result.data)
                } else if (shouldRefreshOnFailure) {
                    refreshForecasts(cityId)
                } else {
                    _forecastViewState.update {
                        ForecastViewState.Empty
                    }
                }
            }
        }
    }

    private fun refreshForecasts(cityId: Int) {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            val result = getForecasts(cityId, true)
            updateViewState(
                result,
                false,
                cityId
            ) { res ->
                _forecastViewState.update {
                    ForecastViewState.Success(res)
                }
            }
        }
    }

    private fun updateForecast(selectedDay: Day, cityId: Int) {
        updateJob?.cancel()
        updateJob = viewModelScope.launch(Dispatchers.IO) {
            val result = getForecasts(cityId, false)
            updateViewState(
                result,
                false,
                cityId
            ) { forecasts ->
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
                    _forecastViewState.update {
                        ForecastViewState.FilteredForecast(filteredList)
                    }
                }
            }
        }
    }
}