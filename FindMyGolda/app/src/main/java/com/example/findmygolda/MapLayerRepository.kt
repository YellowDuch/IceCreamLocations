package com.example.findmygolda

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.findmygolda.network.LayerApi
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import kotlinx.coroutines.*
import java.lang.Exception

class MapLayerRepository {
    private val _mapSource = MutableLiveData<GeoJsonSource?>()
    val mapSource: LiveData<GeoJsonSource?>
        get() = _mapSource
    private val _mapLayer = MutableLiveData<SymbolLayer?>()
    val mapLayer: LiveData<SymbolLayer?>
        get() = _mapLayer
    private var mapLayerJob = Job()
    private val coroutineScope = CoroutineScope(
        mapLayerJob + Dispatchers.Main )

    init {
        refreshRepository()
    }

    private suspend fun refreshLayer() {
        withContext(Dispatchers.IO) {
            val getLayerDeferred = LayerApi.retrofitService.getProperties()
            val geoJson = getLayerDeferred.await()
            val geoJsonSource = GeoJsonSource("geojson-source", geoJson)
            _mapSource.value = geoJsonSource
            _mapLayer.value = SymbolLayer("my.layer.id", geoJsonSource.id)
        }
    }

    private fun refreshRepository() {
        coroutineScope.launch {
            try {
                refreshLayer()
            } catch (e: Exception) {
                // Probably no internet connection
            }
        }
    }
}