package com.example.findmygolda.ActionReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.findmygolda.Constants.Companion.ACTION
import com.example.findmygolda.Constants.Companion.ACTION_DELETE
import com.example.findmygolda.Constants.Companion.ACTION_MARK_AS_READ
import com.example.findmygolda.Constants.Companion.ALERT_ID_KEY
import com.example.findmygolda.Constants.Companion.NOT_EXIST
import com.example.findmygolda.alerts.AlertManager
import com.example.findmygolda.database.Alert
import kotlinx.coroutines.*

class ActionReceiver : BroadcastReceiver() {
    private var actionReceiverJob = Job()
    private val coroutineScope = CoroutineScope(
        actionReceiverJob + Dispatchers.Main)

    override fun onReceive(context: Context?, intent: Intent?) {
        val alertManager = context?.let { AlertManager.getInstance(it) }
        val action = intent!!.getStringExtra(ACTION)
        val alertId = intent!!.getLongExtra(ALERT_ID_KEY, NOT_EXIST)

        if (action == ACTION_MARK_AS_READ) {
            markAlertAsRead(alertId, alertManager)
            //This is used to close the notification tray
            val it = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
            context!!.sendBroadcast(it)
        }
        if (action== ACTION_DELETE){
            deleteAlert(alertId, alertManager)
        }
    }

    private fun markAlertAsRead(
        alertId: Long,
        alertManager: AlertManager?
    ) {
        if (alertId != NOT_EXIST) {
            coroutineScope.launch {
                withContext(Dispatchers.IO) {
                    val alert = alertManager?.getAlert(alertId)
                    alert?.let { alertManager?.update(
                        Alert(alert.id,
                        alert.time,
                        alert.title,
                        alert.description,
                        alert.branchId,
                        true)
                    ) }
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