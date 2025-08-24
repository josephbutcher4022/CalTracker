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
    private lateinit var repository: MealRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("DailyTotalsActivity: Starting onCreate")
        binding = ActivityDailyTotalsBinding.inflate(layoutInflater)
        Timber.d("DailyTotalsActivity: Binding initialized")
        setContentView(binding.root)
        Timber.d("DailyTotalsActivity: Set content view")

        repository = MealRepository(AppDatabase.getDatabase(this))
        Timber.d("DailyTotalsActivity: Repository initialized")

        adapter = DailyTotalsAdapter { dailyTotal ->
            lifecycleScope.launch {
                repository.getMealsByDate(dailyTotal.date).collectLatest { meals ->
                    Timber.d("DailyTotalsActivity: Fetched meals for date ${dailyTotal.date}: $meals")
                    showMealsPopup(meals)
                }
            }
        }
        Timber.d("DailyTotalsActivity: Adapter created")

        binding.rvDailyTotals.layoutManager = LinearLayoutManager(this).apply {
            reverseLayout = true
            stackFromEnd = true
        }
        Timber.d("DailyTotalsActivity: LayoutManager set with reverse order")
        binding.rvDailyTotals.adapter = adapter
        Timber.d("DailyTotalsActivity: Adapter set")

        lifecycleScope.launch {
            Timber.d("DailyTotalsActivity: Starting coroutine")
            repository.getAllDailyTotals().collectLatest { totals ->
                Timber.d("DailyTotalsActivity: Fetched daily totals: $totals")
                adapter.submitList(totals)
            }
        }
        Timber.d("DailyTotalsActivity: Coroutine launched")

        binding.btnBack.setOnClickListener {
            Timber.d("DailyTotalsActivity: Back button clicked")
            finish()
        }
        Timber.d("DailyTotalsActivity: onCreate completed")
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