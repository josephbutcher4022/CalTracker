package com.example.caltracker

import android.app.AlertDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.caltracker.databinding.ActivityDailyTotalsBinding
import com.example.caltracker.databinding.DialogMealsListBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DailyTotalsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDailyTotalsBinding
    private lateinit var adapter: DailyTotalsAdapter
    private val repository = MealRepository(AppDatabase.getDatabase(this))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("DailyTotalsActivity: Starting onCreate")
        binding = ActivityDailyTotalsBinding.inflate(layoutInflater)
        Timber.d("DailyTotalsActivity: Binding initialized")
        setContentView(binding.root)
        Timber.d("DailyTotalsActivity: Set content view")

        adapter = DailyTotalsAdapter { dailyTotal ->
            lifecycleScope.launch {
                val meals = repository.getMealsByDate(dailyTotal.date).first()
                Timber.d("DailyTotalsActivity: Fetched meals for date ${dailyTotal.date}: $meals")
                showMealsPopup(meals, dailyTotal.date)
            }
        }
        Timber.d("DailyTotalsActivity: Adapter created")

        binding.rvDailyTotals.layoutManager = LinearLayoutManager(this)
        Timber.d("DailyTotalsActivity: LayoutManager set with default order")
        binding.rvDailyTotals.adapter = adapter
        Timber.d("DailyTotalsActivity: Adapter set")

        lifecycleScope.launch {
            Timber.d("DailyTotalsActivity: Starting coroutine")
            repository.getAllDailyTotals().collectLatest { totals ->
                Timber.d("DailyTotalsActivity: Fetched daily totals: $totals")
                // Sort totals by date descending to ensure newest day is first
                val sortedTotals = totals.sortedByDescending { it.date }
                adapter.submitList(sortedTotals)
            }
        }
        Timber.d("DailyTotalsActivity: Coroutine launched")

        binding.btnBack.setOnClickListener {
            Timber.d("DailyTotalsActivity: Back button clicked")
            finish()
        }
        Timber.d("DailyTotalsActivity: onCreate completed")
    }

    private fun showMealsPopup(meals: List<MealEntity>, date: String) {
        val dialogBinding = DialogMealsListBinding.inflate(layoutInflater)
        val mealAdapter = PopupMealAdapter { selectedMeal ->
            dialogBinding.rvMeals.adapter?.let { adapter ->
                if (adapter is PopupMealAdapter) {
                    adapter.selectMeal(selectedMeal)
                }
            }
        }
        dialogBinding.rvMeals.layoutManager = LinearLayoutManager(this)
        dialogBinding.rvMeals.adapter = mealAdapter
        mealAdapter.submitList(meals)

        dialogBinding.btnMove.setOnClickListener {
            val selectedMeal = mealAdapter.getSelectedMeal()
            if (selectedMeal == null) {
                AlertDialog.Builder(this, R.style.AlertDialogTheme)
                    .setTitle("No Meal Selected")
                    .setMessage("Please select a meal to move.")
                    .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                    .show()
                return@setOnClickListener
            }
            val calendar = Calendar.getInstance()
            calendar.time = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(selectedMeal.date) ?: return@setOnClickListener
            calendar.add(Calendar.DAY_OF_MONTH, -1)
            val previousDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
            AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setTitle("Move Meal")
                .setMessage("Move ${selectedMeal.description} to $previousDate?")
                .setPositiveButton("Move") { _, _ ->
                    lifecycleScope.launch {
                        repository.moveMeal(selectedMeal, previousDate)
                        Timber.d("DailyTotalsActivity: Moved meal ${selectedMeal.id} to $previousDate")
                        // Refresh the current popup with updated meals
                        val updatedMeals = repository.getMealsByDate(date).first()
                        Timber.d("DailyTotalsActivity: Refetched meals for date $date: $updatedMeals")
                        mealAdapter.submitList(updatedMeals)
                        mealAdapter.selectMeal(null) // Clear selection
                    }
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        AlertDialog.Builder(this, R.style.AlertDialogTheme)
            .setTitle("Meals for $date")
            .setView(dialogBinding.root)
            .setCancelable(true) // Allow tap outside to dismiss
            .show()
    }
}