package ru.aasmc.weather.di.module

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.aasmc.weather.data.source.local.WeatherLocalDataSource
import ru.aasmc.weather.data.source.local.WeatherLocalDataSourceImpl
import ru.aasmc.weather.data.source.remote.WeatherRemoteDataSource
import ru.aasmc.weather.data.source.remote.WeatherRemoteDataSourceImpl

@InstallIn(SingletonComponent::class)
@Module
abstract class DataSourcesModule {
    @Binds
    abstract fun bindLocalDataSource(impl: WeatherLocalDataSourceImpl): WeatherLocalDataSource

    @Binds
    abstract fun bindRemoteDataSource(impl: WeatherRemoteDataSourceImpl): WeatherRemoteDataSource
}