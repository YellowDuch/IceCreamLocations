package com.example.findmygolda.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BranchDatabaseDAO {

    @Query("select * from branches")
    fun get(): LiveData<List<Branch>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(branches: List<Branch>)
}