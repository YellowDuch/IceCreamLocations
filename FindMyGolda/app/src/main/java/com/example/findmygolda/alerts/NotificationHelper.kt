package com.example.findmygolda.alerts

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationHelper(val context: Context) {

    private val notificationManager = NotificationManagerCompat.from(context)

    private fun doseChannelExist(id: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationChannel = notificationManager.getNotificationChannel(id)
            return notificationChannel != null
        }
        return false
    }

    fun createChannel(id: String, importance: Int, channelDescription: String, name: String) {
        if (doseChannelExist(id)) {
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = importance
            val channel = NotificationChannel(id, name, importance).apply {
                description = channelDescription
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun notify(
        id: Long,
        title: String,
        content: String = "",
        smallIcon: Int,
        groupId: String,
        channelId: String,
        icon: Bitmap? = null,
        isAutoCancellation: Boolean = true,
        contentIntent: PendingIntent? = null,
        deleteIntent: PendingIntent? = null,
        vararg actions: NotificationCompat.Action
    ) {
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(smallIcon)
            .setLargeIcon(icon)
            .setContentTitle(title)
            .setContentText(content)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setGroup(groupId)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(contentIntent)
            .setDeleteIntent(deleteIntent)
            .setAutoCancel(isAutoCancellation)

        for (action in actions) {
            notification.addAction(action)
        }

        notificationManager.notify(id.toInt(), notification.build())
    }

    fun cancelNotification(id: Int) {
        notificationManager.cancel(id)
    }
}