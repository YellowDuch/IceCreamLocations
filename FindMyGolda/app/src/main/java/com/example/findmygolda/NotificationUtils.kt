package com.example.findmygolda

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.findmygolda.ActionReceiver.ActionReceiver
import com.example.findmygolda.alerts.ShareIntent

fun getShareAction(context: Context, title: String, content: String): NotificationCompat.Action {
    return NotificationCompat.Action(
        R.drawable.golda_image, context.getString(R.string.shareActionButton),
        getShareIntent(title, content, context)
    )
}

fun getAction(
    actionTitle: String,
    actionImage: Int,
    pendingIntent: PendingIntent?
): NotificationCompat.Action {
    return NotificationCompat.Action(
        actionImage, actionTitle,
        pendingIntent
    )
}

fun getBackToAppPendingIntent(context: Context): PendingIntent {
    val backToApp = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    return PendingIntent.getActivity(context, 0, backToApp, 0)
}

private fun getShareIntent(
    title: String,
    content: String,
    context: Context
): PendingIntent? {
    val shareIntent = ShareIntent.getShareIntent(title, content)
    return PendingIntent.getActivity(context, 0, shareIntent, 0)
}

// Every pending intent needs a unique request code
fun getPendingIntent(requestCode: Int, context: Context, alertId: Long, actionId: String): PendingIntent? {
    val intent = Intent(context, ActionReceiver::class.java)
    intent.putExtra(Constants.ACTION, actionId)
    intent.putExtra(Constants.ALERT_ID_KEY, alertId)

    return PendingIntent.getBroadcast(
        context,
        requestCode,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
}