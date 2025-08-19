package com.example.caltracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MealRepository = MealRepository(application)
    val todayMeals: Flow<List<MealEntity>> = repository.getMealsForToday()
    val dailyTotals: Flow<List<DailyTotalEntity>> = repository.getAllDailyTotals()

    fun saveMeal(date: String, time: String, mealType: String, description: String, calories: Int, protein: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            println("MainViewModel: Saving meal: $mealType, Description: $description, Calories: $calories, Protein: $protein, Date: $date, Time: $time")
            repository.insertMeal(
                MealEntity(
                    date = date,
                    time = time,
                    mealType = mealType,
                    description = description,
                    calories = calories,
                    protein = protein
                )
            )
            // Update daily total
            val meals = repository.getMealsByDate(date).firstOrNull() ?: emptyList()
            val totalCalories = meals.sumOf { it.calories }
            val totalProtein = meals.sumOf { it.protein }
            // Delete existing daily total for the date
            repository.getAllDailyTotals().firstOrNull()?.forEach { dailyTotal ->
                if (dailyTotal.date == date) {
                    repository.deleteDailyTotal(dailyTotal)
                }
            }
            // Insert new daily total if there are meals
            if (meals.isNotEmpty()) {
                repository.insertDailyTotal(
                    DailyTotalEntity(
                        date = date,
                        totalCalories = totalCalories,
                        totalProtein = totalProtein
                    )
                )
            }
            println("MainViewModel: Updated daily total for $date, Calories: $totalCalories, Protein: $totalProtein")
        }
    }

    fun deleteMeal(meal: MealEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteMeal(meal)
            // Update daily total
            val date = meal.date
            val meals = repository.getMealsByDate(date).firstOrNull() ?: emptyList()
            val totalCalories = meals.sumOf { it.calories }
            val totalProtein = meals.sumOf { it.protein }
            // Delete existing daily total for the date
            repository.getAllDailyTotals().firstOrNull()?.forEach { dailyTotal ->
                if (dailyTotal.date == date) {
                    repository.deleteDailyTotal(dailyTotal)
                }
            }
            // Insert new daily total if there are meals
            if (meals.isNotEmpty()) {
                repository.insertDailyTotal(
                    DailyTotalEntity(
                        date = date,
                        totalCalories = totalCalories,
                        totalProtein = totalProtein
                    )
                )
            }
            println("MainViewModel: Updated daily total after deletion for $date, Calories: $totalCalories, Protein: $totalProtein")
        }
    }

    fun deleteDailyTotal(dailyTotal: DailyTotalEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteDailyTotal(dailyTotal)
        }
    }
}