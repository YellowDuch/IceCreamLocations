package com.example.findmygolda.alerts

import androidx.lifecycle.ViewModel
import com.example.findmygolda.database.AlertEntity

class AlertsViewModel(private val alertManager: AlertManager) : ViewModel()  {
    val alerts = alertManager.alerts

    fun updateAlert(alert:AlertEntity){
        alertManager.updateAlert(alert)
    }

    fun deleteAlert(alert: AlertEntity){
        alertManager.deleteAlert(alert)
    }
}