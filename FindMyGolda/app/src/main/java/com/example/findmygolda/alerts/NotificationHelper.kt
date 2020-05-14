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
import com.example.findmygolda.Constants.Companion.CHANNEL_ID
import com.example.findmygolda.Constants.Companion.CHANNEL_NAME
import com.example.findmygolda.Constants.Companion.GROUP_ID
import com.example.findmygolda.MainActivity
import com.example.findmygolda.R

class NotificationHelper(val context: Context) {

  var noificationId = 1

  private fun createChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val importance = NotificationManager.IMPORTANCE_HIGH
      val descriptionText = "Golda notifications"
      val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
        description = descriptionText
      }
      val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      notificationManager.createNotificationChannel(channel)
    }
  }

  fun notify(title: String, content: String,smallIcon: Int, icon: Bitmap) {
    createChannel()
    val intent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntentBackToTheApp: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
    val shareIntent = ShareIntent().getShareIntent(title, content)
    val sharePendingIntent = PendingIntent.getActivity(context,0,shareIntent,0)
    val notificationManager = NotificationManagerCompat.from(context)
    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
      .setSmallIcon(smallIcon)
      .setLargeIcon(icon)
      .setContentTitle(title)
      .setContentText(content)
      .setDefaults(NotificationCompat.DEFAULT_ALL)
      .setGroup(GROUP_ID)
      .setPriority(NotificationCompat.PRIORITY_DEFAULT)
      .setContentIntent(pendingIntentBackToTheApp)
      .addAction(R.drawable.mapbox_logo_icon, "Share",
        sharePendingIntent)
      .setAutoCancel(true)
      .build()
    notificationManager.notify(noificationId, notification)
    noificationId ++
  }

}