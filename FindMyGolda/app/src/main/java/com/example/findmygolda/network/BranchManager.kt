package com.example.findmygolda.network

import android.app.Application
import android.location.Location
import com.example.findmygolda.BranchesRepository
import com.example.findmygolda.database.AlertDatabase
import com.example.findmygolda.database.BranchEntity
import kotlinx.coroutines.*
import java.lang.Exception

class BranchManager(val application: Application) {
    private val branchRepository = BranchesRepository(AlertDatabase.getInstance(application))
    val branches = branchRepository.branches
    private var branchManagerJob = Job()
    private val coroutineScope = CoroutineScope(
        branchManagerJob + Dispatchers.Main)

    init {
        refreshRepository()
    }

    fun isDistanceInRange(location: Location, branch: BranchEntity, range:Int): Boolean{
        val branchLocation = Location("")
        branchLocation.latitude = branch.latitude
        branchLocation.longitude = branch.longtitude
        return (location!!.distanceTo(branchLocation) <= range)
    }

    private fun refreshRepository() {
        coroutineScope.launch {
            try {
                branchRepository.refreshBranches()
            } catch (e: Exception) {
                // Probably no internet connection
            }
        }
    }

}


