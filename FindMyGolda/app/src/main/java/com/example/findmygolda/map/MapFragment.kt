package com.example.findmygolda.map

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import com.example.findmygolda.Constants.Companion.ANITA_LAYER_ID
import com.example.findmygolda.Constants.Companion.ANITA_SOURCE_ID
import com.example.findmygolda.Constants.Companion.MAP_BOX_TOKEN
import com.example.findmygolda.R
import com.example.findmygolda.branches.BranchManager
import com.example.findmygolda.database.Branch
import com.example.findmygolda.databinding.FragmentMapBinding
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import java.net.URISyntaxException

class MapFragment : Fragment() {
    private lateinit var mapView: MapView
    private lateinit var mapViewModel: MapViewModel
    lateinit var map: MapboxMap
    private lateinit var application: Context
    private lateinit var geoJson: String
    private lateinit var branchManager: BranchManager
    private lateinit var mapLayerRepository: MapLayerRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        val activity = activity as Context
        Mapbox.getInstance(activity, MAP_BOX_TOKEN)
        application = requireNotNull(this.activity).application
        branchManager = BranchManager.getInstance(requireNotNull(this.activity).application)
        mapLayerRepository = MapLayerRepository.getInstance(requireNotNull(this.activity).application)
        val viewModelFactory = MapViewModelFactory(requireNotNull(this.activity).application)
        mapViewModel =
            ViewModelProviders.of(
                this, viewModelFactory).get(MapViewModel::class.java)
        val binding = DataBindingUtil.inflate<FragmentMapBinding>(inflater,
            R.layout.fragment_map,container,false)
        mapView = binding.mapView
        binding.viewModel = mapViewModel
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(mapViewModel)

        observeToNavigationToAlertFragment()
        observeToMapReady()

        return binding.root
    }

    private fun observeToMapReady() {
        mapViewModel.isMapReady.observe(viewLifecycleOwner, Observer {isMapReady ->
            if (isMapReady) {
                observeToFocusOnUserLocation()
                observeToMapLayerRepository()
                observeToBranches()
                mapViewModel.doneMapReady()
            }
        })
    }

    private fun observeToBranches() {
        branchManager.branches.observe(viewLifecycleOwner, Observer { branches ->
            addMarkers(branches)
        })
    }

    private fun observeToMapLayerRepository() {
        mapLayerRepository.geojson.observe(viewLifecycleOwner, Observer { geoJson ->
            if (geoJson != null) {
                try {
                    mapViewModel.removeMapLayer(ANITA_LAYER_ID, ANITA_SOURCE_ID)
                    mapViewModel.addMapLayer(geoJson)
                    this.geoJson = geoJson
                } catch (exception: URISyntaxException) {
                    Log.d(TAG, "exception")
                }
            }
        })
    }

    private fun observeToFocusOnUserLocation() {
        mapViewModel.focusOnUserLocation.observe(
            viewLifecycleOwner,
            Observer { isFoucosed ->
                if (isFoucosed == true) {
                    mapViewModel.setCameraPosition()
                    mapViewModel.doneFocusOnUserLocation()
                }
            })
    }

    private fun observeToNavigationToAlertFragment() {
        mapViewModel.navigateToAlertsFragment.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                NavHostFragment.findNavController(this)
                    .navigate(R.id.action_mapFragment_to_alertsFragment)
                mapViewModel.doneNavigateToAlertsFragment()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_map_layer_settings,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.isChecked = !item.isChecked
        when (item.itemId) {
            R.id.golda_check -> {
                if(item.isChecked){
                    branchManager.branches.value?.let { addMarkers(it) }
                } else {
                    mapViewModel.removeAllMarkers()
                }
                return true
            }
            R.id.anita_check -> {
                if(item.isChecked){
                    mapViewModel.addMapLayer(geoJson)
                } else {
                    mapViewModel.removeMapLayer(ANITA_LAYER_ID, ANITA_SOURCE_ID)
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun addMarkers(branches: List<Branch>){
        branches.forEach{
            mapViewModel.addGoldaMarker(it)
        }
    }

    override fun onStart() {
        super.onStart()
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
