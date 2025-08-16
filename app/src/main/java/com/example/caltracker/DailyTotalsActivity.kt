package com.example.caltracker

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.collectLatest
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener

class DailyTotalsActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels { MainViewModelFactory(application) }
    private var selectedTotal: DailyTotalEntity? = null
    private lateinit var adapter: DailyTotalsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_totals)
        println("DailyTotalsActivity: Started")

        val rootLayout: View = findViewById(R.id.root_layout)
        val rvDailyTotals: RecyclerView = findViewById(R.id.rv_daily_totals)
        val btnBack: Button = findViewById(R.id.btn_back)
        val btnDelete: Button = findViewById(R.id.btn_delete)

        adapter = DailyTotalsAdapter(emptyList()) { dailyTotal ->
            selectedTotal = dailyTotal
            btnDelete.isEnabled = true
            println("DailyTotalsActivity: Selected daily total for deletion: $dailyTotal")
        }
        rvDailyTotals.layoutManager = LinearLayoutManager(this)
        rvDailyTotals.adapter = adapter

        // Clear selection when tapping outside RecyclerView
        rootLayout.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                selectedTotal?.let {
                    selectedTotal = null
                    adapter.clearSelection()
                    btnDelete.isEnabled = false
                    println("DailyTotalsActivity: Cleared daily total selection on background tap")
                }
            }
            false // Allow other touch events to proceed
        }

        // Clear selection when tapping empty space in RecyclerView
        val gestureDetector = GestureDetector(this, object : SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                val view = rvDailyTotals.findChildViewUnder(e.x, e.y)
                if (view == null && selectedTotal != null) {
                    selectedTotal = null
                    adapter.clearSelection()
                    btnDelete.isEnabled = false
                    println("DailyTotalsActivity: Cleared daily total selection on empty RecyclerView tap")
                }
                return false // Allow item clicks to proceed
            }
        })
        rvDailyTotals.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            false // Allow RecyclerView item clicks
        }

        lifecycleScope.launchWhenStarted {
            viewModel.dailyTotals.collectLatest { totals ->
                println("DailyTotalsActivity: Collected dailyTotals update with ${totals.size} entries")
                adapter.updateTotals(totals)
            }
        }

        btnBack.setOnClickListener {
            println("DailyTotalsActivity: Back button clicked")
            startActivity(Intent(this, MainActivity::class.java))
            finish() // Close DailyTotalsActivity
        }

        btnDelete.setOnClickListener {
            selectedTotal?.let { total ->
                println("DailyTotalsActivity: Deleting daily total: $total")
                viewModel.deleteDailyTotal(total)
                selectedTotal = null
                adapter.clearSelection() // Clear highlight
                btnDelete.isEnabled = false
            }
        }
    }
}