package com.example.timemanager_refactoringversion.RoomOfTimeData

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TimeDataEntity::class], version = 1, exportSchema = false)
abstract class TimeDataDatabase: RoomDatabase() {
    abstract fun TimeDataDao(): TimeDataDao

    companion object {
        private var timedb: TimeDataDatabase? = null

        fun getdb(context: Context): TimeDataDatabase {
            if (timedb == null) {
                timedb = Room.databaseBuilder(
                    context.applicationContext,
                    TimeDataDatabase::class.java,
                    "time_database"
                ).build()
            }
            return timedb!!
        }
    }
}