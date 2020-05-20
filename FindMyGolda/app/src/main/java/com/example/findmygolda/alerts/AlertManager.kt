package com.example.findmygolda.alerts

import android.content.Context
import android.graphics.BitmapFactory
import android.location.Location
import androidx.preference.PreferenceManager
import com.example.findmygolda.Constants.Companion.DEFAULT_DISTANCE_TO_BRANCH
import com.example.findmygolda.Constants.Companion.DEFAULT_TIME_BETWEEN_ALERTS
import com.example.findmygolda.Constants.Companion.HUNDREDS_METERS
import com.example.findmygolda.Constants.Companion.MINUTES_TO_MILLISECONDS
import com.example.findmygolda.Constants.Companion.MIN_TIME_BETWEEN_NOTIFICATIONS
import com.example.findmygolda.Constants.Companion.PREFERENCE_RADIUS_FROM_BRANCH
import com.example.findmygolda.Constants.Companion.PREFERENCE_TIME_BETWEEN_NOTIFICATIONS
import com.example.findmygolda.R
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
    private val maxDistanceFromBranch = preferences.getInt(PREFERENCE_RADIUS_FROM_BRANCH, DEFAULT_DISTANCE_TO_BRANCH).times(HUNDREDS_METERS)
    private val minTimeBetweenAlerts = parseMinutesToMilliseconds(preferences.getInt(PREFERENCE_TIME_BETWEEN_NOTIFICATIONS,
        DEFAULT_TIME_BETWEEN_ALERTS).times(MIN_TIME_BETWEEN_NOTIFICATIONS))
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
                        val lastAlert = dataSource.getLastAlertOfBranch(it.id.toInt())
                        if(hasTimePast(lastAlert?.time,System.currentTimeMillis(), minTimeBetweenAlerts)){
                            notify(it.name, it.discounts, it.id.toInt())
                        }
                    }
                }
            }
        }
    }

    private fun hasTimePast(startedTime: Long?, finishedTime:Long, maxDifference:Long): Boolean {
        if (startedTime == null)
            return true
        return (finishedTime - startedTime) >= maxDifference
    }

    private fun parseMinutesToMilliseconds(minutes : Int) : Long{
        return (minutes * MINUTES_TO_MILLISECONDS).toLong()
    }

    private fun notify(name:String, discounts:String, branchId:Int){
        val icon = BitmapFactory.decodeResource(
            context.resources,
          R.drawable.golda_imag)

       val alertId =  dataSource.insert(
            AlertEntity(title = name,
                description = discounts,
                branchId = branchId,
                isRead = false)
        )
        notificationHelper.notify(name, discounts, R.drawable.golda_imag, icon, alertId)
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