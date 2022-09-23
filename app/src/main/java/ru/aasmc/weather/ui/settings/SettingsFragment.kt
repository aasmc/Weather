package ru.aasmc.weather.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.aasmc.weather.R
import ru.aasmc.weather.data.preferences.WeatherPreferences
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment :
    PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener
{

    @Inject
    lateinit var weatherPreferences: WeatherPreferences

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(
            this
        )
    }

    private fun init() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    weatherPreferences.observeTheme().collect { selectedOption ->
                        val themePreferenceKey = PREFERENCE_KEY_THEME
                        val themePreference =
                            findPreference<Preference>(themePreferenceKey)
                        themePreference?.summary = selectedOption

                        when (selectedOption) {
                            getString(R.string.light_theme_value) -> setTheme(
                                AppCompatDelegate.MODE_NIGHT_NO
                            )
                            getString(R.string.dark_theme_value) -> setTheme(
                                AppCompatDelegate.MODE_NIGHT_YES
                            )
                            getString(R.string.auto_battery_value) -> setTheme(
                                AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
                            )
                            getString(R.string.follow_system_value) -> setTheme(
                                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                            )
                        }
                    }
                }

                launch {
                    weatherPreferences.observeCacheDuration().collect { setDuration ->
                        val cachePreferenceKey = PREFERENCE_KEY_CACHE
                        val cachePreference =
                            findPreference<Preference>(cachePreferenceKey)
                        cachePreference?.summary = setDuration
                    }
                }

                launch {
                    weatherPreferences.observeTemperatureUnit().collect { selectedUnit ->
                        val unitPreferenceKey = PREFERENCE_KEY_TEMPERATURE_UNIT
                        val unitPreference = findPreference<Preference>(unitPreferenceKey)
                        unitPreference?.summary = selectedUnit
                    }
                }
            }
        }
    }

    override fun onSharedPreferenceChanged(
        sharedPreferences: SharedPreferences,
        key: String?
    ) {
        val themePreferenceKey = PREFERENCE_KEY_THEME
        if (key == themePreferenceKey) {
            val themePreference = findPreference<Preference>(themePreferenceKey)
            val selectedOption = weatherPreferences.theme
            themePreference?.summary = selectedOption

            when (selectedOption) {
                getString(R.string.light_theme_value) -> setTheme(AppCompatDelegate.MODE_NIGHT_NO)
                getString(R.string.dark_theme_value) -> setTheme(AppCompatDelegate.MODE_NIGHT_YES)
                getString(R.string.auto_battery_value) -> setTheme(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
                getString(R.string.follow_system_value) -> setTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }

        val cachePreferenceKey = PREFERENCE_KEY_CACHE
        if (key == cachePreferenceKey) {
            val cachePreference = findPreference<Preference>(cachePreferenceKey)
            val setDuration = weatherPreferences.cacheDuration
            cachePreference?.summary = setDuration
        }

        val unitPreferenceKey = PREFERENCE_KEY_TEMPERATURE_UNIT
        if (key == unitPreferenceKey) {
            val unitPreference = findPreference<Preference>(unitPreferenceKey)
            val selectedUnit = weatherPreferences.temperatureUnit
            unitPreference?.summary = selectedUnit
        }
    }

    private fun setTheme(mode: Int) {
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    companion object {
        private const val PREFERENCE_KEY_THEME = "theme_key"
        private const val PREFERENCE_KEY_CACHE = "cache_key"
        private const val PREFERENCE_KEY_TEMPERATURE_UNIT = "unit_key"
    }

}