package com.example.findmygolda.location

import android.app.Application
import android.location.Location
import android.os.Looper
import com.example.findmygolda.Constants.Companion.INTERVAL_CHECK_LOCATION
import com.example.findmygolda.Constants.Companion.MAX_RESPONSE_TIME
import com.mapbox.android.core.location.*

class LocationAdapter(val application: Application) {
    private lateinit var locationEngine: LocationEngine
    private val callback = LocationChangeListen()
    private val locationChangedInterested = mutableListOf<ILocationChanged>()
    var lastLocation: Location? = null

    init {
        initLocationEngine()
    }

    private fun initLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(application)
        val request = LocationEngineRequest
            .Builder(INTERVAL_CHECK_LOCATION)
            .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
            .setMaxWaitTime(MAX_RESPONSE_TIME)
            .build()
        locationEngine.requestLocationUpdates(request, callback, Looper.myLooper())
        locationEngine.getLastLocation(callback)
    }

    fun subscribeToLocationChangeEvent(interested:ILocationChanged){
        locationChangedInterested.add(interested)
    }

    private inner class LocationChangeListen :
        LocationEngineCallback<LocationEngineResult> {
        override fun onSuccess(result: LocationEngineResult?) {
            result?.lastLocation ?: return

            if (result.lastLocation != null) {
                val newLocation = Location(result.lastLocation)
                lastLocation = newLocation
                locationChangedInterested.forEach{it.locationChanged(newLocation)}
            }
        }

        override fun onFailure(exception: Exception) {}
    }
}