package ru.aasmc.weather.di.module

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.aasmc.weather.data.preferences.WeatherPreferences
import ru.aasmc.weather.data.preferences.WeatherPreferencesImpl
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class SettingsModuleBinds {
    @Singleton
    @Binds
    abstract fun providePreferences(bind: WeatherPreferencesImpl): WeatherPreferences
}

@InstallIn(SingletonComponent::class)
@Module
object SettingsModule {
    @Provides
    @Singleton
    fun provideAppPreferences(@ApplicationContext context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }
}