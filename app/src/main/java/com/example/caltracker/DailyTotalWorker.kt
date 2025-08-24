package com.example.caltracker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.first
import timber.log.Timber

class DailyTotalWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        Timber.d("DailyTotalWorker: Starting work")
        val repository = MealRepository(AppDatabase.getDatabase(applicationContext))
        val date = inputData.getString("date") ?: return Result.failure()
        val meals = repository.getMealsByDate(date).first()
        val totalCalories = meals.sumOf { it.calories.toLong() }.toInt()
        val totalProtein = meals.sumOf { it.protein.toLong() }.toInt()

        val existingDailyTotal = repository.getDailyTotalByDate(date)
        if (existingDailyTotal == null) {
            repository.insertDailyTotal(DailyTotalEntity(date = date, totalCalories = totalCalories, totalProtein = totalProtein))
        } else {
            repository.updateDailyTotal(DailyTotalEntity(id = existingDailyTotal.id, date = date, totalCalories = totalCalories, totalProtein = totalProtein))
        }
        Timber.d("DailyTotalWorker: Daily totals updated for $date: Calories=$totalCalories, Protein=$totalProtein")

        return Result.success()
    }
}