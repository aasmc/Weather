package ru.aasmc.weather.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.algolia.instantsearch.core.connection.ConnectionHandler
import com.algolia.instantsearch.helper.android.item.StatsTextView
import com.algolia.instantsearch.helper.android.searchbox.SearchBoxViewAppCompat
import com.algolia.instantsearch.helper.android.searchbox.connectView
import com.algolia.instantsearch.helper.stats.StatsPresenterImpl
import com.algolia.instantsearch.helper.stats.connectView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.aasmc.weather.R
import ru.aasmc.weather.data.model.SearchResult
import ru.aasmc.weather.data.model.Weather
import ru.aasmc.weather.databinding.FragmentSearchBinding
import ru.aasmc.weather.databinding.FragmentSearchDetailBinding
import ru.aasmc.weather.util.BaseBottomSheetDialog
import ru.aasmc.weather.util.convertKelvinToCelsius

@AndroidEntryPoint
class SearchFragment : Fragment(), SearchResultAdapter.OnItemClickListener {
    private var _binding: FragmentSearchBinding? = null
    private val binding: FragmentSearchBinding
        get() = _binding!!

    private var _searchDetainBinding: FragmentSearchDetailBinding? = null
    private val searchDetailBinding: FragmentSearchDetailBinding
        get() = _searchDetainBinding!!

    private val bottomSheetDialog by lazy {
        BaseBottomSheetDialog(requireActivity(), R.style.AppBottomSheetDialogTheme)
    }

    private val viewModel by viewModels<SearchFragmentViewModel>()
    private val searchResultAdapter by lazy { SearchResultAdapter(this) }
    private val connection = ConnectionHandler()
    private lateinit var searchBoxView: SearchBoxViewAppCompat

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(layoutInflater)
        _searchDetainBinding = FragmentSearchDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchBoxView = SearchBoxViewAppCompat(binding.searchView)
        searchBoxView.searchView.isIconified = false

        val statsView = StatsTextView(binding.stats)
        connection += viewModel.searchBox.connectView(searchBoxView)
        connection += viewModel.stats.connectView(statsView, StatsPresenterImpl())

        searchBoxView.onQuerySubmitted = {
            binding.zeroHits.visibility = View.GONE
            if (it != null && it.isNotEmpty()) {
                viewModel.getSearchWeather(it)
            }
        }

        val recyclerView = binding.locationSearchRecyclerview
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = searchResultAdapter

        searchDetailBinding.fabClose.setOnClickListener {
            if (bottomSheetDialog.isShowing) {
                bottomSheetDialog.dismiss()
            }
        }

        with(viewModel) {
            locations.observe(viewLifecycleOwner) { hits ->
                searchResultAdapter.submitList(hits)
                binding.zeroHits.isVisible = hits.size == 0
            }

            weatherInfo.observe(viewLifecycleOwner) { weather ->
                weather?.let {
                    val formattedWeather = it.apply {
                        this.networkWeatherCondition.temp =
                            convertKelvinToCelsius(this.networkWeatherCondition.temp)
                    }
                    displayWeatherResult(formattedWeather)
                }
            }

            isLoading.observe(viewLifecycleOwner) { state ->
                binding.searchWeatherLoader.isVisible = state
            }

            dataFetchState.observe(viewLifecycleOwner) { state ->
                if (!state) {
                    Snackbar.make(
                        requireView(),
                        "An error occurred! Please try again.",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun displayWeatherResult(result: Weather) {
        with(searchDetailBinding) {
            weatherCondition = result.networkWeatherDescription.first()
            location.text = result.name
            weather = result
        }

        with(bottomSheetDialog) {
            setCancelable(true)
            setContentView(searchDetailBinding.root)
            show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        connection.disconnect()
    }

    override fun onSearchResultClicked(searchResult: SearchResult) {
        searchBoxView.setText(searchResult.name)
        viewModel.getSearchWeather(searchResult.name)
    }
}