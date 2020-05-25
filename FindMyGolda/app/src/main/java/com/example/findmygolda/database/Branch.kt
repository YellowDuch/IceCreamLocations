package com.example.findmygolda.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.findmygolda.Constants.Companion.BRANCHES_TABLE_NAME
import com.squareup.moshi.Json

@Entity(tableName = BRANCHES_TABLE_NAME)
data class Branch constructor(
    @PrimaryKey
    val id: String,
    val address: String,
    val discounts: String,
    val latitude: Double,
    @Json(name = "longtitude") val longitude: Double,
    val phone: String,
    val name: String)