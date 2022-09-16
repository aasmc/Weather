package ru.aasmc.weather.ui

import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar


fun Fragment.showShortSnackBar(message: String) {
    Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
}

fun Fragment.showLongSnackBar(message: String) {
    Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
}