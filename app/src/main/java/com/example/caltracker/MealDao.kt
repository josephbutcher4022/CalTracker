package com.example.caltracker

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {

    @Query("SELECT * FROM meal WHERE date = :date ORDER BY id DESC")
    fun getMealsByDate(date: String): Flow<List<MealEntity>>

    fun getMealsForToday(date: String): Flow<List<MealEntity>> = getMealsByDate(date)

    @Insert
    suspend fun insert(meal: MealEntity)

    @Delete
    suspend fun delete(meal: MealEntity)
}