package ru.aasmc.weather.util

import java.text.SimpleDateFormat

fun String.formatDate(): String {
    val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val formatter = SimpleDateFormat("d MMM y, h:mma")
    return formatter.format(parser.parse(this))
}