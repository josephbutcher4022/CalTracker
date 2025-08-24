package com.example.caltracker

import android.app.AlertDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.caltracker.databinding.ActivityDailyTotalsBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

class DailyTotalsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDailyTotalsBinding
    private lateinit var adapter: DailyTotalsAdapter
    private val repository = MealRepository(AppDatabase.getDatabase(this))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDailyTotalsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = DailyTotalsAdapter { dailyTotal ->
            lifecycleScope.launch {
                repository.getMealsByDate(dailyTotal.date).collectLatest { meals ->
                    Timber.d("DailyTotalsActivity: Fetched meals for date ${dailyTotal.date}: $meals")
                    showMealsPopup(meals)
                }
            }
        }
        binding.rvDailyTotals.layoutManager = LinearLayoutManager(this)
        binding.rvDailyTotals.adapter = adapter

        lifecycleScope.launch {
            repository.getAllDailyTotals().collectLatest { totals ->
                Timber.d("DailyTotalsActivity: Fetched daily totals: $totals")
                adapter.submitList(totals)
            }
        }

        binding.btnBack.setOnClickListener {
            Timber.d("DailyTotalsActivity: Back button clicked")
            finish()
        }
    }

    private fun showMealsPopup(meals: List<MealEntity>) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_meals_list, null)
        val rvMeals: RecyclerView = dialogView.findViewById(R.id.rv_meals)
        val mealAdapter = MealAdapter()
        rvMeals.layoutManager = LinearLayoutManager(this)
        rvMeals.adapter = mealAdapter
        mealAdapter.submitList(meals)

        AlertDialog.Builder(this)
            .setTitle("Meals for selected day")
            .setView(dialogView)
            .setPositiveButton("Close") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}