package com.example.findmygolda

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.findmygolda.network.LayerApi
import kotlinx.coroutines.*
import java.io.*
import java.lang.Exception

class MapLayerRepository(val mainActivity: MainActivity) {
    private var mapLayerJob = Job()
    private val coroutineScope = CoroutineScope(
        mapLayerJob + Dispatchers.Main )
    private val sharedPreference: SharedPreferences =  mainActivity.getSharedPreferences("geoJson", Context.MODE_PRIVATE)
    private val _geojson = MutableLiveData<String?>()
    val geojson: LiveData<String?>
        get() = _geojson
    init {
        // TODO: If file exist load from file
        refreshRepository()
    }

    private suspend fun refreshLayer() {
        withContext(Dispatchers.IO) {
            val getLayerDeferred = LayerApi.retrofitService.getProperties()
            val geoJson = getLayerDeferred.await()
            writeGeoJsonFile("AnitaGeoJson", geoJson)
        }
    }

    private fun refreshRepository() {
        coroutineScope.launch {
            try {
                refreshLayer()
                _geojson.value = getFileContent("AnitaGeoJson")
            } catch (e: Exception) {
                // Probably no internet connection
            }
        }
    }

    private fun writeGeoJsonFile(fileName:String, value : String){
        val file: FileOutputStream = mainActivity.openFileOutput(fileName, Context.MODE_PRIVATE)
        file.write(value.toByteArray())
        file.close()
    }

    private fun getFileContent(fileName:String): String{
        val charset = Charsets.UTF_8
        val file: FileInputStream = mainActivity.openFileInput(fileName)
        var inputString = file.readBytes().toString(charset)
        file.close()
        return  inputString
    }
}