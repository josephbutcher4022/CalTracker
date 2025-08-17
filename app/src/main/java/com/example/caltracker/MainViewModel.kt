package com.example.caltracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.Flow

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MealRepository = MealRepository(application)
    val todayMeals: Flow<List<MealEntity>> = repository.getMealsForToday()
    val dailyTotals: Flow<List<DailyTotalEntity>> = repository.getAllDailyTotals()

    fun saveMeal(date: String, time: String, mealType: String, description: String, calories: Int, protein: Int) {
        viewModelScope.launch {
            println("MainViewModel: Saving meal: $mealType, Description: $description, Calories: $calories, Protein: $protein, Date: $date, Time: $time")
            repository.insertMeal(MealEntity(
                date = date,
                time = time,
                mealType = mealType,
                description = description,
                calories = calories,
                protein = protein
            ))
        }
    }

    fun deleteMeal(meal: MealEntity) {
        viewModelScope.launch {
            repository.deleteMeal(meal)
        }
    }

    fun deleteDailyTotal(dailyTotal: DailyTotalEntity) {
        viewModelScope.launch {
            repository.deleteDailyTotal(dailyTotal)
        }
    }
}