package com.example.findmygolda.network

import com.example.findmygolda.Constants
import com.example.findmygolda.Constants.Companion.GOLDA_API_URL
import com.example.findmygolda.database.Branch
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(Constants.BASE_URL)
    .build()

interface BranchesApiService {
    @GET(GOLDA_API_URL)
    fun getProperties():
            Deferred<List<Branch>>
}

object BranchApi {
    val retrofitService : BranchesApiService by lazy {
        retrofit.create(BranchesApiService::class.java) }
}