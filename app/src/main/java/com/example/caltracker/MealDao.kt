package com.example.caltracker

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {
    @Insert
    suspend fun insertMeal(meal: MealEntity)

    @Delete
    suspend fun deleteMeal(meal: MealEntity)

    @Query("SELECT * FROM meals WHERE date = :date")
    fun getMealsForDate(date: String): Flow<List<MealEntity>>

    @Query("SELECT * FROM meals")
    fun getAllMeals(): Flow<List<MealEntity>>

    @Query("DELETE FROM meals WHERE date < :cutoffDate")
    suspend fun deleteMealsOlderThan(cutoffDate: String)
}