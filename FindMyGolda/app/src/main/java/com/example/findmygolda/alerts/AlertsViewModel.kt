package com.example.findmygolda.alerts

import android.app.Application
import androidx.lifecycle.ViewModel
import com.example.findmygolda.database.AlertEntity

class AlertsViewModel(application: Application) : ViewModel()  {
    private val alertManager = AlertManager.getInstance(application)
    val alerts = alertManager.alerts

    fun changeIsReadStatus(alert:AlertEntity){
        alertManager.update(AlertEntity(alert.id,
            alert.time,
            alert.title,
            alert.description,
            alert.branchId,
            !alert.isRead))
    }

    fun deleteAlert(alert: AlertEntity){
        alertManager.deleteAlert(alert)
    }
}