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
                // Only insert if there are meals to avoid empty entries
                if (meals.isNotEmpty()) {
                    // Check if a total already exists for today to avoid duplicates
                    val existingTotal = repository.getAllDailyTotals().firstOrNull()?.find { it.date == today }
                    if (existingTotal == null) {
                        repository.insertDailyTotal(
                            DailyTotalEntity(
                                date = today,
                                totalCalories = totalCalories,
                                totalProtein = totalProtein
                            )
                        )
                        println("DailyTotalWorker: Saved total for $today, Calories: $totalCalories, Protein: $totalProtein")
                    } else {
                        println("DailyTotalWorker: Total for $today already exists, skipping")
                    }
                } else {
                    println("DailyTotalWorker: No meals for $today, no total saved")
                }
                Result.success()
            } catch (e: Exception) {
                println("DailyTotalWorker: Error - ${e.message}")
                Result.failure()
            }
        }
    }
}