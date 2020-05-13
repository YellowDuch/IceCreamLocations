package com.example.findmygolda.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.findmygolda.Constants.Companion.DB_NAME

@Database(entities = [AlertEntity::class, BranchEntity :: class], version = 7, exportSchema = false)
abstract class AlertDatabase : RoomDatabase() {

    abstract val alertDatabaseDAO: AlertDatabaseDAO
    abstract val branchDatabaseDAO: BranchDatabaseDAO

    companion object {

        @Volatile
        private var INSTANCE: AlertDatabase? = null

        fun getInstance(context: Context): AlertDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.applicationContext,
                            AlertDatabase::class.java,
                            DB_NAME
                        ).fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}

