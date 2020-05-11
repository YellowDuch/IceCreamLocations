package com.example.findmygolda

import android.content.Context
import android.content.SharedPreferences
import com.example.findmygolda.network.LayerApi
import kotlinx.coroutines.*
import java.lang.Exception

class MapLayerRepository(val mainActivity: MainActivity) {
    private var mapLayerJob = Job()
    private val coroutineScope = CoroutineScope(
        mapLayerJob + Dispatchers.Main )
    private val sharedPreference: SharedPreferences =  mainActivity.getSharedPreferences("geoJson", Context.MODE_PRIVATE)
    init {
        refreshRepository()
    }

    private suspend fun refreshLayer() {
        withContext(Dispatchers.IO) {
            val getLayerDeferred = LayerApi.retrofitService.getProperties()
            val geoJson = getLayerDeferred.await()
            var editor = sharedPreference.edit()
            editor.putString("mapJeoJson",geoJson)
            editor.commit()
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