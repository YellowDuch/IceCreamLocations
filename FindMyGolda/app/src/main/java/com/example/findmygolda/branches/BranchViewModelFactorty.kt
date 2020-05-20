package com.example.findmygolda.branches

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.findmygolda.location.LocationAdapter

class BranchViewModelFactorty (
    private val application: Application
) : ViewModelProvider.Factory  {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BranchesViewModel::class.java)) {
            return BranchesViewModel(application) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}