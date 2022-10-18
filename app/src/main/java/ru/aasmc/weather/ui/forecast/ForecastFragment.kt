package ru.aasmc.weather.ui.forecast

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.shrikanthravi.collapsiblecalendarview.widget.CollapsibleCalendar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.aasmc.weather.R
import ru.aasmc.weather.data.exceptions.DBException
import ru.aasmc.weather.data.exceptions.NetworkException
import ru.aasmc.weather.data.preferences.WeatherPreferences
import ru.aasmc.weather.databinding.FragmentForecastBinding
import ru.aasmc.weather.domain.model.Forecast
import ru.aasmc.weather.domain.model.WeatherCondition
import ru.aasmc.weather.util.convertCelsiusToFahrenheit
import ru.aasmc.weather.util.convertKelvinToCelsius
import timber.log.Timber
import javax.inject.Inject

// TODO Add animations when changing forecasts on selected day change

@AndroidEntryPoint
class ForecastFragment : Fragment(), WeatherForecastAdapter.ForecastOnClickListener {
    private var _binding: FragmentForecastBinding? = null
    private val binding: FragmentForecastBinding
        get() = _binding!!

    private val viewModel by viewModels<ForecastFragmentViewModel>()

    private val weatherForecastAdapter by lazy {
        WeatherForecastAdapter(
            this,
            weatherPrefs
        )
    }

    @Inject
    lateinit var weatherPrefs: WeatherPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForecastBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCalendar()
        binding.forecastRecyclerview.adapter = weatherForecastAdapter
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                weatherPrefs.observeCityId().collect {
                    viewModel.handleEvent(ForecastEvent.ObserveForecast(it))
                }
            }
        }
        observeMoreViewModels()
    }

    private fun observeMoreViewModels() {

        with(viewModel) {
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    forecastViewState.collect { viewState ->
                        when (viewState) {
                            ForecastViewState.Empty -> {
                                renderEmptyState()
                            }
                            is ForecastViewState.Failure -> {
                                renderFailure(viewState.exception)
                            }
                            is ForecastViewState.FilteredForecast -> {
                                renderFilteredForecast(viewState.filteredForecasts)
                            }
                            ForecastViewState.Loading -> {
                                renderLoading()
                            }
                            is ForecastViewState.Success -> {
                                renderSuccess(viewState.forecasts)
                            }
                        }
                    }
                }
            }
        }
        binding.forecastSwipeRefresh.setOnRefreshListener {
            initiateRefresh()
        }
    }

    private fun renderSuccess(forecasts: List<Forecast>) {
        binding.apply {
            forecastRecyclerview.isVisible = true
            forecastErrorText.visibility= View.GONE
            forecastProgressBar.isVisible = false
        }
        weatherForecastAdapter.submitList(forecasts)
        binding.emptyListText.isVisible = forecasts.isEmpty()

    }

    private fun renderLoading() {
        binding.apply {
            emptyListText.isVisible = false
            forecastProgressBar.isVisible = true
            forecastRecyclerview.isVisible = false
            forecastErrorText.visibility = View.GONE
        }
    }

    private fun renderFilteredForecast(filteredForecasts: List<Forecast>) {
        renderSuccess(filteredForecasts)
    }

    private fun renderFailure(throwable: Throwable) {
        Timber.d(throwable.message)
        val message = when (throwable) {
            is NetworkException -> requireActivity().getString(R.string.network_error)
            is DBException -> requireActivity().getString(R.string.database_error)
            else -> requireActivity().getString(R.string.unknown_error)
        }
        binding.apply {
            emptyListText.isVisible = false
            forecastProgressBar.isVisible = false
            forecastRecyclerview.isVisible = false
            forecastErrorText.visibility = View.VISIBLE
            forecastErrorText.text = message
        }
    }

    private fun renderEmptyState() {
        binding.apply {
            emptyListText.isVisible = true
            forecastProgressBar.isVisible = false
            forecastRecyclerview.isVisible = false
            forecastErrorText.visibility = View.GONE
        }
    }

    private fun initiateRefresh() {
        viewModel.handleEvent(ForecastEvent.RefreshForecast(weatherPrefs.cityId))
        binding.forecastSwipeRefresh.isRefreshing = false
    }

    private fun setupCalendar() {
        binding.calendarView.setCalendarListener(object :
                                                     CollapsibleCalendar.CalendarListener {
            override fun onClickListener() {
            }

            override fun onDataUpdate() {
            }

            override fun onDayChanged() {
            }

            override fun onDaySelect() {
                binding.emptyListText.visibility = View.GONE
                runCatching {
                    val selectedDay = binding.calendarView.selectedDay
                    selectedDay?.let {
                        viewModel.handleEvent(
                            ForecastEvent.UpdateWeatherForecast(
                                it,
                                weatherPrefs.cityId
                            )
                        )
                    }
                }.onFailure {
                    Timber.d(it)
                }
            }

            override fun onItemClick(v: View) {
            }

            override fun onMonthChange() {
            }

            override fun onWeekChange(position: Int) {
            }
        })
    }

}