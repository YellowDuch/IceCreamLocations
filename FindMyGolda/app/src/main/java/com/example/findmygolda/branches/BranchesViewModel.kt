package com.example.findmygolda.branches

import android.app.Application
import android.location.Location
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.findmygolda.MainActivity
import com.example.findmygolda.database.BranchDatabaseDAO
import com.example.findmygolda.database.BranchEntity
import com.example.findmygolda.location.ILocationChanged
import com.example.findmygolda.location.LocationAdapter
import com.example.findmygolda.network.BranchManager

class BranchesViewModel( branchManager: BranchManager,
                         locationAdapter: LocationAdapter
): ViewModel(),ILocationChanged {

    private val _filteredBranches = MutableLiveData<List<BranchEntity>>()
    val filteredBranches: LiveData<List<BranchEntity>>
        get() = _filteredBranches
    private var location: Location? = null

    init {
        _filteredBranches.value = branchManager.branches.value
        locationAdapter.subscribeToLocationChangeEvent(this)
        location = locationAdapter.lastLocation
    }

    fun chipPicked(chipTitle: String){
        if (chipTitle == "A-Z") {
            _filteredBranches.value = _filteredBranches.value?.sortAtoZ()
        }

        if (chipTitle == "Distance") {
             _filteredBranches.value = _filteredBranches.value?.sortByLocation()
        }
    }

    private fun List<BranchEntity>?.sortByLocation(): List<BranchEntity>? =
        this?.sortedBy {
            location?.distanceTo(branchLocation(it))
        }

    private fun List<BranchEntity>.sortAtoZ(): List<BranchEntity> =
        this.sortedBy { it?.name }

    private fun branchLocation(branch: BranchEntity): Location {
        val branchLocation = Location("branchLocation")
        branchLocation.longitude = branch.longtitude
        branchLocation.latitude = branch.latitude
        return branchLocation
    }

    override fun locationChanged(location: Location) {
        this.location = location
    }
}