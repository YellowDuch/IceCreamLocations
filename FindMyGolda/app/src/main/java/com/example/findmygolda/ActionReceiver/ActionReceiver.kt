package com.example.findmygolda.ActionReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.findmygolda.alerts.AlertManager
import kotlinx.coroutines.*

class ActionReceiver : BroadcastReceiver() {
    private var actionReceiverJob = Job()
    private val coroutineScope = CoroutineScope(
        actionReceiverJob + Dispatchers.Main)

    override fun onReceive(context: Context?, intent: Intent?) {
        val alertManager = context?.let { AlertManager.getInstance(it) }
        val action = intent!!.getStringExtra("action")
        val alertId = intent!!.getLongExtra("alertId", -1)

        if (action == "markAsRead") {
            markAlertAsRead(alertId, alertManager)
            //This is used to close the notification tray
            val it = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
            context!!.sendBroadcast(it)
        }
        if (action== "delete"){
            deleteAlert(alertId, alertManager)
        }
    }

    private fun markAlertAsRead(
        alertId: Long,
        alertManager: AlertManager?
    ) {
        if (alertId != -1L) {
            coroutineScope.launch {
                withContext(Dispatchers.IO) {
                    val alert = alertManager?.getAlert(alertId)
                    alert?.let { alertManager?.markAsRead(it) }
                }
            }
        }
    }

    private fun deleteAlert(
        alertId: Long,
        alertManager: AlertManager?
    ) {
        if (alertId != -1L) {
            coroutineScope.launch {
                withContext(Dispatchers.IO) {
                    val alert = alertManager?.getAlert(alertId)
                    alert?.let { alertManager?.deleteAlert(it) }
                }
            }
        }
    }
}