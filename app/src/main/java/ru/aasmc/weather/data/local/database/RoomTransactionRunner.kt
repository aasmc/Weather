package ru.aasmc.weather.data.local.database

import androidx.room.withTransaction
import javax.inject.Inject

class RoomTransactionRunner @Inject constructor(
    private val db: WeatherDatabase
) : TransactionRunner {
    override suspend fun <T> invoke(block: suspend () -> T): T {
        return db.withTransaction {
            block()
        }
    }
}