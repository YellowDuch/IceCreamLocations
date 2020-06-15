package com.example.findmygolda.alerts

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.findmygolda.Constants.Companion.GOLDA_CHANNEL_ID

class NotificationHelper(val context: Context) {

    fun isChannelExist(id: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationChannel = notificationManager.getNotificationChannel(id)
            return notificationChannel != null
        }
        return false
    }

    fun createChannel(importance: Int, channelDescription: String, id: String, name: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val importance = importance
            val channel = NotificationChannel(id, name, importance).apply {
                description = channelDescription
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun notify(
        title: String,
        content: String = "",
        icon: Bitmap? = null,
        smallIcon: Int,
        groupId: String,
        alertId: Long,
        isAutoCancellation: Boolean = true,
        contentIntent: PendingIntent? = null,
        deleteIntent: PendingIntent? = null,
        vararg actions: NotificationCompat.Action
    ) {
        val notificationManager = NotificationManagerCompat.from(context)

        val notification = NotificationCompat.Builder(context, GOLDA_CHANNEL_ID)
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

        notificationManager.notify(alertId.toInt(), notification.build())
    }

    fun cancelNotification(context: Context, notifyId: Int) {
        val notificationService = Context.NOTIFICATION_SERVICE
        val notificationManager =
            context.getSystemService(notificationService) as NotificationManager
        notificationManager.cancel(notifyId)
    }
}