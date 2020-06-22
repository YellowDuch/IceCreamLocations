package com.example.findmygolda.alerts

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.findmygolda.database.Alert

class AlertsViewModel(application: Application) : AndroidViewModel(application)  {
    private val alertManager = AlertManager.getInstance(application)
    val alerts = alertManager.alerts

    fun changeIsReadToggleStatus(alert:Alert){
        alertManager.changeIsReadToggleStatus(alert.id, !alert.isRead)
    }

    fun deleteAlert(alert: Alert){
        alertManager.deleteAlert(alert.id)
    }
}