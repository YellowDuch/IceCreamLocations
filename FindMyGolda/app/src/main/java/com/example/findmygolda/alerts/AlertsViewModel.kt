package com.example.findmygolda.alerts

import android.app.Application
import androidx.lifecycle.ViewModel
import com.example.findmygolda.database.Alert

class AlertsViewModel(application: Application) : ViewModel()  {
    private val alertManager = AlertManager.getInstance(application)
    val alerts = alertManager.alerts

    fun changeIsReadStatus(alert:Alert){
        alertManager.update(Alert(alert.id,
            alert.time,
            alert.title,
            alert.description,
            alert.branchId,
            !alert.isRead))
    }

    fun deleteAlert(alert: Alert){
        alertManager.deleteAlert(alert)
    }
}