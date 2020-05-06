package com.example.findmygolda.alerts

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.findmygolda.database.AlertDatabaseDAO

class AlertViewModelFactory(
    private val alertManager: AlertManager) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlertsViewModel::class.java)) {
            return AlertsViewModel(alertManager) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
