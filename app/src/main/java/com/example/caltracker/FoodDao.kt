package com.example.caltracker

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {

    @Query("SELECT * FROM food")
    fun getAllFoods(): Flow<List<FoodEntity>>

    @Insert
    suspend fun insert(food: FoodEntity)

    @Delete
    suspend fun delete(food: FoodEntity)

    @Query("SELECT * FROM food WHERE name = :name")
    suspend fun getFoodByName(name: String): FoodEntity?

    @Query("DELETE FROM food WHERE name = :name")
    suspend fun deleteFoodByName(name: String)
}