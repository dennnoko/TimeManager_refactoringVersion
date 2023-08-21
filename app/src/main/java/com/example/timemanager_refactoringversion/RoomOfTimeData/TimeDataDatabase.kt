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

/*
DayDatabaseという名前は、もともとは1日の行動記録を付けるアプリにしようとしていたから。実際には特に期間は指定しておらず、ユーザーが任意のタイミングでデータをリセットできる仕様となった。
ここではデータベースクラスの作成のメソッドが定義してあり、データベースが作成されていない場合のみデータベースを作成し、作成済みであれば既存のデータベースを返す。
 */