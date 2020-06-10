package com.example.findmygolda.branches

import android.content.Context
import android.location.Location
import com.example.findmygolda.database.DB
import com.example.findmygolda.database.Branch
import kotlinx.coroutines.*
import java.lang.Exception

class BranchManager(val context: Context) {
    private val branchRepository =
        BranchesRepository(
            DB.getInstance(context)
        )
    val branches = branchRepository.branches
    private val coroutineScope = CoroutineScope(
        Dispatchers.Main)

    companion object {
        @Volatile
        private var INSTANCE: BranchManager? = null

        fun getInstance(context: Context): BranchManager {
            synchronized(this) {
                var instance =
                    INSTANCE

                if (instance == null) {
                    instance =
                        BranchManager(context)
                    INSTANCE = instance
                }
                return instance
            }
        }
    }

    init {
        refreshRepository()
    }

    fun isDistanceInRange(location: Location, branch: Branch, range:Int): Boolean{
        val branchLocation = Location("")
        branchLocation.latitude = branch.latitude
        branchLocation.longitude = branch.longitude
        return (location.distanceTo(branchLocation) <= range)
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


