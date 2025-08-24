package com.example.caltracker

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "caltracker_database"
        private const val DATABASE_VERSION = 1

        // Meal table
        private const val TABLE_MEALS = "meal"
        private const val MEAL_ID = "id"
        private const val MEAL_DATE = "date"
        private const val MEAL_TIME = "time"
        private const val MEAL_TYPE = "meal_type"
        private const val MEAL_DESCRIPTION = "description"
        private const val MEAL_CALORIES = "calories"
        private const val MEAL_PROTEIN = "protein"

        // Food table
        private const val TABLE_FOODS = "food"
        private const val FOOD_ID = "id"
        private const val FOOD_NAME = "name"
        private const val FOOD_CALORIES_PER_100G = "calories_per_100g"

        // Daily total table
        private const val TABLE_DAILY_TOTALS = "daily_total"
        private const val DAILY_TOTAL_ID = "id"
        private const val DAILY_TOTAL_DATE = "date"
        private const val DAILY_TOTAL_CALORIES = "total_calories"
        private const val DAILY_TOTAL_PROTEIN = "total_protein"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createMealsTable = """
            CREATE TABLE $TABLE_MEALS (
                $MEAL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $MEAL_DATE TEXT NOT NULL,
                $MEAL_TIME TEXT NOT NULL,
                $MEAL_TYPE TEXT NOT NULL,
                $MEAL_DESCRIPTION TEXT NOT NULL,
                $MEAL_CALORIES INTEGER NOT NULL,
                $MEAL_PROTEIN INTEGER NOT NULL
            )
        """.trimIndent()
        db.execSQL(createMealsTable)

        val createFoodsTable = """
            CREATE TABLE $TABLE_FOODS (
                $FOOD_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $FOOD_NAME TEXT NOT NULL UNIQUE,
                $FOOD_CALORIES_PER_100G INTEGER NOT NULL
            )
        """.trimIndent()
        db.execSQL(createFoodsTable)

        val createDailyTotalsTable = """
            CREATE TABLE $TABLE_DAILY_TOTALS (
                $DAILY_TOTAL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $DAILY_TOTAL_DATE TEXT NOT NULL UNIQUE,
                $DAILY_TOTAL_CALORIES INTEGER NOT NULL,
                $DAILY_TOTAL_PROTEIN INTEGER NOT NULL
            )
        """.trimIndent()
        db.execSQL(createDailyTotalsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Drop tables and recreate them (for simplicity, can be refined later)
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MEALS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_FOODS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DAILY_TOTALS")
        onCreate(db)
    }

    // Meal operations
    fun insertMeal(meal: MealEntity) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(MEAL_DATE, meal.date)
            put(MEAL_TIME, meal.time)
            put(MEAL_TYPE, meal.mealType)
            put(MEAL_DESCRIPTION, meal.description)
            put(MEAL_CALORIES, meal.calories)
            put(MEAL_PROTEIN, meal.protein)
        }
        db.insert(TABLE_MEALS, null, values)
        db.close()
    }

    fun deleteMeal(meal: MealEntity) {
        val db = writableDatabase
        db.delete(TABLE_MEALS, "$MEAL_ID = ?", arrayOf(meal.id.toString()))
        db.close()
    }

    fun getMealsByDate(date: String): Flow<List<MealEntity>> = flow {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_MEALS, null, "$MEAL_DATE = ?", arrayOf(date),
            null, null, "$MEAL_ID DESC"
        )
        val meals = mutableListOf<MealEntity>()
        cursor.use {
            while (it.moveToNext()) {
                meals.add(
                    MealEntity(
                        id = it.getInt(it.getColumnIndexOrThrow(MEAL_ID)),
                        date = it.getString(it.getColumnIndexOrThrow(MEAL_DATE)),
                        time = it.getString(it.getColumnIndexOrThrow(MEAL_TIME)),
                        mealType = it.getString(it.getColumnIndexOrThrow(MEAL_TYPE)),
                        description = it.getString(it.getColumnIndexOrThrow(MEAL_DESCRIPTION)),
                        calories = it.getInt(it.getColumnIndexOrThrow(MEAL_CALORIES)),
                        protein = it.getInt(it.getColumnIndexOrThrow(MEAL_PROTEIN))
                    )
                )
            }
        }
        db.close()
        emit(meals)
    }

    // Food operations
    fun insertFood(food: FoodEntity) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(FOOD_NAME, food.name)
            put(FOOD_CALORIES_PER_100G, food.caloriesPer100g)
        }
        db.insert(TABLE_FOODS, null, values)
        db.close()
    }

    fun deleteFood(food: FoodEntity) {
        val db = writableDatabase
        db.delete(TABLE_FOODS, "$FOOD_ID = ?", arrayOf(food.id.toString()))
        db.close()
    }

    fun getAllFoods(): Flow<List<FoodEntity>> = flow {
        val db = readableDatabase
        val cursor = db.query(TABLE_FOODS, null, null, null, null, null, null)
        val foods = mutableListOf<FoodEntity>()
        cursor.use {
            while (it.moveToNext()) {
                foods.add(
                    FoodEntity(
                        id = it.getInt(it.getColumnIndexOrThrow(FOOD_ID)),
                        name = it.getString(it.getColumnIndexOrThrow(FOOD_NAME)),
                        caloriesPer100g = it.getInt(it.getColumnIndexOrThrow(FOOD_CALORIES_PER_100G))
                    )
                )
            }
        }
        db.close()
        emit(foods)
    }

    fun getFoodByName(name: String): FoodEntity? {
        val db = readableDatabase
        val cursor = db.query(TABLE_FOODS, null, "$FOOD_NAME = ?", arrayOf(name), null, null, null)
        var food: FoodEntity? = null
        cursor.use {
            if (it.moveToFirst()) {
                food = FoodEntity(
                    id = it.getInt(it.getColumnIndexOrThrow(FOOD_ID)),
                    name = it.getString(it.getColumnIndexOrThrow(FOOD_NAME)),
                    caloriesPer100g = it.getInt(it.getColumnIndexOrThrow(FOOD_CALORIES_PER_100G))
                )
            }
        }
        db.close()
        return food
    }

    fun deleteFoodByName(name: String) {
        val db = writableDatabase
        db.delete(TABLE_FOODS, "$FOOD_NAME = ?", arrayOf(name))
        db.close()
    }

    // Daily total operations
    fun insertDailyTotal(dailyTotal: DailyTotalEntity) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(DAILY_TOTAL_DATE, dailyTotal.date)
            put(DAILY_TOTAL_CALORIES, dailyTotal.totalCalories)
            put(DAILY_TOTAL_PROTEIN, dailyTotal.totalProtein)
        }
        db.insert(TABLE_DAILY_TOTALS, null, values)
        db.close()
    }

    fun updateDailyTotal(dailyTotal: DailyTotalEntity) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(DAILY_TOTAL_DATE, dailyTotal.date)
            put(DAILY_TOTAL_CALORIES, dailyTotal.totalCalories)
            put(DAILY_TOTAL_PROTEIN, dailyTotal.totalProtein)
        }
        db.update(TABLE_DAILY_TOTALS, values, "$DAILY_TOTAL_ID = ?", arrayOf(dailyTotal.id.toString()))
        db.close()
    }

    fun deleteDailyTotal(dailyTotal: DailyTotalEntity) {
        val db = writableDatabase
        db.delete(TABLE_DAILY_TOTALS, "$DAILY_TOTAL_ID = ?", arrayOf(dailyTotal.id.toString()))
        db.close()
    }

    fun getAllDailyTotals(): Flow<List<DailyTotalEntity>> = flow {
        val db = readableDatabase
        val cursor = db.query(TABLE_DAILY_TOTALS, null, null, null, null, null, null)
        val totals = mutableListOf<DailyTotalEntity>()
        cursor.use {
            while (it.moveToNext()) {
                totals.add(
                    DailyTotalEntity(
                        id = it.getInt(it.getColumnIndexOrThrow(DAILY_TOTAL_ID)),
                        date = it.getString(it.getColumnIndexOrThrow(DAILY_TOTAL_DATE)),
                        totalCalories = it.getInt(it.getColumnIndexOrThrow(DAILY_TOTAL_CALORIES)),
                        totalProtein = it.getInt(it.getColumnIndexOrThrow(DAILY_TOTAL_PROTEIN))
                    )
                )
            }
        }
        db.close()
        emit(totals)
    }

    fun getDailyTotalByDate(date: String): DailyTotalEntity? {
        val db = readableDatabase
        val cursor = db.query(TABLE_DAILY_TOTALS, null, "$DAILY_TOTAL_DATE = ?", arrayOf(date), null, null, null)
        var dailyTotal: DailyTotalEntity? = null
        cursor.use {
            if (it.moveToFirst()) {
                dailyTotal = DailyTotalEntity(
                    id = it.getInt(it.getColumnIndexOrThrow(DAILY_TOTAL_ID)),
                    date = it.getString(it.getColumnIndexOrThrow(DAILY_TOTAL_DATE)),
                    totalCalories = it.getInt(it.getColumnIndexOrThrow(DAILY_TOTAL_CALORIES)),
                    totalProtein = it.getInt(it.getColumnIndexOrThrow(DAILY_TOTAL_PROTEIN))
                )
            }
        }
        db.close()
        return dailyTotal
    }
}