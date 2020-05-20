package com.example.findmygolda.map

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.findmygolda.Constants
import com.example.findmygolda.network.LayerApi
import kotlinx.coroutines.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class MapLayerRepository(val context: Context) {
    private var mapLayerJob = Job()
    private val coroutineScope = CoroutineScope(
        mapLayerJob + Dispatchers.Main )
    private val _geojson = MutableLiveData<String?>()
    val geojson: LiveData<String?>
        get() = _geojson

    init {
        if(fileExist(Constants.ANITA_GEO_FILE_NAME)){
            _geojson.value = getFileContent(Constants.ANITA_GEO_FILE_NAME)
        } else {
            refreshRepository()
        }
    }

    private suspend fun refreshLayer() {
        withContext(Dispatchers.IO) {
            val getLayerDeferred = LayerApi.retrofitService.getProperties()
            val geoJson = getLayerDeferred.await()
            writeGeoJsonFile(Constants.ANITA_GEO_FILE_NAME, geoJson)
        }
    }

    private fun refreshRepository() {
        coroutineScope.launch {
            try {
                refreshLayer()
                _geojson.value = getFileContent(Constants.ANITA_GEO_FILE_NAME)
            } catch (e: Exception) {
                // Probably no internet connection
            }
        }
    }

    private fun writeGeoJsonFile(fileName:String, value : String){
        val file: FileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
        file.write(value.toByteArray())
        file.close()
    }

    private fun getFileContent(fileName:String): String{
        val charset = Charsets.UTF_8
        val file: FileInputStream = context.openFileInput(fileName)
        var inputString = file.readBytes().toString(charset)
        file.close()
        return  inputString
    }

    private fun fileExist(fname: String?): Boolean {
        val file: File = context.getFileStreamPath(fname)
        return file.exists()
    }

    companion object {
        @Volatile
        private var INSTANCE: MapLayerRepository? = null

        fun getInstance(context: Context): MapLayerRepository {
            synchronized(this) {
                var instance =
                    INSTANCE

                if (instance == null) {
                    instance =
                        MapLayerRepository(context)
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}