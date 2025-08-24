package com.example.caltracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel(private val repository: MealRepository) : ViewModel() {

    fun insertMeal(meal: MealEntity) {
        viewModelScope.launch {
            repository.insertMeal(meal)
        }
    }

    fun deleteMeal(meal: MealEntity) {
        viewModelScope.launch {
            repository.deleteMeal(meal)
        }
    }

    fun insertDailyTotal(dailyTotal: DailyTotalEntity) {
        viewModelScope.launch {
            repository.insertDailyTotal(dailyTotal)
        }
    }

    fun deleteDailyTotal(dailyTotal: DailyTotalEntity) {
        viewModelScope.launch {
            repository.deleteDailyTotal(dailyTotal)
        }
    }
}