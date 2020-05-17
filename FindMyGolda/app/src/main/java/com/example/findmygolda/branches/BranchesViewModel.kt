package com.example.findmygolda.branches

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.findmygolda.Constants.Companion.CHIP_TITTLE_A_TO_Z
import com.example.findmygolda.Constants.Companion.CHIP_TITTLE_DISTANCE
import com.example.findmygolda.Constants.Companion.LOCATION_NAME
import com.example.findmygolda.database.BranchEntity
import com.example.findmygolda.location.ILocationChanged
import com.example.findmygolda.location.LocationAdapter

class BranchesViewModel(branchManager: BranchManager,
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
        if (chipTitle == CHIP_TITTLE_A_TO_Z) {
            _filteredBranches.value = _filteredBranches.value?.sortAtoZ()
        }

        if (chipTitle == CHIP_TITTLE_DISTANCE) {
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
        val branchLocation = Location(LOCATION_NAME)
        branchLocation.longitude = branch.longtitude
        branchLocation.latitude = branch.latitude
        return branchLocation
    }

    override fun locationChanged(location: Location) {
        this.location = location
    }
}