package com.example.findmygolda.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.findmygolda.Constants.Companion.ALERTS_TABLE_NAME

@Entity(tableName = ALERTS_TABLE_NAME)
data class Alert(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val time: Long = System.currentTimeMillis(),
    val title: String,
    val description: String,
    val branchId: Int,
    val isRead: Boolean = false
)