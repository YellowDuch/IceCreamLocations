package com.example.findmygolda.map

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.findmygolda.alerts.AlertManager
import com.mapbox.mapboxsdk.maps.MapboxMap

class MapViewModelFactory(
    private val application: Application) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            return MapViewModel(application) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
