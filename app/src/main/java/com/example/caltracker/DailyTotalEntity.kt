package com.example.caltracker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_totals")
data class DailyTotalEntity(
    @PrimaryKey val date: String,
    val totalCalories: Int,
    val totalProtein: Int
)