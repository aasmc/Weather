package ru.aasmc.weather.util

import ru.aasmc.weather.data.local.database.TransactionRunner

object TestTransactionRunner : TransactionRunner {
    override suspend fun <T> invoke(block: suspend () -> T): T = block()
}