package com.example.caltracker

import android.app.Application
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class DailyTotalWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val repository = MealRepository(applicationContext as Application)
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val today = dateFormat.format(Date())
                val meals = repository.getMealsForToday().firstOrNull() ?: emptyList()
                val totalCalories = meals.sumOf { it.calories }
                val totalProtein = meals.sumOf { it.protein }
                repository.insertDailyTotal(
                    DailyTotalEntity(
                        date = today,
                        totalCalories = totalCalories,
                        totalProtein = totalProtein
                    )
                )
                repository.deleteOldTotals()
                println("DailyTotalWorker: Ran for $today, Calories: $totalCalories, Protein: $totalProtein")
                Result.success()
            } catch (e: Exception) {
                println("DailyTotalWorker: Error - ${e.message}")
                Result.failure()
            }
        }
    }
}