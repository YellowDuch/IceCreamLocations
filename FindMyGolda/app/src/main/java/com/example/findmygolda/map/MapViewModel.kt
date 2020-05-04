package com.example.findmygolda.map

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.findmygolda.alerts.AlertManager
import com.example.findmygolda.location.LocationAdapter

class MapViewModel(val application: Application,
                   private val alertManager : AlertManager) : ViewModel() {

    private val _focusOnUserLocation = MutableLiveData<Boolean?>()
    val focusOnUserLocation: LiveData<Boolean?>
        get() = _focusOnUserLocation

    private val _navigateToAlertsFragment = MutableLiveData<Boolean?>()
    val navigateToAlertsFragment: LiveData<Boolean?>
        get() = _navigateToAlertsFragment

    var  locationManager : LocationAdapter = LocationAdapter(application,alertManager)

    fun onAlertsButtonClicked(){
        _navigateToAlertsFragment.value = true
    }

    fun doneNavigateToAlertsFragment(){
        _navigateToAlertsFragment.value = false;
    }

    fun focusOnUserLocationClicked(){
        _focusOnUserLocation.value = true;
    }

    fun doneFocusOnUserLocation(){
        _focusOnUserLocation.value = false;
    }
}