package com.example.caltracker

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import androidx.room.withTransaction
import timber.log.Timber

class MealRepository(private val appDatabase: AppDatabase) {

    private val mealDao = appDatabase.mealDao()
    private val foodDao = appDatabase.foodDao()
    private val dailyTotalDao = appDatabase.dailyTotalDao()

    fun getAllFoods(): Flow<List<FoodEntity>> = foodDao.getAllFoods()

    suspend fun insertFood(food: FoodEntity) = withContext(Dispatchers.IO) {
        Timber.d("MealRepository: Inserting food: $food")
        appDatabase.withTransaction {
            foodDao.insert(food)
            Timber.d("MealRepository: Food inserted inside transaction: $food")
        }
        Timber.d("MealRepository: Food insert completed: $food")
    }

    suspend fun deleteFood(food: FoodEntity) = withContext(Dispatchers.IO) {
        Timber.d("MealRepository: Deleting food: $food")
        appDatabase.withTransaction {
            foodDao.delete(food)
            Timber.d("MealRepository: Food deleted inside transaction: $food")
        }
        Timber.d("MealRepository: Food delete completed: $food")
    }

    suspend fun getFoodByName(name: String): FoodEntity? = withContext(Dispatchers.IO) {
        val food = foodDao.getFoodByName(name)
        Timber.d("MealRepository: Fetched food by name: $name, result: $food")
        food
    }

    suspend fun deleteFoodByName(name: String) = withContext(Dispatchers.IO) {
        Timber.d("MealRepository: Deleting food by name: $name")
        appDatabase.withTransaction {
            foodDao.deleteFoodByName(name)
            Timber.d("MealRepository: Food deleted by name inside transaction: $name")
        }
        Timber.d("MealRepository: Food delete by name completed: $name")
    }

    fun getMealsByDate(date: String): Flow<List<MealEntity>> = mealDao.getMealsByDate(date)

    fun getMealsForToday(date: String): Flow<List<MealEntity>> = mealDao.getMealsForToday(date)

    suspend fun insertMeal(meal: MealEntity) = withContext(Dispatchers.IO) {
        Timber.d("MealRepository: Inserting meal: $meal")
        appDatabase.withTransaction {
            mealDao.insert(meal)
            Timber.d("MealRepository: Meal inserted inside transaction: $meal")
        }
        Timber.d("MealRepository: Meal insert completed: $meal")
    }

    suspend fun deleteMeal(meal: MealEntity) = withContext(Dispatchers.IO) {
        Timber.d("MealRepository: Deleting meal: $meal")
        appDatabase.withTransaction {
            mealDao.delete(meal)
            Timber.d("MealRepository: Meal deleted inside transaction: $meal")
        }
        Timber.d("MealRepository: Meal delete completed: $meal")
    }

    fun getAllDailyTotals(): Flow<List<DailyTotalEntity>> = dailyTotalDao.getAllDailyTotals()

    suspend fun insertDailyTotal(dailyTotal: DailyTotalEntity) = withContext(Dispatchers.IO) {
        Timber.d("MealRepository: Inserting daily total: $dailyTotal")
        appDatabase.withTransaction {
            dailyTotalDao.insert(dailyTotal)
            Timber.d("MealRepository: Daily total inserted inside transaction: $dailyTotal")
        }
        Timber.d("MealRepository: Daily total insert completed: $dailyTotal")
    }

    suspend fun updateDailyTotal(dailyTotal: DailyTotalEntity) = withContext(Dispatchers.IO) {
        Timber.d("MealRepository: Updating daily total: $dailyTotal")
        appDatabase.withTransaction {
            dailyTotalDao.update(dailyTotal)
            Timber.d("MealRepository: Daily total updated inside transaction: $dailyTotal")
        }
        Timber.d("MealRepository: Daily total update completed: $dailyTotal")
    }

    suspend fun deleteDailyTotal(dailyTotal: DailyTotalEntity) = withContext(Dispatchers.IO) {
        Timber.d("MealRepository: Deleting daily total: $dailyTotal")
        appDatabase.withTransaction {
            dailyTotalDao.delete(dailyTotal)
            Timber.d("MealRepository: Daily total deleted inside transaction: $dailyTotal")
        }
        Timber.d("MealRepository: Daily total delete completed: $dailyTotal")
    }

    suspend fun getDailyTotalByDate(date: String): DailyTotalEntity? = withContext(Dispatchers.IO) {
        val dailyTotal = dailyTotalDao.getDailyTotalByDate(date)
        Timber.d("MealRepository: Fetched daily total for date: $date, result: $dailyTotal")
        dailyTotal
    }
}