package com.example.findmygolda.database

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface AlertDatabaseDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(alert: Alert):Long

    @Query("SELECT * FROM alerts WHERE id = :id")
    fun getAlertById(id: Long) : Alert?

    @Query("SELECT * FROM alerts ORDER BY id DESC")
    fun getAllAlerts() : LiveData<List<Alert>>

    @Query("SELECT * from alerts WHERE branchId = :branchId ORDER BY time DESC LIMIT 1")
    fun getLastAlertOfBranch(branchId: String): Alert?

    @Update
    fun update(alert: Alert)

    @Delete
    fun delete(alert: Alert)
}