package com.example.timemanager_refactoringversion.RoomOfWorkContent

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [WCEntity::class], version = 1, exportSchema = false)
abstract class WCDatabase: RoomDatabase() {
    abstract fun WCDao(): WCDao

    companion object {
        private var wcdb: WCDatabase? = null

        fun getDB(context: Context): WCDatabase {
            if (wcdb == null) {
                wcdb = Room.databaseBuilder(
                    context = context.applicationContext,
                    WCDatabase::class.java,
                    "wcdatabase"
                ).build()
            }
            return wcdb!!
        }
    }
}

/*
作業内容のデータベース
データベースを作成するメソッドが定義されている
 */