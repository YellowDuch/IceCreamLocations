package com.example.findmygolda.map

import android.content.ContentValues.TAG
import android.content.Context
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import com.example.findmygolda.MainActivity
import com.example.findmygolda.R
import com.example.findmygolda.database.BranchEntity
import com.example.findmygolda.databinding.FragmentMapBinding
import com.example.findmygolda.location.ILocationChanged
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import java.net.URISyntaxException

const val DEFAULT_MAP_ZOOM = 15.0
const val ANITA_LAYER_ID = "anitaLayer"
const val ANITA_SOURCE_ID = "anitaSource"

class MapFragment : Fragment(), OnMapReadyCallback, ILocationChanged {
    lateinit var mapView: MapView
    lateinit var mapViewModel: MapViewModel
    lateinit var map: MapboxMap
    var locationComponent: LocationComponent? = null
    private lateinit var application: Context
    private lateinit var currentLocation: Location
    private lateinit var mainActivity: MainActivity
    private lateinit var geoJson: String
    private lateinit var mapStyle: Style

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_map_layer_settings,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.golda_check -> {
                item.isChecked = !item.isChecked
                if(!item.isChecked){
                    mapViewModel.removeAllMarkers()
                } else {
                    mainActivity.branchManager.branches.value?.let { addMarkers(it) }
                }
                return true
            }
            R.id.anita_check -> {
                item.isChecked = !item.isChecked
                if(!item.isChecked){
                    mapViewModel.removeMapLayer(mapStyle, ANITA_LAYER_ID, ANITA_SOURCE_ID)
                } else {
                    mapViewModel.addMapLayer(geoJson, mapStyle)
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        val activity = activity as Context
        Mapbox.getInstance(activity, getString(R.string.mapbox_access_token))
        application = requireNotNull(this.activity).application
        mainActivity = activity as MainActivity
        val viewModelFactory = MapViewModelFactory(requireNotNull(this.activity).application)
        mapViewModel =
            ViewModelProviders.of(
                this, viewModelFactory).get(MapViewModel::class.java)
        val binding = DataBindingUtil.inflate<FragmentMapBinding>(inflater,
            R.layout.fragment_map,container,false)
        mapView = binding.mapView
        binding.viewModel = mapViewModel
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        mapViewModel.navigateToAlertsFragment.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                NavHostFragment.findNavController(this)
                    .navigate(R.id.action_mapFragment_to_alertsFragment)
                mapViewModel.doneNavigateToAlertsFragment()
            }
        })

        return binding.root
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        map = mapboxMap
        mapboxMap.setStyle(Style.MAPBOX_STREETS) {style->
            mapStyle = style
            initializeLocationComponent(style)
            mainActivity.locationAdapter.subscribeToLocationChangeEvent(this)
            mapViewModel.focusOnUserLocation.observe(viewLifecycleOwner, Observer {
                if (it == true) {
                    mapViewModel.setCameraPosition(currentLocation,map)
                    mapViewModel.doneFocusOnUserLocation()
                }
            })
            mainActivity.mapLayerRepository.geojson.observe(viewLifecycleOwner, Observer { geoJson ->
                if (geoJson != null) {
                    try {
                        mapViewModel.addMapLayer(geoJson, style)
                        this.geoJson = geoJson
                    } catch (exception: URISyntaxException) {
                        Log.d(TAG, "exception")
                    }
                }
            })
            mainActivity.branchManager.branches.observe(viewLifecycleOwner, Observer { branches ->
                addMarkers(branches)
            })
        }
    }

    override fun locationChanged(location: Location) {
        map.locationComponent.forceLocationUpdate(location)
        if (location != null) {
            currentLocation = location
        }
    }

    private fun addMarkers(branches: List<BranchEntity>){
        branches.forEach{
            mapViewModel.addGoldaMarker(it,map)
        }
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

    override fun onStart() {
        super.onStart()
        if (PermissionsManager.areLocationPermissionsGranted(activity)) {
            locationComponent?.onStart()
        }
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        locationComponent?.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }
}
