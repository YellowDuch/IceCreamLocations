package com.example.findmygolda.alerts

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.findmygolda.R


const val CHANNEL_ID = "com.example.findMyGolda.branchDetails"
const val GROUP_ID = "com.example.findMyGolda"
const val CHANNEL_NAME = "Golda notifications"

class NotificationHelper(val context: Context) {

  var noificationId = 1

  private fun createChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val importance = NotificationManager.IMPORTANCE_HIGH
      val descriptionText = "channel for notifications"
      val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
        description = descriptionText
      }
      val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      notificationManager.createNotificationChannel(channel)
    }

  }

  fun notify(title: String, content: String,smallIcon: Int, icon: Bitmap) {
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
      .build()
    notificationManager.notify(noificationId, notification)
    noificationId ++
  }
}