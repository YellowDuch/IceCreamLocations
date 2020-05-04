package com.example.findmygolda.map

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.findmygolda.alerts.AlertManager

class MapViewModelFactory(
    private val application: Application,
    private var alertManager: AlertManager
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            return MapViewModel(application, alertManager) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
