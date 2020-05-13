package com.example.findmygolda.map

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import com.example.findmygolda.R
import com.example.findmygolda.database.BranchEntity
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource

class MapViewModel(val application: Application) : ViewModel() {

    private val _focusOnUserLocation = MutableLiveData<Boolean?>()
    val focusOnUserLocation: LiveData<Boolean?>
        get() = _focusOnUserLocation
    private val _navigateToAlertsFragment = MutableLiveData<Boolean?>()
    val navigateToAlertsFragment: LiveData<Boolean?>
        get() = _navigateToAlertsFragment
    private val markers = mutableListOf<Marker>()

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
        val icon = IconFactory.getInstance(application).fromResource(R.drawable.golda_marker)
        val point = LatLng(branch.latitude, branch.longtitude)
        val marker = map.addMarker(MarkerOptions().setTitle(branch.name).setSnippet(branch.address).position(point).icon(icon))
        markers.add(marker)
    }

    fun removeAllMarkers(){
       markers.forEach{ marker ->
           marker.remove()
       }
    }

    fun addMapLayer(geoJson: String?, style: Style) {
        val geoJsonSource = GeoJsonSource(ANITA_SOURCE_ID)
        geoJsonSource.setGeoJson(geoJson)
        style.addSource(geoJsonSource)
        val anitaMarkerImage = application.resources.getDrawable(R.drawable.anita_marker)
        style.addImage("myImage", anitaMarkerImage)
        val myLayer = SymbolLayer(ANITA_LAYER_ID, geoJsonSource.id)
        myLayer.setProperties(PropertyFactory.iconImage("myImage"))
        style.addLayer(myLayer)
    }

    fun removeMapLayer(style: Style, layerId: String, sourceId: String){
        style.removeLayer(layerId)
        style.removeSource(sourceId)
    }
}