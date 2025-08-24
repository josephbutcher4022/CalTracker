package com.example.caltracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.caltracker.databinding.ActivityCalculatorBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

class CalculatorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCalculatorBinding
    private val repository = MealRepository(AppDatabase.getDatabase(this))
    private val adapter = FoodAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalculatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvFoods.layoutManager = LinearLayoutManager(this)
        binding.rvFoods.adapter = adapter

        lifecycleScope.launch {
            repository.getAllFoods().collectLatest { foods ->
                Timber.d("CalculatorActivity: Fetched foods: $foods")
                adapter.submitList(foods)
            }
        }

        binding.btnAddFood.setOnClickListener {
            val name = binding.etFoodName.text.toString()
            val caloriesPer100g = binding.etCaloriesPer100g.text.toString().toIntOrNull() ?: 0
            if (name.isNotBlank() && caloriesPer100g > 0) {
                val food = FoodEntity(name = name, caloriesPer100g = caloriesPer100g)
                lifecycleScope.launch {
                    repository.insertFood(food)
                    Timber.d("CalculatorActivity: Food added: $food")
                }
                binding.etFoodName.text.clear()
                binding.etCaloriesPer100g.text.clear()
            }
        }

        binding.btnCalculate.setOnClickListener {
            val grams = binding.etGrams.text.toString().toIntOrNull() ?: 0
            val selectedFood = adapter.selectedFood
            if (selectedFood != null && grams > 0) {
                val calories = (selectedFood.caloriesPer100g * grams) / 100
                binding.tvResult.text = "Calories: $calories"
                Timber.d("CalculatorActivity: Calculated calories: $calories for food: $selectedFood, grams: $grams")
            }
        }

        binding.btnDeleteFood.setOnClickListener {
            adapter.selectedFood?.let { food ->
                lifecycleScope.launch {
                    repository.deleteFood(food)
                    Timber.d("CalculatorActivity: Food deleted: $food")
                }
            }
        }

        binding.btnBack.setOnClickListener {
            Timber.d("CalculatorActivity: Back button clicked")
            finish()
        }
    }
}