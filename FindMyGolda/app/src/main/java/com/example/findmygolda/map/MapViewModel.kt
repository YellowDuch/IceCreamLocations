package com.example.findmygolda.map

import android.app.Application
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.findmygolda.Constants.Companion.ANITA_LAYER_ID
import com.example.findmygolda.Constants.Companion.ANITA_MARKER_IMAGE_ID
import com.example.findmygolda.Constants.Companion.ANITA_SOURCE_ID
import com.example.findmygolda.Constants.Companion.DEFAULT_MAP_ZOOM
import com.example.findmygolda.R
import com.example.findmygolda.database.Branch
import com.example.findmygolda.location.ILocationChanged
import com.example.findmygolda.location.LocationAdapter
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource

class MapViewModel(val application: Application) : ViewModel(),
    OnMapReadyCallback,
    ILocationChanged {

    private var locationAdapter: LocationAdapter = LocationAdapter.getInstance(application)
    private val _focusOnUserLocation = MutableLiveData<Boolean?>()
    val focusOnUserLocation: LiveData<Boolean?>
        get() = _focusOnUserLocation
    private val _navigateToAlertsFragment = MutableLiveData<Boolean?>()
    val navigateToAlertsFragment: LiveData<Boolean?>
        get() = _navigateToAlertsFragment
    private val markers = mutableListOf<Marker>()
    private val _isMapReady = MutableLiveData<Boolean>()
    val isMapReady: LiveData<Boolean>
        get() = _isMapReady
    lateinit var map: MapboxMap


    fun onAlertsButtonClicked(){
        _navigateToAlertsFragment.value = true
    }

    fun doneNavigateToAlertsFragment(){
        _navigateToAlertsFragment.value = false
    }

    fun focusOnUserLocationClicked(){
        _focusOnUserLocation.value = true
    }

    fun doneFocusOnUserLocation(){
        _focusOnUserLocation.value = false
    }

    fun doneMapReady(){
        _isMapReady.value = false
    }

    fun setCameraPosition() {
        val currentLocation = locationAdapter.lastLocation
        if (currentLocation != null) {
            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(currentLocation.latitude,
                        currentLocation.longitude), DEFAULT_MAP_ZOOM))
        }
    }

    fun addGoldaMarker(branch: Branch){
        val icon = IconFactory.getInstance(application).fromResource(R.drawable.golda_marker)
        val point = LatLng(branch.latitude, branch.longitude)
        val marker = map.addMarker(MarkerOptions().setTitle(branch.name).setSnippet(branch.address).position(point).icon(icon))
        markers.add(marker)
    }

    fun removeAllMarkers(){
       markers.forEach{ marker ->
           marker.remove()
       }
    }

    fun addMapLayer(geoJson: String?) {
        val style = map.style
        val geoJsonSource = GeoJsonSource(ANITA_SOURCE_ID)
        geoJsonSource.setGeoJson(geoJson)
        style?.addSource(geoJsonSource)
        val anitaMarkerImage = application.resources.getDrawable(R.drawable.anita_marker)
        style?.addImage(ANITA_MARKER_IMAGE_ID, anitaMarkerImage)
        val myLayer = SymbolLayer(ANITA_LAYER_ID, geoJsonSource.id)
        myLayer.setProperties(PropertyFactory.iconImage(ANITA_MARKER_IMAGE_ID))
        style?.addLayer(myLayer)
    }

    fun removeMapLayer(layerId: String, sourceId: String){
        map.style?.removeLayer(layerId)
        map.style?.removeSource(sourceId)
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        map = mapboxMap
        mapboxMap.setStyle(Style.MAPBOX_STREETS) {style->
            initializeLocationComponent(style)
            _isMapReady.value = true
        }
        locationAdapter.subscribeToLocationChangeEvent(this)
    }

    @SuppressWarnings("MissingPermission")
    fun initializeLocationComponent(loadedMapStyle: Style) {
        val customLocationComponentOptions = LocationComponentOptions.builder(application)
            .trackingGesturesManagement(true)
            .build()
        val locationComponentActivationOptions =
            LocationComponentActivationOptions.builder(application, loadedMapStyle)
                .locationComponentOptions(customLocationComponentOptions)
                .build()
        map.locationComponent.apply {
            activateLocationComponent(locationComponentActivationOptions)
            isLocationComponentEnabled = true
            cameraMode = CameraMode.TRACKING
            renderMode = RenderMode.COMPASS
        }
    }

    override fun locationChanged(location: Location?) {
        map.locationComponent.forceLocationUpdate(location)
    }
}