package ru.aasmc.weather.di.module

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.aasmc.weather.data.local.source.WeatherLocalDataSource
import ru.aasmc.weather.data.local.source.WeatherLocalDataSourceImpl
import ru.aasmc.weather.data.remote.WeatherRemoteDataSource
import ru.aasmc.weather.data.remote.WeatherRemoteDataSourceImpl

@InstallIn(SingletonComponent::class)
@Module
abstract class DataSourcesModule {
    @Binds
    abstract fun bindLocalDataSource(impl: WeatherLocalDataSourceImpl): WeatherLocalDataSource

    @Binds
    abstract fun bindRemoteDataSource(impl: WeatherRemoteDataSourceImpl): WeatherRemoteDataSource
}