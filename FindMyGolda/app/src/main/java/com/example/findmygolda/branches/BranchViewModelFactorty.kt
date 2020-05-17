package com.example.findmygolda.branches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.findmygolda.location.LocationAdapter

class BranchViewModelFactorty (
    private val branchManager: BranchManager,
    private val locationAdapter: LocationAdapter
) : ViewModelProvider.Factory  {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BranchesViewModel::class.java)) {
            return BranchesViewModel(branchManager, locationAdapter) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}