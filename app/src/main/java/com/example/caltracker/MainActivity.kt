package com.example.caltracker

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.collectLatest

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels { MainViewModelFactory(application) }
    private var selectedMeal: MealEntity? = null
    private lateinit var mealAdapter: MealAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        println("MainActivity: Started")

        // Schedule daily total aggregation at 11:59 PM
        val constraints = Constraints.Builder().build()
        val workRequest = PeriodicWorkRequestBuilder<DailyTotalWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(calculateInitialDelay(), TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "dailyTotalWork",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )

        val rootLayout: View = findViewById(R.id.root_layout)
        val spinnerMealType: Spinner = findViewById(R.id.spinner_meal_type)
        val etDescription: EditText = findViewById(R.id.et_description)
        val etCalories: EditText = findViewById(R.id.et_calories)
        val etProtein: EditText = findViewById(R.id.et_protein)
        val btnSave: Button = findViewById(R.id.btn_save)
        val btnDelete: Button = findViewById(R.id.btn_delete)
        val btnDailyTotals: Button = findViewById(R.id.btn_daily_totals)
        val btnCalculator: Button = findViewById(R.id.btn_calculator)
        val rvTodayMeals: RecyclerView = findViewById(R.id.rv_today_meals)
        val tvCurrentDate: TextView = findViewById(R.id.tv_current_date)

        // Set current date
        tvCurrentDate.text = SimpleDateFormat("M-d-yyyy", Locale.getDefault()).format(Date())

        println("MainActivity: Button bindings - btnSave=${btnSave.text}, btnDelete=${btnDelete.text}, btnDailyTotals=${btnDailyTotals.text}, btnCalculator=${btnCalculator.text}")

        ArrayAdapter.createFromResource(
            this,
            R.array.meal_types,
            R.layout.spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.spinner_item)
            spinnerMealType.adapter = adapter
        }

        rvTodayMeals.layoutManager = LinearLayoutManager(this).apply {
            reverseLayout = true
            stackFromEnd = true
        }
        mealAdapter = MealAdapter(emptyList()) { meal ->
            selectedMeal = meal
            btnDelete.isEnabled = meal != null
            println("MainActivity: Selected meal for deletion: ${meal?.description ?: "none"}")
        }
        rvTodayMeals.adapter = mealAdapter
        println("MainActivity: RecyclerView initialized with LinearLayoutManager")

        // Clear selection when tapping anywhere outside a meal item
        rootLayout.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                selectedMeal?.let {
                    selectedMeal = null
                    mealAdapter.clearSelection()
                    btnDelete.isEnabled = false
                    println("MainActivity: Cleared meal selection on background tap")
                }
            }
            false // Allow other touch events to proceed
        }

        // Clear selection when tapping empty space or same item in RecyclerView
        val gestureDetector = GestureDetector(this, object : SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                val view = rvTodayMeals.findChildViewUnder(e.x, e.y)
                if (view == null && selectedMeal != null) {
                    selectedMeal = null
                    mealAdapter.clearSelection()
                    btnDelete.isEnabled = false
                    println("MainActivity: Cleared meal selection on empty RecyclerView tap")
                }
                return false // Allow item clicks to proceed
            }
        })

        rvTodayMeals.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            false // Allow RecyclerView item clicks
        }

        lifecycleScope.launchWhenStarted {
            viewModel.todayMeals.collectLatest { meals ->
                println("MainActivity: Collected todayMeals update with ${meals.size} entries")
                mealAdapter.updateMeals(meals.sortedBy { it.id })
                println("MainActivity: Notified adapter for update")
            }
        }

        btnSave.setOnClickListener {
            println("MainActivity: Log Meal button clicked")
            val mealType = spinnerMealType.selectedItem.toString()
            val description = etDescription.text.toString().trim()
            val calories = etCalories.text.toString().toIntOrNull() ?: 0
            val protein = etProtein.text.toString().toIntOrNull() ?: 0
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
            val today = dateFormat.format(Date())
            val time = timeFormat.format(Date())
            println("MainActivity: Saving meal: $mealType, Description: $description, Calories: $calories, Protein: $protein, Date: $today, Time: $time")
            viewModel.saveMeal(today, time, mealType, description, calories, protein)
            etDescription.text.clear()
            etCalories.text.clear()
            etProtein.text.clear()
            spinnerMealType.setSelection(0)
        }

        btnDelete.setOnClickListener {
            selectedMeal?.let { meal ->
                println("MainActivity: Deleting meal: $meal")
                viewModel.deleteMeal(meal)
                selectedMeal = null
                mealAdapter.clearSelection()
                btnDelete.isEnabled = false
            }
        }

        btnDailyTotals.setOnClickListener {
            println("MainActivity: Navigating to DailyTotalsActivity")
            startActivity(Intent(this, DailyTotalsActivity::class.java))
        }

        btnCalculator.setOnClickListener {
            println("MainActivity: Navigating to CalculatorActivity")
            startActivity(Intent(this, CalculatorActivity::class.java))
        }
    }

    private fun calculateInitialDelay(): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(now)) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        return target.timeInMillis - now.timeInMillis
    }
}