package com.example.findmygolda.network

import com.example.findmygolda.Constants
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(Constants.BASE_URL)
    .build()

interface MapLayerApiService {
    @GET("anita.json")
    fun getProperties():
            Deferred<String>
}

object LayerApi {
    val retrofitService : MapLayerApiService by lazy {
        retrofit.create(MapLayerApiService::class.java) }
}