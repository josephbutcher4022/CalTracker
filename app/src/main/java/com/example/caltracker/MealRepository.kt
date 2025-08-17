package com.example.caltracker

import android.app.Application
import androidx.room.Room
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MealRepository(application: Application) {

    private val db: AppDatabase = AppDatabase.getDatabase(application)
    private val mealDao: MealDao = db.mealDao()
    private val dailyTotalDao: DailyTotalDao = db.dailyTotalDao()
    private val foodDao: FoodDao = db.foodDao()

    fun getMealsForToday(): Flow<List<MealEntity>> {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        println("MealRepository: Fetching meals for today: $today")
        return mealDao.getMealsForDate(today)
    }

    fun getAllMeals(): Flow<List<MealEntity>> {
        return mealDao.getAllMeals()
    }

    fun getAllDailyTotals(): Flow<List<DailyTotalEntity>> {
        return dailyTotalDao.getAllDailyTotals()
    }

    suspend fun insertMeal(meal: MealEntity) {
        mealDao.insertMeal(meal)
        println("MealRepository: Meal inserted successfully: $meal")
    }

    suspend fun deleteMeal(meal: MealEntity) {
        mealDao.deleteMeal(meal)
        println("MealRepository: Meal deleted successfully: $meal")
    }

    suspend fun deleteDailyTotal(dailyTotal: DailyTotalEntity) {
        dailyTotalDao.deleteDailyTotal(dailyTotal)
        println("MealRepository: Daily total deleted successfully: $dailyTotal")
    }

    suspend fun deleteOldMeals() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -30)
        val cutoffDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
        mealDao.deleteMealsOlderThan(cutoffDate)
        println("MealRepository: Deleted meals older than $cutoffDate")
    }

    suspend fun insertDailyTotal(total: DailyTotalEntity) {
        dailyTotalDao.insertDailyTotal(total)
        println("MealRepository: Daily total inserted successfully: $total")
    }

    suspend fun deleteOldTotals() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -30)
        val cutoffDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
        dailyTotalDao.deleteTotalsOlderThan(cutoffDate)
        println("MealRepository: Deleted daily totals older than $cutoffDate")
    }

    suspend fun insertFood(food: FoodEntity) {
        foodDao.insertFood(food)
        println("MealRepository: Food inserted successfully: $food")
    }

    suspend fun getFoodByName(name: String): FoodEntity? {
        return foodDao.getFoodByName(name)
    }

    fun getAllFoods(): Flow<List<FoodEntity>> {
        return foodDao.getAllFoods()
    }

    suspend fun deleteFood(food: FoodEntity) {
        foodDao.deleteFood(food)
        println("MealRepository: Food deleted successfully: $food")
    }
}