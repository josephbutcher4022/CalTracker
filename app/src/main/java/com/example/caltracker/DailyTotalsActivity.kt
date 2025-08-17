package com.example.caltracker

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DailyTotalsActivity : AppCompatActivity() {

    private lateinit var repository: MealRepository
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DailyTotalsAdapter
    private var selectedDailyTotal: DailyTotalEntity? = null
    private lateinit var btnView: Button
    private lateinit var btnDelete: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_totals)
        println("DailyTotalsActivity: Started")

        repository = MealRepository(application)
        recyclerView = findViewById(R.id.rv_daily_totals)
        btnView = findViewById(R.id.btn_view)
        btnDelete = findViewById(R.id.btn_delete)
        val btnBack: Button = findViewById(R.id.btn_back)
        sharedPreferences = getSharedPreferences("CalTrackerPrefs", MODE_PRIVATE)

        // Initialize RecyclerView for daily totals
        adapter = DailyTotalsAdapter(emptyList()) { dailyTotal ->
            selectedDailyTotal = dailyTotal
            btnView.isEnabled = dailyTotal != null
            btnDelete.isEnabled = dailyTotal != null
            println("DailyTotalsActivity: Selected daily total: ${dailyTotal?.date ?: "none"}")
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Load all daily totals and current day's meals
        lifecycleScope.launch {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val today = dateFormat.format(Date())
            repository.getAllDailyTotals().collectLatest { totals ->
                // Calculate current day's total from meals
                repository.getMealsByDate(today).collectLatest { meals ->
                    val todayTotalCalories = meals.sumOf { it.calories }
                    val todayTotalProtein = meals.sumOf { it.protein }
                    val todayTotal = DailyTotalEntity(
                        id = 0, // Temporary ID, not saved yet
                        date = today,
                        totalCalories = todayTotalCalories,
                        totalProtein = todayTotalProtein
                    )
                    // Combine today's total with saved totals
                    val updatedTotals = if (totals.any { it.date == today }) {
                        totals // If today is already saved, use existing totals
                    } else {
                        listOf(todayTotal) + totals.filter { it.date != today }
                    }
                    adapter.updateTotals(updatedTotals.sortedByDescending { it.date })
                    println("DailyTotalsActivity: Loaded ${updatedTotals.size} daily totals, including today: $todayTotalCalories cal, $todayTotalProtein g protein")
                }
            }
        }

        // View button: Show popup with meals for selected day
        btnView.setOnClickListener {
            selectedDailyTotal?.let { total ->
                lifecycleScope.launch(Dispatchers.IO) {
                    repository.getMealsByDate(total.date).collectLatest { meals ->
                        runOnUiThread {
                            showMealsPopup(meals)
                        }
                    }
                }
            }
        }

        // Delete button
        btnDelete.setOnClickListener {
            selectedDailyTotal?.let { total ->
                lifecycleScope.launch(Dispatchers.IO) {
                    // Delete all meals for the selected date
                    val meals = repository.getMealsByDate(total.date).firstOrNull() ?: emptyList()
                    meals.forEach { meal ->
                        repository.deleteMeal(meal)
                    }
                    // Delete the daily total if it exists in the database
                    if (total.id != 0) { // Only delete if it's a saved entry
                        repository.deleteDailyTotal(total)
                    }
                    runOnUiThread {
                        selectedDailyTotal = null
                        adapter.clearSelection()
                        btnView.isEnabled = false
                        btnDelete.isEnabled = false
                        println("DailyTotalsActivity: Deleted daily total for ${total.date}")
                    }
                }
            }
            // Consume the click event to prevent propagation
            true
        }

        // Back button
        btnBack.setOnClickListener {
            println("DailyTotalsActivity: Back button clicked")
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // Clear selection when tapping anywhere in RecyclerView (except on items)
        recyclerView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val view = recyclerView.findChildViewUnder(event.x, event.y)
                if (view == null && selectedDailyTotal != null) {
                    selectedDailyTotal = null
                    adapter.clearSelection()
                    btnView.isEnabled = false
                    btnDelete.isEnabled = false
                    println("DailyTotalsActivity: Cleared selection on RecyclerView tap")
                }
            }
            false // Allow item clicks to proceed
        }
    }

    private fun showMealsPopup(meals: List<MealEntity>) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_meals_list, null)
        val rvMeals: RecyclerView = dialogView.findViewById(R.id.rv_meals)
        val mealAdapter = MealAdapter(meals) { /* No-op for popup, selection not needed */ }
        rvMeals.layoutManager = LinearLayoutManager(this)
        rvMeals.adapter = mealAdapter

        AlertDialog.Builder(this)
            .setTitle("Meals for ${selectedDailyTotal?.date}")
            .setView(dialogView)
            .setCancelable(true) // Allow closing by tapping outside
            .show()
    }
}