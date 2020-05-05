package com.example.findmygolda.map

import android.app.Application
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.findmygolda.alerts.AlertManager
import com.example.findmygolda.database.BranchEntity
import com.example.findmygolda.location.LocationAdapter
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style

class MapViewModel(val application: Application) : ViewModel() {

    private val _focusOnUserLocation = MutableLiveData<Boolean?>()
    val focusOnUserLocation: LiveData<Boolean?>
        get() = _focusOnUserLocation
    private val _navigateToAlertsFragment = MutableLiveData<Boolean?>()
    val navigateToAlertsFragment: LiveData<Boolean?>
        get() = _navigateToAlertsFragment

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

    fun setCameraPosition(location: Location, map:MapboxMap) {
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(location.latitude,
                    location.longitude), DEFAULT_MAP_ZOOM))
    }

    fun addGoldaMarker(branch: BranchEntity, map: MapboxMap){
        val point = LatLng(branch.latitude, branch.longtitude)
        map.addMarker(MarkerOptions().setTitle(branch.name).setSnippet(branch.address).position(point))
    }

}