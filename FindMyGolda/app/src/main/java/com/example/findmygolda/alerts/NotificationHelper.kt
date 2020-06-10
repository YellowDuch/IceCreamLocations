package com.example.findmygolda.alerts

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.findmygolda.*
import com.example.findmygolda.ActionReceiver.ActionReceiver
import com.example.findmygolda.Constants.Companion.ACTION
import com.example.findmygolda.Constants.Companion.ACTION_DELETE
import com.example.findmygolda.Constants.Companion.ACTION_MARK_AS_READ
import com.example.findmygolda.Constants.Companion.ALERT_ID_KEY
import com.example.findmygolda.Constants.Companion.CHANNEL_ID
import com.example.findmygolda.Constants.Companion.CHANNEL_NAME
import com.example.findmygolda.Constants.Companion.GROUP_ID
import com.example.findmygolda.Constants.Companion.NOTIFICATION_CHANEL_DESCRIPTION
import com.example.findmygolda.Constants.Companion.REQUEST_CODE_PENDING_INTENT_DELETE_ALERT
import com.example.findmygolda.Constants.Companion.REQUEST_CODE_PENDING_INTENT_MARK_AS_READ

class NotificationHelper(val context: Context) {

  init {
      createChannel(NotificationManager.IMPORTANCE_HIGH, NOTIFICATION_CHANEL_DESCRIPTION, CHANNEL_ID, CHANNEL_NAME)
  }

  private fun createChannel(importance: Int, channelDescription: String, Id: String, name: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val importance = importance
      val channel = NotificationChannel(Id, name, importance).apply {
        description = channelDescription
      }
      val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      notificationManager.createNotificationChannel(channel)
    }
  }

  fun notify(title: String,
             content: String = "",
             icon: Bitmap? = null,
             smallIcon: Int,
             groupId: String,
             alertId: Long,
             isAutoCancellation: Boolean = true,
             contentIntent: PendingIntent? = null,
             deleteIntent: PendingIntent? = null,
             vararg actions: NotificationCompat.Action) {
    val notificationManager = NotificationManagerCompat.from(context)

    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
      .setSmallIcon(smallIcon)
      .setLargeIcon(icon)
      .setContentTitle(title)
      .setContentText(content)
      .setDefaults(NotificationCompat.DEFAULT_ALL)
      .setGroup(groupId)
      .setPriority(NotificationCompat.PRIORITY_DEFAULT)
      .setContentIntent(getBackToAppIntent(context))
      .addAction(createShareAction(context, title, content))
      .addAction(createMarkAsReadAction(context, title, content, alertId))
      .setDeleteIntent(getPendingIntentDeleteAlert(context, alertId))
      .setAutoCancel(isAutoCancellation)

    for (action in actions) {
      notification.addAction(action)
    }

    notificationManager.notify(alertId.toInt(), notification.build())
  }

  fun cancelNotification(context: Context, notifyId: Int) {
    val notificationService = Context.NOTIFICATION_SERVICE
    val notificationManager = context.getSystemService(notificationService) as NotificationManager
    notificationManager.cancel(notifyId)
  }
}