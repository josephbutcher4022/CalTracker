package com.example.caltracker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meals")
data class MealEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val time: String,
    val mealType: String,
    val description: String = "",
    val calories: Int,
    val protein: Int
)