package com.example.caltracker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_total")
data class DailyTotalEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var date: String,
    var totalCalories: Int,
    var totalProtein: Int
)