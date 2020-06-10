package com.example.findmygolda.map

import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.findmygolda.Constants
import com.example.findmygolda.Constants.Companion.MAP_BOX_TOKEN
import com.example.findmygolda.R
import com.example.findmygolda.branches.BranchManager
import com.example.findmygolda.databinding.FragmentMapBinding
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.maps.MapView
import java.net.URISyntaxException

class MapFragment : Fragment() {
    private lateinit var mapView: MapView
    private lateinit var mapViewModel: MapViewModel
    private lateinit var branchManager: BranchManager
    private lateinit var mapLayerRepository: MapLayerRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        Mapbox.getInstance(activity as Context, MAP_BOX_TOKEN)
        branchManager = BranchManager.getInstance(requireNotNull(this.activity).application)
        mapLayerRepository = MapLayerRepository.getInstance(requireNotNull(this.activity).application)
        mapViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(requireNotNull(this.activity).application).create(MapViewModel::class.java)

        val binding = DataBindingUtil.inflate<FragmentMapBinding>(inflater,
            R.layout.fragment_map,container,false)
        mapView = binding.mapView
        binding.viewModel = mapViewModel
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(mapViewModel)
        navigationToAlertFragmentObserver()
        observeMapReady()
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_map_layer_settings,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.isChecked = !item.isChecked
        when (item.itemId) {
            R.id.golda_check -> {
                mapViewModel.goldaLayerCheckChanged(item.isChecked)
                return true
            }
            R.id.anita_check -> {
                mapViewModel.anitaLayerCheckChanged(item.isChecked)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun observeMapReady() {
        mapViewModel.isMapReady.observe(viewLifecycleOwner, Observer {isMapReady ->
            if (isMapReady) {
                focusOnUserLocationObserver()
                observeToMapLayerRepository()
                branchesObserve()
                mapViewModel.doneMapReady()
            }
        })
    }

    private fun branchesObserve() {
        branchManager.branches.observe(viewLifecycleOwner, Observer { branches ->
            mapViewModel.addMarkersOfBranches(branches)
        })
    }

    private fun observeToMapLayerRepository() {
        mapLayerRepository.geojson.observe(viewLifecycleOwner, Observer { geoJson ->
            geoJson?.let {
                addAnitaLayer(it)
                mapViewModel.geoJson = it
            }
        })
    }

    fun addAnitaLayer(geoJson: String) {
        try {
                mapViewModel.removeMapLayer(Constants.ANITA_LAYER_ID, Constants.ANITA_SOURCE_ID)
                mapViewModel.addMapLayer(
                Constants.ANITA_SOURCE_ID,
                geoJson,
                Constants.ANITA_MARKER_IMAGE_ID,
                R.drawable.anita_marker,
                Constants.ANITA_LAYER_ID
            )
        } catch (exception: URISyntaxException) {
            Log.d(ContentValues.TAG, "exception")
        }
    }

    private fun focusOnUserLocationObserver() {
        mapViewModel.isFocusOnUserLocation.observe(
            viewLifecycleOwner,
            Observer { isFocused ->
                if (isFocused) {
                    mapViewModel.focusOnUserLocation()
                }
            })
    }

    private fun navigationToAlertFragmentObserver() {
        mapViewModel.isNavigateToAlertsFragment.observe(viewLifecycleOwner, Observer {isNavigate ->
            if (isNavigate) {
                NavHostFragment.findNavController(this)
                    .navigate(R.id.action_mapFragment_to_alertsFragment)
                mapViewModel.doneNavigateToAlertsFragment()
            }
        })
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
