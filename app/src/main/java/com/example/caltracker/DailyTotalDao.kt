package com.example.caltracker

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyTotalDao {
    @Insert
    suspend fun insertDailyTotal(dailyTotal: DailyTotalEntity)

    @Delete
    suspend fun deleteDailyTotal(dailyTotal: DailyTotalEntity)

    @Query("SELECT * FROM daily_totals")
    fun getAllDailyTotals(): Flow<List<DailyTotalEntity>>

    @Query("DELETE FROM daily_totals WHERE date < :cutoffDate")
    suspend fun deleteTotalsOlderThan(cutoffDate: String)
}