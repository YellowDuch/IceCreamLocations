package com.example.findmygolda.alerts

import android.content.Context
import android.graphics.BitmapFactory
import android.location.Location
import androidx.preference.PreferenceManager
import com.example.findmygolda.Constants.Companion.DEFAULT_DISTANCE_TO_BRANCH
import com.example.findmygolda.Constants.Companion.DEFAULT_TIME_BETWEEN_ALERTS
import com.example.findmygolda.Constants.Companion.HUNDREDS_METERS
import com.example.findmygolda.Constants.Companion.MINUTES_TO_MILLISECONDS
import com.example.findmygolda.Constants.Companion.SIZE_OF_JUMP
import com.example.findmygolda.Constants.Companion.NOTIFICATION_IMAGE_ICON
import com.example.findmygolda.Constants.Companion.PREFERENCE_RADIUS_FROM_BRANCH
import com.example.findmygolda.Constants.Companion.PREFERENCE_TIME_BETWEEN_NOTIFICATIONS
import com.example.findmygolda.database.AlertDatabase
import com.example.findmygolda.database.AlertEntity
import com.example.findmygolda.location.ILocationChanged
import com.example.findmygolda.branches.BranchManager
import com.example.findmygolda.location.LocationAdapter
import kotlinx.coroutines.*

class AlertManager(val context: Context):ILocationChanged {
    private val branchManager = BranchManager.getInstance(context)
    private val dataSource = (AlertDatabase.getInstance(context)).alertDatabaseDAO
    val alerts = dataSource.getAllAlerts()
    private var alertManagerJob = Job()
    private val coroutineScope = CoroutineScope(
        alertManagerJob + Dispatchers.Main)
    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)
    var maxDistanceFromBranch = preferences.getInt(PREFERENCE_RADIUS_FROM_BRANCH, DEFAULT_DISTANCE_TO_BRANCH).times(HUNDREDS_METERS)
    var intervalBetweenIdenticalNotifications = parseMinutesToMilliseconds(preferences.getInt(PREFERENCE_TIME_BETWEEN_NOTIFICATIONS,
        DEFAULT_TIME_BETWEEN_ALERTS).times(SIZE_OF_JUMP))
    private val notificationHelper = NotificationHelper(context.applicationContext)

    init {
        LocationAdapter.getInstance(context).subscribeToLocationChangeEvent(this)
    }

    override fun locationChanged(location: Location) {
        alertIfNeeded(location)
    }

    private fun alertIfNeeded(location: Location){
        val branches = branchManager.branches.value

        branches?.forEach{
            if(branchManager.isDistanceInRange(location, it, maxDistanceFromBranch)){
                coroutineScope.launch{
                    withContext(Dispatchers.IO){
                        val lastAlert = dataSource.getLastAlertOfBranch(it.id)
                        if(lastAlert == null || hasIntervalExceeded(lastAlert.time,System.currentTimeMillis(), intervalBetweenIdenticalNotifications)){
                            val alertId = addAlert(it.name, it.discounts, it.id.toInt())
                            notify(it.name, it.discounts, alertId, NOTIFICATION_IMAGE_ICON)
                        }
                    }
                }
            }
        }
    }

    private fun hasIntervalExceeded(startedTime: Long, finishedTime:Long, interval:Long): Boolean {
        return (finishedTime - startedTime) >= interval
    }

    private fun notify(name: String, discounts: String, alertId: Long, drawableImage: Int){
        val icon = BitmapFactory.decodeResource(
            context.resources,
          drawableImage)
        notificationHelper.notify(name, discounts, drawableImage, icon, alertId)
    }

    private fun addAlert(name:String, discounts:String, branchId:Int): Long{
        return dataSource.insert(
                AlertEntity(title = name,
                    description = discounts,
                    branchId = branchId,
                    isRead = false)
            )
    }

    fun changeIsReadStatus(alert: AlertEntity){
        coroutineScope.launch{
            withContext(Dispatchers.IO){
                dataSource.insert(
                    AlertEntity(alert.id,
                        alert.time,
                        alert.title,
                        alert.description,
                        alert.branchId,
                        !alert.isRead)
                )
            }
        }
    }

    fun markAsRead(alert: AlertEntity){
        coroutineScope.launch{
            withContext(Dispatchers.IO){
                dataSource.insert(
                    AlertEntity(alert.id,
                        alert.time,
                        alert.title,
                        alert.description,
                        alert.branchId,
                        true)
                )
            }
        }
    }

    fun deleteAlert(alert: AlertEntity){
        coroutineScope.launch{
            withContext(Dispatchers.IO){
                dataSource.delete(alert)
                notificationHelper.cancelNotification(context, alert.id.toInt())
            }
        }
    }

    fun getAlert(id: Long): AlertEntity? {
        return dataSource.getAlertById(id)
    }

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
}