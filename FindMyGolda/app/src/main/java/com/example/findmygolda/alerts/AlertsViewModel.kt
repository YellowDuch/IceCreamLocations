package com.example.findmygolda.alerts

import android.app.Application
import androidx.lifecycle.ViewModel
import com.example.findmygolda.database.AlertEntity

class AlertsViewModel(application: Application) : ViewModel()  {
    private val alertManager = AlertManager.getInstance(application)
    val alerts = alertManager.alerts

    fun changeIsReadStatus(alert:AlertEntity){
        alertManager.changeIsReadStatus(alert)
    }

    fun deleteAlert(alert: AlertEntity){
        alertManager.deleteAlert(alert)
    }
}