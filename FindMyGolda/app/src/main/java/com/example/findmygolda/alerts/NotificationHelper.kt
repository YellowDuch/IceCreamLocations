package com.example.findmygolda.alerts

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.findmygolda.ActionReceiver.ActionReceiver
import com.example.findmygolda.Constants.Companion.CHANNEL_ID
import com.example.findmygolda.Constants.Companion.CHANNEL_NAME
import com.example.findmygolda.Constants.Companion.GROUP_ID
import com.example.findmygolda.Constants.Companion.NOTIFICATION_CHANEL_DESCRIPTION
import com.example.findmygolda.Constants.Companion.REQUEST_CODE_PENDING_INTENT_DELETE_ALERT
import com.example.findmygolda.Constants.Companion.REQUEST_CODE_PENDING_INTENT_MARK_AS_READ
import com.example.findmygolda.MainActivity
import com.example.findmygolda.R

class NotificationHelper(val context: Context) {

  private fun createChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val importance = NotificationManager.IMPORTANCE_HIGH
      val descriptionText = NOTIFICATION_CHANEL_DESCRIPTION
      val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
        description = descriptionText
      }
      val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      notificationManager.createNotificationChannel(channel)
    }
  }

  fun notify(title: String, content: String,smallIcon: Int, icon: Bitmap, alertId: Long) {
    createChannel()
    val notificationManager = NotificationManagerCompat.from(context)
    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
      .setSmallIcon(smallIcon)
      .setLargeIcon(icon)
      .setContentTitle(title)
      .setContentText(content)
      .setDefaults(NotificationCompat.DEFAULT_ALL)
      .setGroup(GROUP_ID)
      .setPriority(NotificationCompat.PRIORITY_DEFAULT)
      .setContentIntent(getBackToAppIntent())
      .addAction(R.drawable.mapbox_logo_icon, context.getString(R.string.shareActionButton),
        getShareIntent(title, content))
      .addAction(R.drawable.mapbox_logo_icon, "Mark as read",
        getPendingIntentMarkAsRead(alertId))
      .setDeleteIntent(getPendingIntentDeleteAlert(alertId))
      .setAutoCancel(true)
      .build()
    notificationManager.notify(alertId.toInt(), notification)
  }

  private fun getBackToAppIntent(): PendingIntent {
    val backToApp = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
      }
    return PendingIntent.getActivity(context, 0, backToApp, 0)
  }

  private fun getShareIntent(
      title: String,
      content: String
  ): PendingIntent? {
    val shareIntent = ShareIntent().getShareIntent(title, content)
    return PendingIntent.getActivity(context, 0, shareIntent, 0)
  }

  private fun getPendingIntentDeleteAlert(alertId: Long): PendingIntent? {
    val deleteNotification = Intent(context, ActionReceiver::class.java)
    deleteNotification.putExtra("action", "delete")
    deleteNotification.putExtra("alertId", alertId)
    return PendingIntent.getBroadcast(
      context,
      REQUEST_CODE_PENDING_INTENT_DELETE_ALERT,
      deleteNotification,
      PendingIntent.FLAG_CANCEL_CURRENT
    )
  }

  private fun getPendingIntentMarkAsRead(alertId: Long): PendingIntent? {
    val markAsReadIntent = Intent(context, ActionReceiver::class.java)
    markAsReadIntent.putExtra("action", "markAsRead")
    markAsReadIntent.putExtra("alertId", alertId)
    return PendingIntent.getBroadcast(
      context,
      REQUEST_CODE_PENDING_INTENT_MARK_AS_READ,
      markAsReadIntent,
      PendingIntent.FLAG_UPDATE_CURRENT
    )
  }
}