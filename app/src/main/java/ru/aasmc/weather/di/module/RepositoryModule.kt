package ru.aasmc.weather.di.module

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.aasmc.weather.data.repository.WeatherRepositoryImpl
import ru.aasmc.weather.domain.repositories.Repository

@InstallIn(SingletonComponent::class)
@Module
abstract class RepositoryModule {
    @Binds
    abstract fun bindRepository(impl: WeatherRepositoryImpl): Repository
}