package com.example.findmygolda.ActionReceiver

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        val action = intent!!.getStringExtra("action")
        if (action == "markAsRead") {
            //performAction1()

            //This is used to close the notification tray
            val it = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
            context!!.sendBroadcast(it)
        }
    }
}