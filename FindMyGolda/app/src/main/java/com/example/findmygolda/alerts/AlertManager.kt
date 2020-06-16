package com.example.findmygolda.alerts

import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.location.Location
import androidx.preference.PreferenceManager
import com.example.findmygolda.*
import com.example.findmygolda.Constants.Companion.ACTION_DELETE
import com.example.findmygolda.Constants.Companion.ACTION_MARK_AS_READ
import com.example.findmygolda.Constants.Companion.GOLDA_CHANNEL_ID
import com.example.findmygolda.Constants.Companion.DEFAULT_DISTANCE_TO_BRANCH
import com.example.findmygolda.Constants.Companion.DEFAULT_TIME_BETWEEN_ALERTS
import com.example.findmygolda.Constants.Companion.GOLADA_NOTIFICATION_CHANEL_DESCRIPTION
import com.example.findmygolda.Constants.Companion.GOLDA_CHANNEL_NAME
import com.example.findmygolda.Constants.Companion.GROUP_ID
import com.example.findmygolda.Constants.Companion.JUMP_IN_METERS
import com.example.findmygolda.Constants.Companion.JUMP_IN_MINUTES
import com.example.findmygolda.Constants.Companion.NOTIFICATION_IMAGE_ICON
import com.example.findmygolda.Constants.Companion.NOT_EXIST
import com.example.findmygolda.Constants.Companion.RADIUS_FROM_BRANCH_PREFERENCE
import com.example.findmygolda.Constants.Companion.REQUEST_CODE_DELETE_ALERT
import com.example.findmygolda.Constants.Companion.REQUEST_CODE_MARK_AS_READ
import com.example.findmygolda.Constants.Companion.TIME_BETWEEN_NOTIFICATIONS_PREFERENCE
import com.example.findmygolda.database.DB
import com.example.findmygolda.database.Alert
import com.example.findmygolda.location.ILocationChanged
import com.example.findmygolda.branches.BranchManager
import com.example.findmygolda.location.LocationAdapter
import kotlinx.coroutines.*

class AlertManager(val context: Context) : ILocationChanged,
    SharedPreferences.OnSharedPreferenceChangeListener {
    private val branchManager = BranchManager.getInstance(context)
    private val dataSource = (DB.getInstance(context)).alertDatabaseDAO
    val alerts = dataSource.getAllAlerts()
    private val coroutineScope = CoroutineScope(
        Dispatchers.Main
    )
    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)
    var maxDistanceFromBranch =
        preferences.getInt(RADIUS_FROM_BRANCH_PREFERENCE, DEFAULT_DISTANCE_TO_BRANCH)
            .times(JUMP_IN_METERS)
    var intervalBetweenIdenticalNotifications = parseMinutesToMilliseconds(
        preferences.getInt(
            TIME_BETWEEN_NOTIFICATIONS_PREFERENCE,
            DEFAULT_TIME_BETWEEN_ALERTS
        ).times(JUMP_IN_MINUTES)
    )
    private val notificationHelper = NotificationHelper(context.applicationContext)

    companion object {
        @Volatile
        private var INSTANCE: AlertManager? = null

        fun getInstance(context: Context): AlertManager {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = AlertManager(context)
                    INSTANCE = instance
                }
                return instance
            }
        }
    }

    init {
        LocationAdapter.getInstance(context).subscribeToLocationChangeEvent(this)
        preferences.registerOnSharedPreferenceChangeListener(this)
        notificationHelper.createChannel(
            GOLDA_CHANNEL_ID,
            NotificationManager.IMPORTANCE_HIGH,
            GOLADA_NOTIFICATION_CHANEL_DESCRIPTION,
            GOLDA_CHANNEL_NAME
        )
    }

    override fun locationChanged(location: Location?) {
        location ?: return
        alertIfBranchInRangeAndTimeExceeded(location)
    }

    private fun alertIfBranchInRangeAndTimeExceeded(location: Location) {
        val branches = branchManager.branches.value
        branches?.forEach { branch ->
            if (branchManager.isDistanceInRange(
                    location,
                    getBranchLocation(branch),
                    maxDistanceFromBranch
                )
            ) {
                coroutineScope.launch {
                    withContext(Dispatchers.IO) {
                        val lastAlertOfBranch = dataSource.getLastAlertOfBranch(branch.id)

                        if (lastAlertOfBranch == null || hasIntervalExceeded(
                                lastAlertOfBranch.time,
                                System.currentTimeMillis(),
                                intervalBetweenIdenticalNotifications
                            )
                        ) {
                            val alertId = dataSource.insert(
                                Alert(
                                    title = branch.name,
                                    description = branch.discounts,
                                    branchId = branch.id,
                                    isRead = false
                                )
                            )
                            notify(branch.name, branch.discounts, alertId, NOTIFICATION_IMAGE_ICON)
                        }
                    }
                }
            }
        }
    }

    private fun hasIntervalExceeded(startedTime: Long, currentTime: Long, interval: Long): Boolean {
        return (currentTime - startedTime) >= interval
    }

    private fun notify(name: String, discounts: String, alertId: Long, drawableImage: Int) {
        val icon = BitmapFactory.decodeResource(
            context.resources,
            drawableImage
        )
        notificationHelper.notify(
            alertId,
            name,
            discounts,
            drawableImage,
            GROUP_ID,
            GOLDA_CHANNEL_ID,
            icon,
            true,
            getBackToAppPendingIntent(context),
            getPendingIntent(REQUEST_CODE_DELETE_ALERT, context, alertId, ACTION_DELETE),
            getShareAction(context, name, discounts),
            getAction(context.getString(R.string.MarkAsRead),
                R.drawable.golda_image,
                getPendingIntent(REQUEST_CODE_MARK_AS_READ, context, alertId, ACTION_MARK_AS_READ))

        )
    }

    fun changeIsReadToggleStatus(alertId: Long, readStatus: Boolean) {
        if (alertId != NOT_EXIST) {
            coroutineScope.launch {
                withContext(Dispatchers.IO) {
                    val alert = getAlert(alertId)
                    alert?.let {
                        update(
                            Alert(
                                alert.id,
                                alert.time,
                                alert.title,
                                alert.description,
                                alert.branchId,
                                readStatus
                            )
                        )
                    }
                }
            }
        }
    }

    private fun update(alert: Alert) {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                dataSource.insert(alert)
            }
        }
    }

    fun deleteAlert(id: Long) {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                dataSource.deleteAlert(id)
                notificationHelper.cancelNotification(id.toInt())
            }
        }
    }

    private fun getAlert(id: Long): Alert? {
        return dataSource.getAlertById(id)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            RADIUS_FROM_BRANCH_PREFERENCE -> {
                sharedPreferences?.let {
                    maxDistanceFromBranch = sharedPreferences.getInt(
                        RADIUS_FROM_BRANCH_PREFERENCE,
                        DEFAULT_DISTANCE_TO_BRANCH
                    )
                        .times(JUMP_IN_METERS)

                }
            }

            TIME_BETWEEN_NOTIFICATIONS_PREFERENCE -> {
                sharedPreferences?.let {
                    val minutesBetweenNotification = sharedPreferences.getInt(
                        TIME_BETWEEN_NOTIFICATIONS_PREFERENCE,
                        DEFAULT_TIME_BETWEEN_ALERTS
                    ).times(JUMP_IN_MINUTES)

                    intervalBetweenIdenticalNotifications =
                        parseMinutesToMilliseconds(minutesBetweenNotification)
                }
            }
        }
    }
}