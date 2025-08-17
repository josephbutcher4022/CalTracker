package com.example.caltracker

import android.app.Application
import androidx.room.Room
import kotlinx.coroutines.flow.Flow

class MealRepository(application: Application) {
    private val database: AppDatabase = Room.databaseBuilder(
        application,
        AppDatabase::class.java, "caltracker-database"
    ).build()
    private val foodDao = database.foodDao()
    private val mealDao = database.mealDao()
    private val dailyTotalDao = database.dailyTotalDao()

    fun getAllFoods(): Flow<List<FoodEntity>> = foodDao.getAllFoods()
    suspend fun getFoodByName(name: String): FoodEntity? = foodDao.getFoodByName(name)
    suspend fun insertFood(food: FoodEntity) = foodDao.insertFood(food)
    suspend fun deleteFood(food: FoodEntity) = foodDao.deleteFood(food)
    fun getMealsByDate(date: String): Flow<List<MealEntity>> = mealDao.getMealsByDate(date)
    suspend fun insertMeal(meal: MealEntity) = mealDao.insertMeal(meal)
    suspend fun deleteMeal(meal: MealEntity) = mealDao.deleteMeal(meal)
    fun getMealsForToday(): Flow<List<MealEntity>> {
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
        return mealDao.getMealsForToday(today)
    }
    fun getAllDailyTotals(): Flow<List<DailyTotalEntity>> = dailyTotalDao.getAllDailyTotals()
    suspend fun insertDailyTotal(dailyTotal: DailyTotalEntity) = dailyTotalDao.insertDailyTotal(dailyTotal)
    suspend fun deleteDailyTotal(dailyTotal: DailyTotalEntity) = dailyTotalDao.deleteDailyTotal(dailyTotal)
    suspend fun deleteOldTotals() {
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
        dailyTotalDao.deleteOldTotals(today)
    }
}