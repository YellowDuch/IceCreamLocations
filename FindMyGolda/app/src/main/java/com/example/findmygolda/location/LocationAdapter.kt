package com.example.findmygolda.location

import android.content.Context
import android.location.Location
import android.os.Looper
import com.example.findmygolda.Constants.Companion.INTERVAL_CHECK_LOCATION
import com.example.findmygolda.Constants.Companion.MAX_RESPONSE_TIME
import com.mapbox.android.core.location.*

class LocationAdapter(val context: Context) {
    private lateinit var locationEngine: LocationEngine
    private val locationChangeListener = LocationChangeListener()
    private val locationChangedInterested = mutableListOf<ILocationChanged>()
    var lastLocation: Location? = null

    companion object {
        @Volatile
        private var INSTANCE: LocationAdapter? = null

        fun getInstance(context: Context): LocationAdapter {
            synchronized(this) {
                var instance =
                    INSTANCE

                if (instance == null) {
                    instance =
                        LocationAdapter(context)
                    INSTANCE = instance
                }
                return instance
            }
        }
    }

    init {
        initLocationEngine()
    }

    private fun initLocationEngine() {
        val request = LocationEngineRequest
            .Builder(INTERVAL_CHECK_LOCATION)
            .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
            .setMaxWaitTime(MAX_RESPONSE_TIME)
            .build()

        locationEngine = LocationEngineProvider.getBestLocationEngine(context)
        locationEngine.requestLocationUpdates(request, locationChangeListener, Looper.myLooper())
        locationEngine.getLastLocation(locationChangeListener)
    }

    fun subscribeToLocationChangeEvent(interested: ILocationChanged) {
        locationChangedInterested.add(interested)
    }

    private inner class LocationChangeListener :
        LocationEngineCallback<LocationEngineResult> {
        override fun onSuccess(result: LocationEngineResult?) {
            result?.lastLocation ?: return
            lastLocation = Location(result.lastLocation)
            locationChangedInterested.forEach { it.locationChanged(lastLocation) }
        }

        override fun onFailure(exception: Exception) {}
    }
}