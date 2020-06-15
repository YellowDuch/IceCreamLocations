package com.example.findmygolda.branches

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.findmygolda.Constants.Companion.CHIP_TITTLE_A_TO_Z
import com.example.findmygolda.Constants.Companion.CHIP_TITTLE_DISTANCE
import com.example.findmygolda.database.Branch
import com.example.findmygolda.getBranchLocation
import com.example.findmygolda.location.LocationAdapter
import com.google.android.material.chip.Chip

class BranchesViewModel(application: Application
): AndroidViewModel(application) {
    private val branchManager = BranchManager.getInstance(application)
    private val locationAdapter = LocationAdapter.getInstance(application)
    private val _filteredBranches = MutableLiveData<List<Branch>>()
    val filteredBranches: LiveData<List<Branch>>
        get() = _filteredBranches

    init {
        _filteredBranches.value = branchManager.branches.value
    }

    fun chipPicked(chip: View){
        val chip = chip as Chip
        when(chip.text){
            CHIP_TITTLE_A_TO_Z -> {
                _filteredBranches.value = _filteredBranches.value?.sortAtoZ()
            }
            CHIP_TITTLE_DISTANCE -> {
                _filteredBranches.value = _filteredBranches.value?.sortByLocation()
            }
        }
    }

    private fun List<Branch>.sortByLocation(): List<Branch> =
        this.sortedBy {
            locationAdapter.lastLocation?.distanceTo(getBranchLocation(it))
        }

    private fun List<Branch>.sortAtoZ(): List<Branch> =
        this.sortedBy { it.name }
}