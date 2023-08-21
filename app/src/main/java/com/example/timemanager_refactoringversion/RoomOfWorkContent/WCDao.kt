package com.example.timemanager_refactoringversion.RoomOfWorkContent

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WCDao {
    //作業内容の追加
    @Insert
    suspend fun insertWC(wc: WCEntity)
    //IDで作業を選択して削除
    @Query("delete from WCEntity where id = :id")
    suspend fun deleteWCById(id: Int)
    //リストの取得
    @Query("select * from WCEntity")
    fun getAll(): Flow<List<WCEntity>>
    //リストの中のStringのみのリストを取得
    // workContentは取得したいテーブルのカラム名
    @Query("select distinct workContent from WCEntity")
    fun getAllName(): Flow<List<String>>
}