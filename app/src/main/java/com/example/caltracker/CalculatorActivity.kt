package com.example.caltracker

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CalculatorActivity : AppCompatActivity() {

    private lateinit var repository: MealRepository
    private lateinit var tvResultDynamic: TextView
    private var lastClickTime = 0L

    private fun showResultDynamic(message: String) {
        tvResultDynamic.animate().cancel()
        Handler(Looper.getMainLooper()).post {
            if (tvResultDynamic.alpha > 0) {
                tvResultDynamic.animate()
                    .alpha(0f)
                    .setDuration(1000)
                    .withEndAction {
                        tvResultDynamic.text = message
                        tvResultDynamic.visibility = View.VISIBLE
                        tvResultDynamic.alpha = 0f
                        tvResultDynamic.animate()
                            .alpha(1f)
                            .setDuration(1000)
                            .start()
                    }
                    .start()
            } else {
                tvResultDynamic.text = message
                tvResultDynamic.visibility = View.VISIBLE
                tvResultDynamic.alpha = 0f
                tvResultDynamic.animate()
                    .alpha(1f)
                    .setDuration(1000)
                    .start()
            }
            tvResultDynamic.invalidate()
            tvResultDynamic.requestLayout()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)
        println("CalculatorActivity: Started")

        repository = MealRepository(application)
        val btnBack: Button = findViewById(R.id.btn_back)
        val btnDelete: Button = findViewById(R.id.btn_delete)
        val etFoodName: EditText = findViewById(R.id.et_food_name)
        val etCalPer100g: EditText = findViewById(R.id.et_cal_per_100g)
        val btnAddFood: Button = findViewById(R.id.btn_add_food)
        val etGrams: EditText = findViewById(R.id.et_grams)
        val spinnerFoodName: Spinner = findViewById(R.id.spinner_food_name)
        val btnCalculate: Button = findViewById(R.id.btn_calculate)
        tvResultDynamic = findViewById(R.id.tv_result_dynamic)

        lifecycleScope.launchWhenStarted {
            repository.getAllFoods().collectLatest { foods ->
                val foodNames = foods.map { it.name }.distinct()
                runOnUiThread {
                    val adapter = ArrayAdapter(this@CalculatorActivity, android.R.layout.simple_spinner_item, foodNames)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerFoodName.adapter = adapter
                    btnDelete.isEnabled = foodNames.isNotEmpty()
                }
                println("CalculatorActivity: Updated spinner with ${foodNames.size} foods")
            }
        }

        btnBack.setOnClickListener {
            println("CalculatorActivity: Back button clicked")
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        btnDelete.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime < 1000) return@setOnClickListener
            lastClickTime = currentTime
            val name = spinnerFoodName.selectedItem?.toString()
            if (!name.isNullOrEmpty()) {
                lifecycleScope.launch(Dispatchers.IO) {
                    val food = repository.getFoodByName(name)
                    if (food != null) {
                        repository.deleteFood(food)
                        println("CalculatorActivity: Deleted food $name")
                    } else {
                        println("CalculatorActivity: Food $name not found for deletion")
                    }
                }
            } else {
                println("CalculatorActivity: No food selected for deletion")
            }
        }

        btnAddFood.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime < 1000) return@setOnClickListener
            lastClickTime = currentTime
            val name = etFoodName.text.toString().trim()
            val calPer100g = etCalPer100g.text.toString().toIntOrNull() ?: 0
            if (name.isNotEmpty() && calPer100g > 0) {
                lifecycleScope.launch(Dispatchers.IO) {
                    repository.insertFood(FoodEntity(name, calPer100g))
                    runOnUiThread {
                        etFoodName.text.clear()
                        etCalPer100g.text.clear()
                    }
                    println("CalculatorActivity: Added food $name with $calPer100g cal/100g")
                }
            } else {
                println("CalculatorActivity: Invalid food input")
            }
        }

        btnCalculate.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime < 1000) return@setOnClickListener
            lastClickTime = currentTime
            val name = spinnerFoodName.selectedItem?.toString()
            val grams = etGrams.text.toString().toIntOrNull() ?: 0
            if (!name.isNullOrEmpty() && grams > 0) {
                lifecycleScope.launch(Dispatchers.IO) {
                    val food = repository.getFoodByName(name)
                    if (food != null) {
                        val totalCal = (grams * food.caloriesPer100g) / 100
                        runOnUiThread { showResultDynamic("$totalCal for $name") }
                        println("CalculatorActivity: Calculated $totalCal cal for $grams g of $name")
                    } else {
                        runOnUiThread { showResultDynamic("Food not found") }
                        println("CalculatorActivity: Food $name not found")
                    }
                }
            } else {
                showResultDynamic("Select a food and enter valid grams")
                println("CalculatorActivity: Invalid calculation input")
            }
        }
    }
}