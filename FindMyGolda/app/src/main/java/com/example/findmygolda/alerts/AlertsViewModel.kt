package com.example.findmygolda.alerts

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.findmygolda.database.AlertDatabaseDAO
import kotlinx.coroutines.*

class AlertsViewModel(
    val database: AlertDatabaseDAO,
    application: Application
) : AndroidViewModel(application)  {

    val alerts = database.getAllAlerts()

}