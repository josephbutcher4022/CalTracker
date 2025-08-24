package com.example.caltracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import timber.log.Timber

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MealRepository(AppDatabase.getDatabase(application))

    fun insertMeal(meal: MealEntity) {
        viewModelScope.launch {
            try {
                repository.insertMealAndUpdateTotals(meal)
                Timber.d("MainViewModel: Meal inserted: $meal")
            } catch (e: Exception) {
                Timber.e(e, "MainViewModel: Failed to insert meal: $meal")
            }
        }
    }

    fun deleteMeal(meal: MealEntity) {
        viewModelScope.launch {
            try {
                repository.deleteMeal(meal)
                Timber.d("MainViewModel: Meal deleted: $meal")
            } catch (e: Exception) {
                Timber.e(e, "MainViewModel: Failed to delete meal: $meal")
            }
        }
    }
}