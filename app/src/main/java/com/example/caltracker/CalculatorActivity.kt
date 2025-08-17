package com.example.caltracker

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CalculatorActivity : AppCompatActivity() {

    private lateinit var repository: MealRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)
        println("CalculatorActivity: Started")

        // Initialize repository
        repository = MealRepository(application)

        // UI elements
        val btnBack: Button = findViewById(R.id.btn_back)
        val btnDelete: Button = findViewById(R.id.btn_delete)
        val etFoodName: EditText = findViewById(R.id.et_food_name)
        val etCalPer100g: EditText = findViewById(R.id.et_cal_per_100g)
        val btnAddFood: Button = findViewById(R.id.btn_add_food)
        val etGrams: EditText = findViewById(R.id.et_grams)
        val spinnerFoodName: Spinner = findViewById(R.id.spinner_food_name)
        val btnCalculate: Button = findViewById(R.id.btn_calculate)
        val tvResult: TextView = findViewById(R.id.tv_result)
        val tvMessage: TextView = findViewById(R.id.tv_message)

        // Populate Spinner with food names and enable Delete button when a food is selected
        lifecycleScope.launchWhenStarted {
            repository.getAllFoods().collectLatest { foods ->
                val foodNames = foods.map { it.name }
                val adapter = ArrayAdapter(this@CalculatorActivity, android.R.layout.simple_spinner_item, foodNames)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerFoodName.adapter = adapter
                btnDelete.isEnabled = foodNames.isNotEmpty() // Enable Delete if foods exist
                println("CalculatorActivity: Updated spinner with ${foodNames.size} foods")
            }
        }

        // Back button
        btnBack.setOnClickListener {
            println("CalculatorActivity: Back button clicked")
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // Delete button
        btnDelete.setOnClickListener {
            val name = spinnerFoodName.selectedItem?.toString()
            if (!name.isNullOrEmpty()) {
                lifecycleScope.launch {
                    val food = repository.getFoodByName(name)
                    if (food != null) {
                        repository.deleteFood(food)
                        tvMessage.text = "Deleted $name"
                        tvMessage.alpha = 0f // Ensure initial state is invisible
                        tvMessage.animate().alpha(1f).setDuration(500).withEndAction {
                            tvMessage.animate().alpha(0f).setDuration(500).setStartDelay(5000).start()
                        }.start()
                        println("CalculatorActivity: Deleted food $name")
                    } else {
                        tvMessage.text = "Food not found"
                        tvMessage.alpha = 0f // Ensure initial state is invisible
                        tvMessage.animate().alpha(1f).setDuration(500).withEndAction {
                            tvMessage.animate().alpha(0f).setDuration(500).setStartDelay(5000).start()
                        }.start()
                        println("CalculatorActivity: Food $name not found for deletion")
                    }
                }
            } else {
                tvMessage.text = "Select a food to delete"
                tvMessage.alpha = 0f // Ensure initial state is invisible
                tvMessage.animate().alpha(1f).setDuration(500).withEndAction {
                    tvMessage.animate().alpha(0f).setDuration(500).setStartDelay(5000).start()
                }.start()
                println("CalculatorActivity: No food selected for deletion")
            }
        }

        // Add Food button
        btnAddFood.setOnClickListener {
            val name = etFoodName.text.toString().trim()
            val calPer100g = etCalPer100g.text.toString().toIntOrNull() ?: 0
            if (name.isNotEmpty() && calPer100g > 0) {
                lifecycleScope.launch {
                    repository.insertFood(FoodEntity(name, calPer100g))
                    println("CalculatorActivity: Added food $name with $calPer100g cal/100g")
                    etFoodName.text.clear()
                    etCalPer100g.text.clear()
                    tvMessage.text = "Added $name"
                    tvMessage.alpha = 0f // Ensure initial state is invisible
                    tvMessage.animate().alpha(1f).setDuration(500).withEndAction {
                        tvMessage.animate().alpha(0f).setDuration(500).setStartDelay(5000).start()
                    }.start()
                    // Select the newly added food in the Spinner
                    repository.getAllFoods().collectLatest { foods ->
                        val foodNames = foods.map { it.name }
                        val position = foodNames.indexOf(name)
                        if (position >= 0) {
                            spinnerFoodName.setSelection(position)
                        }
                    }
                }
            } else {
                tvMessage.text = "Enter valid food name and calories"
                tvMessage.alpha = 0f // Ensure initial state is invisible
                tvMessage.animate().alpha(1f).setDuration(500).withEndAction {
                    tvMessage.animate().alpha(0f).setDuration(500).setStartDelay(5000).start()
                }.start()
                println("CalculatorActivity: Invalid food input")
            }
        }

        // Calculate button
        btnCalculate.setOnClickListener {
            val name = spinnerFoodName.selectedItem?.toString()
            val grams = etGrams.text.toString().toIntOrNull() ?: 0
            if (!name.isNullOrEmpty() && grams > 0) {
                lifecycleScope.launch {
                    val food = repository.getFoodByName(name)
                    if (food != null) {
                        val totalCal = (grams * food.caloriesPer100g) / 100
                        tvResult.text = "Total calories: $totalCal"
                        println("CalculatorActivity: Calculated $totalCal cal for $grams g of $name")
                    } else {
                        tvResult.text = "Food not found"
                        println("CalculatorActivity: Food $name not found")
                    }
                }
            } else {
                tvResult.text = "Select a food and enter valid grams"
                println("CalculatorActivity: Invalid calculation input")
            }
        }
    }
}