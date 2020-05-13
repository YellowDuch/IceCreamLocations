package com.example.findmygolda.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.findmygolda.Constants.Companion.BRANCHES_TABLE_NAME

@Entity(tableName = BRANCHES_TABLE_NAME)
data class BranchEntity constructor(
    @PrimaryKey
    val id: String,
    val address: String,
    val discounts: String,
    val latitude: Double,
    val longtitude: Double,
    val phone: String,
    val name: String)