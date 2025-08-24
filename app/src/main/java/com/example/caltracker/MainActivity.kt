package com.example.caltracker

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.caltracker.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: MealAdapter
    private lateinit var repository: MealRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("MainActivity: Starting onCreate")
        binding = ActivityMainBinding.inflate(layoutInflater)
        Timber.d("MainActivity: Binding initialized")
        setContentView(binding.root)
        Timber.d("MainActivity: Set content view")

        repository = MealRepository(AppDatabase.getDatabase(this))
        Timber.d("MainActivity: Repository initialized")

        viewModel = ViewModelProvider(this, MainViewModelFactory(this)).get(MainViewModel::class.java)
        Timber.d("MainActivity: ViewModel initialized")
        adapter = MealAdapter()
        Timber.d("MainActivity: Adapter created")
        binding.rvMeals.layoutManager = LinearLayoutManager(this).apply { reverseLayout = false; stackFromEnd = false }
        Timber.d("MainActivity: LayoutManager set with normal order")
        binding.rvMeals.adapter = adapter
        Timber.d("MainActivity: Adapter set")

        // Set up swipe-to-delete
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false // No dragging support
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val meal = adapter.currentList[position]
                lifecycleScope.launch {
                    viewModel.deleteMeal(meal)
                    Timber.d("MainActivity: Meal deleted: $meal")
                    // Update daily totals after deletion
                    val date = meal.date
                    val meals = repository.getMealsByDate(date).first()
                    val totalCalories = meals.sumOf { m: MealEntity -> m.calories.toLong() }.toInt()
                    val totalProtein = meals.sumOf { m: MealEntity -> m.protein.toLong() }.toInt()
                    val existingDailyTotal = repository.getDailyTotalByDate(date)
                    if (meals.isEmpty() && existingDailyTotal != null) {
                        repository.deleteDailyTotal(existingDailyTotal)
                    } else if (existingDailyTotal != null) {
                        repository.updateDailyTotal(DailyTotalEntity(id = existingDailyTotal.id, date = date, totalCalories = totalCalories, totalProtein = totalProtein))
                    }
                    Timber.d("MainActivity: Daily totals updated after deletion for $date: Calories=$totalCalories, Protein=$totalProtein")
                    adapter.notifyDataSetChanged() // Force adapter refresh
                }
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.rvMeals)
        Timber.d("MainActivity: Swipe-to-delete set up")

        // Set up spinner with meal types
        val mealTypes = arrayOf("Breakfast", "Lunch", "Dinner", "Snack")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, mealTypes)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerMealType.adapter = spinnerAdapter
        Timber.d("MainActivity: Spinner adapter set")

        lifecycleScope.launch {
            Timber.d("MainActivity: Starting coroutine")
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(java.util.Date())
            Timber.d("MainActivity: Date formatted: $date")
            repository.getMealsForToday(date).collectLatest { meals: List<MealEntity> ->
                Timber.d("MainActivity: Collected meals: $meals")
                adapter.submitList(meals)
            }
        }
        Timber.d("MainActivity: Coroutine launched")

        binding.btnLogMeal.setOnClickListener {
            Timber.d("MainActivity: Log meal button clicked")
            val mealType = binding.spinnerMealType.selectedItem?.toString() ?: "Unknown"
            Timber.d("MainActivity: Meal type: $mealType")
            val description = binding.etDescription.text.toString().trim()
            Timber.d("MainActivity: Description: $description")
            val caloriesStr = binding.etCalories.text.toString().trim()
            Timber.d("MainActivity: Calories string: $caloriesStr")
            val proteinStr = binding.etProtein.text.toString().trim()
            Timber.d("MainActivity: Protein string: $proteinStr")
            val calories = caloriesStr.toIntOrNull() ?: 0
            val protein = proteinStr.toIntOrNull() ?: 0

            // Prevent logging if all input fields are empty
            if (description.isEmpty() && calories == 0 && protein == 0) {
                Timber.d("MainActivity: Log meal skipped - all input fields are empty")
                return@setOnClickListener
            }

            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(java.util.Date())
            val time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(java.util.Date())
            val meal = MealEntity(
                date = date,
                time = time,
                mealType = mealType,
                description = description,
                calories = calories,
                protein = protein
            )
            lifecycleScope.launch {
                try {
                    Timber.d("MainActivity: Attempting to log meal: $meal")
                    viewModel.insertMeal(meal)
                    Timber.d("MainActivity: Meal logged successfully: $meal")
                    // Clear input fields after logging
                    binding.etDescription.text.clear()
                    binding.etCalories.text.clear()
                    binding.etProtein.text.clear()

                    // Update daily totals after logging a meal
                    val meals = repository.getMealsByDate(date).first()
                    val totalCalories = meals.sumOf { m: MealEntity -> m.calories.toLong() }.toInt()
                    val totalProtein = meals.sumOf { m: MealEntity -> m.protein.toLong() }.toInt()
                    val existingDailyTotal = repository.getDailyTotalByDate(date)
                    if (existingDailyTotal == null) {
                        repository.insertDailyTotal(DailyTotalEntity(date = date, totalCalories = totalCalories, totalProtein = totalProtein))
                    } else {
                        repository.updateDailyTotal(DailyTotalEntity(id = existingDailyTotal.id, date = date, totalCalories = totalCalories, totalProtein = totalProtein))
                    }
                    Timber.d("MainActivity: Daily totals updated for $date: Calories=$totalCalories, Protein=$totalProtein")
                    adapter.notifyDataSetChanged() // Force adapter refresh after insert
                } catch (e: Exception) {
                    Timber.e(e, "MainActivity: Failed to log meal or update totals: $meal")
                    Toast.makeText(this@MainActivity, "Error saving meal: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnDailyTotals.setOnClickListener {
            Timber.d("MainActivity: Daily totals button clicked")
            startActivity(Intent(this, DailyTotalsActivity::class.java))
        }

        binding.btnCalculator.setOnClickListener {
            Timber.d("MainActivity: Calculator button clicked")
            startActivity(Intent(this, CalculatorActivity::class.java))
        }
        Timber.d("MainActivity: onCreate completed")
    }
}