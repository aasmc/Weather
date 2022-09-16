package ru.aasmc.weather.ui.forecast

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.shrikanthravi.collapsiblecalendarview.widget.CollapsibleCalendar
import dagger.hilt.android.AndroidEntryPoint
import ru.aasmc.weather.R
import ru.aasmc.weather.databinding.FragmentForecastBinding
import ru.aasmc.weather.util.SharedPreferencesHelper
import ru.aasmc.weather.util.convertKelvinToCelsius
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ForecastFragment : Fragment(), WeatherForecastAdapter.ForecastOnClickListener {
    private var _binding: FragmentForecastBinding? = null
    private val binding: FragmentForecastBinding
        get() = _binding!!

    private val viewModel by viewModels<ForecastFragmentViewModel>()

    private val weatherForecastAdapter by lazy { WeatherForecastAdapter(this) }

    @Inject
    lateinit var prefs: SharedPreferencesHelper

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
        viewModel.getWeatherForecast(prefs.getCityId())
        observeMoreViewModels()
    }

    private fun observeMoreViewModels() {
        with(viewModel) {
            forecast.observe(viewLifecycleOwner) { weatherForecast ->
                weatherForecast?.let { list ->
                    list.forEach { fcst ->
                        if (prefs.getSelectedTemperatureUnit()
                            == requireActivity().resources.getString(R.string.temp_unit_fahrenheit)
                        ) {
                            fcst.networkWeatherCondition.temp =
                                convertKelvinToCelsius(fcst.networkWeatherCondition.temp)
                        }
                    }
                    weatherForecastAdapter.submitList(list)
                }
            }

            dataFetchState.observe(viewLifecycleOwner) { state ->
                binding.apply {
                    forecastRecyclerview.isVisible = state
                    forecastErrorText?.isVisible = !state
                }
            }

            isLoading.observe(viewLifecycleOwner) { state ->
                binding.forecastProgressBar.isVisible = state
            }

            filteredForecast.observe(viewLifecycleOwner) {
                binding.emptyListText.isVisible = it.isEmpty()
                weatherForecastAdapter.submitList(it)
            }
        }

        binding.forecastSwipeRefresh.setOnRefreshListener {
            initiateRefresh()
        }
    }

    private fun initiateRefresh() {
        binding.forecastErrorText?.visibility = View.GONE
        binding.forecastProgressBar.visibility = View.VISIBLE
        binding.forecastRecyclerview.visibility = View.GONE
        viewModel.refreshForecastData(prefs.getCityId())
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
                    val list = viewModel.forecast.value
                    viewModel.updateWeatherForecast(
                        requireNotNull(selectedDay),
                        requireNotNull(list)
                    )
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