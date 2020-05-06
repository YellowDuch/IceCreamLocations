package com.example.findmygolda.alerts

import androidx.lifecycle.ViewModel

class AlertsViewModel(alertManager: AlertManager) : ViewModel()  {
    val alerts = alertManager.alerts
}