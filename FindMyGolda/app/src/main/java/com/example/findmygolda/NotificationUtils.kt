package com.example.findmygolda

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.findmygolda.ActionReceiver.ActionReceiver
import com.example.findmygolda.alerts.ShareIntent

fun getShareAction(context: Context, title: String, content: String): NotificationCompat.Action {
    return NotificationCompat.Action(R.drawable.golda_image, context.getString(R.string.shareActionButton),
        getShareIntent(title, content, context))
}

fun getMarkAsReadAction(context: Context, title: String, content: String, alertId: Long): NotificationCompat.Action {
    return NotificationCompat.Action(R.drawable.golda_image, context.getString(R.string.MarkAsRead),
        getPendingIntentMarkAsRead(context, alertId))
}

fun getBackToAppPendingIntent(context: Context): PendingIntent {
    val backToApp = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    return PendingIntent.getActivity(context, 0, backToApp, 0)
}

fun getDeleteAlertPendingIntent(context: Context, alertId: Long): PendingIntent? {
    val deleteNotification = Intent(context, ActionReceiver::class.java)
    deleteNotification.putExtra(Constants.ACTION, Constants.ACTION_DELETE)
    deleteNotification.putExtra(Constants.ALERT_ID_KEY, alertId)
    return PendingIntent.getBroadcast(
        context,
        Constants.REQUEST_CODE_PENDING_INTENT_DELETE_ALERT,
        deleteNotification,
        PendingIntent.FLAG_CANCEL_CURRENT
    )
}

private fun getShareIntent(
    title: String,
    content: String,
    context: Context
): PendingIntent? {
    val shareIntent = ShareIntent.getShareIntent(title, content)
    return PendingIntent.getActivity(context, 0, shareIntent, 0)
}

private fun getPendingIntentMarkAsRead(context: Context, alertId: Long): PendingIntent? {
    val markAsReadIntent = Intent(context, ActionReceiver::class.java)
    markAsReadIntent.putExtra(Constants.ACTION, Constants.ACTION_MARK_AS_READ)
    markAsReadIntent.putExtra(Constants.ALERT_ID_KEY, alertId)
    return PendingIntent.getBroadcast(
        context,
        Constants.REQUEST_CODE_PENDING_INTENT_MARK_AS_READ,
        markAsReadIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
}