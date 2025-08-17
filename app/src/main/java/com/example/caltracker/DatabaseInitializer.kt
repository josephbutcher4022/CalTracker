package com.example.caltracker

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DatabaseInitializer(private val context: Context) {

    private val repository = MealRepository(context.applicationContext as android.app.Application)
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("CalTrackerPrefs", Context.MODE_PRIVATE)

    fun initialize() {
        if (!sharedPreferences.getBoolean("example_data_inserted", false)) {
            kotlinx.coroutines.GlobalScope.launch(Dispatchers.IO) {
                insertExampleData()
                sharedPreferences.edit().putBoolean("example_data_inserted", true).apply()
                println("DatabaseInitializer: Inserted example data")
            }
        }
    }

    private suspend fun insertExampleData() {
        withContext(Dispatchers.IO) {
            // Example data for three days
            val days = listOf(
                "2025-08-15",
                "2025-08-16",
                "2025-08-17"
            )

            days.forEach { date ->
                // Insert three meals per day
                val meals = listOf(
                    MealEntity(
                        date = date,
                        time = "8:00 AM",
                        mealType = "Breakfast",
                        description = "Oatmeal with fruit",
                        calories = 200,
                        protein = 10
                    ),
                    MealEntity(
                        date = date,
                        time = "12:30 PM",
                        mealType = "Lunch",
                        description = "Chicken salad",
                        calories = 350,
                        protein = 25
                    ),
                    MealEntity(
                        date = date,
                        time = "6:00 PM",
                        mealType = "Dinner",
                        description = "Grilled salmon",
                        calories = 450,
                        protein = 30
                    )
                )

                meals.forEach { meal ->
                    repository.insertMeal(meal)
                }

                // Calculate and insert daily total
                val totalCalories = meals.sumOf { it.calories }
                val totalProtein = meals.sumOf { it.protein }
                repository.insertDailyTotal(
                    DailyTotalEntity(
                        date = date,
                        totalCalories = totalCalories,
                        totalProtein = totalProtein
                    )
                )
            }
        }
    }
}