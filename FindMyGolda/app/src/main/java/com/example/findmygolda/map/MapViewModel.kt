package com.example.findmygolda.map

import android.app.Application
import android.content.ContentValues
import android.location.Location
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.findmygolda.Constants
import com.example.findmygolda.Constants.Companion.DEFAULT_MAP_ZOOM
import com.example.findmygolda.R
import com.example.findmygolda.branches.BranchManager
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
import java.net.URISyntaxException

class MapViewModel(application: Application) : AndroidViewModel(application),
    OnMapReadyCallback,
    ILocationChanged {

    private var applicationContext = application
    private var branchManager = BranchManager.getInstance(application)
    private var locationAdapter: LocationAdapter = LocationAdapter.getInstance(application)
    private val _isFocusOnUserLocation = MutableLiveData<Boolean>()
    val isFocusedOnUserLocation: MutableLiveData<Boolean>
        get() = _isFocusOnUserLocation
    private val _isNavigateToAlertsFragment = MutableLiveData<Boolean>()
    val isNavigateToAlertsFragment: LiveData<Boolean>
        get() = _isNavigateToAlertsFragment
    private val markers = mutableListOf<Marker>()
    private val _isMapReady = MutableLiveData<Boolean>()
    val isMapReady: LiveData<Boolean>
        get() = _isMapReady
    lateinit var map: MapboxMap
    lateinit var geoJson: String

    fun onAlertsButtonClicked() {
        _isNavigateToAlertsFragment.value = true
    }

    fun doneNavigateToAlertsFragment() {
        _isNavigateToAlertsFragment.value = false
    }

    fun focusOnUserLocationClicked() {
        _isFocusOnUserLocation.value = true
    }

    private fun doneFocusOnUserLocation() {
        _isFocusOnUserLocation.value = false
    }

    fun doneMapReady() {
        _isMapReady.value = false
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        map = mapboxMap
        mapboxMap.setStyle(Style.MAPBOX_STREETS) { style ->
            initializeLocationComponent(style)
            _isMapReady.value = true
            locationAdapter.subscribeToLocationChangeEvent(this)
        }
    }

    private fun setCameraPosition() {
        val currentLocation = locationAdapter.lastLocation
        currentLocation?.let {
            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        it.latitude,
                        it.longitude
                    ), DEFAULT_MAP_ZOOM
                )
            )
        }
    }

    private fun addMarker(
        title: String,
        description: String,
        latLng: LatLng,
        icon: com.mapbox.mapboxsdk.annotations.Icon
    ) {
        val marker = map.addMarker(
            MarkerOptions().setTitle(title).setSnippet(description).position(latLng).icon(icon)
        )
        markers.add(marker)
    }

    fun addMarkersOfBranches(branches: List<Branch>) {
        val icon = IconFactory.getInstance(applicationContext).fromResource(R.drawable.golda_marker)

        branches.forEach { branch ->
            addMarker(branch.name, branch.address, LatLng(branch.latitude, branch.longitude), icon)
        }
    }

    private fun removeAllMarkers() {
        markers.forEach { marker ->
            marker.remove()
        }
    }

    fun addMapLayer(
        sourceId: String,
        geoJson: String?,
        markerImageId: String,
        markerDrawableImage: Int,
        layerId: String
    ) {
        val style = map.style
        val geoJsonSource = GeoJsonSource(sourceId)
        val markerImage = applicationContext.resources.getDrawable(markerDrawableImage)
        val layer = SymbolLayer(layerId, geoJsonSource.id)

        geoJsonSource.setGeoJson(geoJson)
        style?.addSource(geoJsonSource)
        style?.addImage(markerImageId, markerImage)
        layer.setProperties(PropertyFactory.iconImage(markerImageId))
        style?.addLayer(layer)
    }

    fun focusOnUserLocation() {
        setCameraPosition()
        doneFocusOnUserLocation()
    }

    fun anitaLayerCheckChanged(isChecked: Boolean) {
        if (isChecked) {
            addMapLayer(
                Constants.ANITA_SOURCE_ID, geoJson,
                Constants.ANITA_MARKER_IMAGE_ID, R.drawable.anita_marker,
                Constants.ANITA_LAYER_ID
            )
        } else {
            removeMapLayer(Constants.ANITA_LAYER_ID, Constants.ANITA_SOURCE_ID)
        }
    }

    fun goldaLayerCheckChanged(isChecked: Boolean) {
        if (isChecked) {
            branchManager.branches.value?.let { addMarkersOfBranches(it) }
        } else {
            removeAllMarkers()
        }
    }

    fun removeMapLayer(layerId: String, sourceId: String) {
        map.style?.removeLayer(layerId)
        map.style?.removeSource(sourceId)
    }

    @SuppressWarnings("MissingPermission")
    fun initializeLocationComponent(loadedMapStyle: Style) {
        val customLocationComponentOptions = LocationComponentOptions.builder(applicationContext)
            .trackingGesturesManagement(true)
            .build()
        val locationComponentActivationOptions =
            LocationComponentActivationOptions.builder(applicationContext, loadedMapStyle)
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