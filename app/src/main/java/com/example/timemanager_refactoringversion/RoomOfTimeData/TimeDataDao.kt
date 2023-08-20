package com.example.timemanager_refactoringversion.RoomOfTimeData

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TimeDataDao {
    //データ追加
    @Insert
    suspend fun insertData(timeData: TimeDataEntity)
    //全データ削除
    @Delete
    suspend fun deleteAll(dataList: List<TimeDataEntity>)
    //全データのリストを取得
    @Query("select * from TDE")
    fun getAll(): Flow<List<TimeDataEntity>>
    //work content（wc）のリストを返す
    @Query("select distinct workContent from TDE")
    suspend fun getDistinctCWList(): List<String>
    //作業内容を指定し、合計時間を返す
    @Query("select sum(timeData) from TDE where workContent = :workContent")
    suspend fun getTotalTimeByWC(workContent: String): Int
}