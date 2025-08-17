package com.example.caltracker

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "foods")
data class FoodEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val caloriesPer100g: Int
)

@Entity(tableName = "meals")
data class MealEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val time: String,
    val mealType: String,
    val description: String,
    val calories: Int,
    val protein: Int
)

@Entity(tableName = "daily_totals")
data class DailyTotalEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val totalCalories: Int,
    val totalProtein: Int
)

@Dao
interface FoodDao {
    @Query("SELECT * FROM foods")
    fun getAllFoods(): Flow<List<FoodEntity>>

    @Query("SELECT * FROM foods WHERE name = :name LIMIT 1")
    suspend fun getFoodByName(name: String): FoodEntity?

    @Insert
    suspend fun insertFood(food: FoodEntity)

    @Delete
    suspend fun deleteFood(food: FoodEntity)
}

@Dao
interface MealDao {
    @Query("SELECT * FROM meals WHERE date = :date ORDER BY id ASC")
    fun getMealsByDate(date: String): Flow<List<MealEntity>>

    @Insert
    suspend fun insertMeal(meal: MealEntity)

    @Delete
    suspend fun deleteMeal(meal: MealEntity)

    @Query("SELECT * FROM meals WHERE date = :date ORDER BY id ASC")
    fun getMealsForToday(date: String): Flow<List<MealEntity>>
}

@Dao
interface DailyTotalDao {
    @Query("SELECT * FROM daily_totals")
    fun getAllDailyTotals(): Flow<List<DailyTotalEntity>>

    @Insert
    suspend fun insertDailyTotal(dailyTotal: DailyTotalEntity)

    @Delete
    suspend fun deleteDailyTotal(dailyTotal: DailyTotalEntity)

    @Query("DELETE FROM daily_totals WHERE date < :date")
    suspend fun deleteOldTotals(date: String)
}

@Database(entities = [FoodEntity::class, MealEntity::class, DailyTotalEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun foodDao(): FoodDao
    abstract fun mealDao(): MealDao
    abstract fun dailyTotalDao(): DailyTotalDao
}