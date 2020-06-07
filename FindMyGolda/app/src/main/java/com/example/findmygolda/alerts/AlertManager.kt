package com.example.findmygolda.alerts

import android.content.Context
import android.graphics.BitmapFactory
import android.location.Location
import androidx.preference.PreferenceManager
import com.example.findmygolda.Constants.Companion.DEFAULT_DISTANCE_TO_BRANCH
import com.example.findmygolda.Constants.Companion.DEFAULT_TIME_BETWEEN_ALERTS
import com.example.findmygolda.Constants.Companion.HUNDREDS_METERS
import com.example.findmygolda.Constants.Companion.SIZE_OF_JUMP
import com.example.findmygolda.Constants.Companion.NOTIFICATION_IMAGE_ICON
import com.example.findmygolda.Constants.Companion.PREFERENCE_RADIUS_FROM_BRANCH
import com.example.findmygolda.Constants.Companion.PREFERENCE_TIME_BETWEEN_NOTIFICATIONS
import com.example.findmygolda.database.DB
import com.example.findmygolda.database.Alert
import com.example.findmygolda.location.ILocationChanged
import com.example.findmygolda.branches.BranchManager
import com.example.findmygolda.database.Branch
import com.example.findmygolda.location.LocationAdapter
import kotlinx.coroutines.*

class AlertManager(val context: Context):ILocationChanged {
    private val branchManager = BranchManager.getInstance(context)
    private val dataSource = (DB.getInstance(context)).alertDatabaseDAO
    val alerts = dataSource.getAllAlerts()
    private var alertManagerJob = Job()
    private val coroutineScope = CoroutineScope(
        alertManagerJob + Dispatchers.Main)
    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)
    var maxDistanceFromBranch = preferences.getInt(PREFERENCE_RADIUS_FROM_BRANCH, DEFAULT_DISTANCE_TO_BRANCH).times(HUNDREDS_METERS)
    var intervalBetweenIdenticalNotifications = parseMinutesToMilliseconds(preferences.getInt(PREFERENCE_TIME_BETWEEN_NOTIFICATIONS,
        DEFAULT_TIME_BETWEEN_ALERTS).times(SIZE_OF_JUMP))
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
    }

    override fun locationChanged(location: Location?) {
        location?: return
        alertIfBranchInRangeAndTimeExceeded(location)
    }

    private fun alertIfBranchInRangeAndTimeExceeded(location: Location) {
        val branches = branchManager.branches.value
        branches?.forEach{branch ->
            if(branchManager.isDistanceInRange(location, branch, maxDistanceFromBranch)){
                coroutineScope.launch{
                    withContext(Dispatchers.IO){
                        //val lastAlert = dataSource.getLastAlertOfBranch(branch.id)
                        val lastAlert = getLastAlertOfBranch(branch.id.toLong())
                        if(lastAlert == null || hasIntervalExceeded(lastAlert.time,System.currentTimeMillis(), intervalBetweenIdenticalNotifications)){
                            addAlert(branch)
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
        notificationHelper.notify(name, discounts,icon, drawableImage, alertId)
    }

    private fun addAlert(branch: Branch){
        coroutineScope.launch{
            withContext(Dispatchers.IO){
                val alertId = dataSource.insert(
                    Alert(title = branch.name,
                        description = branch.discounts,
                        branchId = branch.id.toInt(),
                        isRead = false)
                )
                notify(branch.name, branch.discounts, alertId, NOTIFICATION_IMAGE_ICON)
            }
        }
    }

    fun update(alert: Alert){
        coroutineScope.launch{
            withContext(Dispatchers.IO){
                dataSource.insert(alert)
            }
        }
    }

    fun deleteAlert(alert: Alert){
        coroutineScope.launch{
            withContext(Dispatchers.IO){
                dataSource.delete(alert)
                notificationHelper.cancelNotification(context, alert.id.toInt())
            }
        }
    }

    fun getAlert(id: Long): Alert? {
        return dataSource.getAlertById(id)
    }

    fun getLastAlertOfBranch(id: Long): Alert? {
        var lastAlert: Alert? = null
        alerts.value?.forEach {alert->
            if (alert.id == id){
                if (lastAlert == null){
                    lastAlert = alert
                }else if (lastAlert!!.time < alert.time){
                    lastAlert = alert
                }
            }
        }
        return lastAlert
    }
}