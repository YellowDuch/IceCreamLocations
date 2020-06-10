package com.example.findmygolda.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.findmygolda.Constants.Companion.ALERTS_TABLE_NAME

@Entity(tableName = ALERTS_TABLE_NAME)
data class Alert(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val time: Long = System.currentTimeMillis(),
    val title: String,
    val description: String,
    val branchId: Long,
    val isRead: Boolean = false
)