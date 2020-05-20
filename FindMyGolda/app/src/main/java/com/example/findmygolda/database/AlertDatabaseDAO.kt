package com.example.findmygolda.database

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface AlertDatabaseDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(alert: AlertEntity):Long

    @Query("SELECT * FROM alerts WHERE id = :id")
    fun getAlertById(id: Long) : AlertEntity?

    @Query("SELECT * FROM alerts ORDER BY id DESC")
    fun getAllAlerts() : LiveData<List<AlertEntity>>

    @Query("SELECT * FROM alerts ORDER BY id DESC LIMIT 1")
    fun getLastAlert(): AlertEntity?

    @Query("SELECT * from alerts WHERE branchId = :branchId ORDER BY time DESC LIMIT 1")
    fun getLastAlertOfBranch(branchId: String): AlertEntity?

    @Update
    fun update(alert: AlertEntity)

    @Delete
    fun delete(alert: AlertEntity)
}