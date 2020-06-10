package com.example.findmygolda.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.findmygolda.Constants.Companion.DB_NAME

@Database(entities = [Alert::class, Branch :: class], version = 10, exportSchema = false)
abstract class DB : RoomDatabase() {

    abstract val alertDatabaseDAO: AlertDatabaseDAO
    abstract val branchDatabaseDAO: BranchDatabaseDAO

    companion object {

        @Volatile
        private var INSTANCE: DB? = null

        fun getInstance(context: Context): DB {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.applicationContext,
                            DB::class.java,
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

