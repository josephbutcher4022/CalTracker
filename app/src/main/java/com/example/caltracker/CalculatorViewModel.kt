package com.example.caltracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class CalculatorViewModel(private val repository: MealRepository) : ViewModel() {

    fun insertFood(food: FoodEntity) {
        viewModelScope.launch {
            repository.insertFood(food)
        }
    }

    fun deleteFood(food: FoodEntity) {
        viewModelScope.launch {
            repository.deleteFood(food)
        }
    }
}