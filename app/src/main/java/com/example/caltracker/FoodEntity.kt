package com.example.caltracker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "foods")
data class FoodEntity(
    @PrimaryKey val name: String,  // Food name like "Potato" is the unique key
    val caloriesPer100g: Int       // Calories per 100g, e.g., 77
)