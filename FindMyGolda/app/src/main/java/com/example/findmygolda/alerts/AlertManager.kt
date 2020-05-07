package com.example.findmygolda.alerts

import android.app.Application
import android.graphics.BitmapFactory
import android.location.Location
import androidx.preference.PreferenceManager
import com.example.findmygolda.R
import com.example.findmygolda.database.AlertDatabase
import com.example.findmygolda.database.AlertEntity
import com.example.findmygolda.location.ILocationChanged
import com.example.findmygolda.network.BranchManager
import kotlinx.coroutines.*

class AlertManager(val application: Application, private val branchManager: BranchManager):ILocationChanged {
    private val dataSource = (AlertDatabase.getInstance(application)).alertDatabaseDAO
    val alerts = dataSource.getAllAlerts()
    private var alertManagerJob = Job()
    private val coroutineScope = CoroutineScope(
        alertManagerJob + Dispatchers.Main )
    private val preferences = PreferenceManager.getDefaultSharedPreferences(application)
    private val maxDistanceFromBranch = preferences.getInt("radiusFromBranch", 5).times(100)
    private val minTimeBetweenAlerts = parseMinutesToMilliseconds(preferences.getInt("timeBetweenNotifications", 1).times(5))
    private val notificationHelper = NotificationHelper(application.applicationContext)

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
        return (minutes * 60000).toLong()
    }

    private fun notify(name:String, discounts:String, branchId:Int){
        val icon = BitmapFactory.decodeResource(
          application.resources,
          R.drawable.golda_imag)

        dataSource.insert(
            AlertEntity(title = name,
                description = discounts,
                branchId = branchId,
                isRead = false)
        )
        notificationHelper.notify(name, discounts, R.drawable.golda_imag, icon)
    }

    fun updateAlert(alert: AlertEntity){
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
}