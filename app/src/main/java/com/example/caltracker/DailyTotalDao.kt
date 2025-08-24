package com.example.caltracker

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyTotalDao {

    @Query("SELECT * FROM daily_total")
    fun getAllDailyTotals(): Flow<List<DailyTotalEntity>>

    @Insert
    suspend fun insert(dailyTotal: DailyTotalEntity)

    @Update
    suspend fun update(dailyTotal: DailyTotalEntity)

    @Delete
    suspend fun delete(dailyTotal: DailyTotalEntity)

    @Query("SELECT * FROM daily_total WHERE date = :date")
    suspend fun getDailyTotalByDate(date: String): DailyTotalEntity?
}