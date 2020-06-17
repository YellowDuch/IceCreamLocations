package com.example.findmygolda.ActionReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.findmygolda.Constants.Companion.ACTION
import com.example.findmygolda.Constants.Companion.ACTION_DELETE
import com.example.findmygolda.Constants.Companion.ACTION_MARK_AS_READ
import com.example.findmygolda.Constants.Companion.EXTRA_ID
import com.example.findmygolda.Constants.Companion.NOT_EXIST
import com.example.findmygolda.alerts.AlertManager

class ActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val alertManager = context?.let { AlertManager.getInstance(it) }
        val action = intent!!.getStringExtra(ACTION)
        val alertId = intent.getLongExtra(EXTRA_ID, NOT_EXIST)

        when(action){
            ACTION_MARK_AS_READ -> {
                alertManager?.changeIsReadToggleStatus(alertId,true)
                //This is used to close the notification tray
                val it = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
                context!!.sendBroadcast(it)
            }
            ACTION_DELETE ->   alertManager?.deleteAlert(alertId)
        }
    }
}