package com.example.caltracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class CalculatorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)
        println("CalculatorActivity: Started")

        val btnBack: Button = findViewById(R.id.btn_back)
        val btnDelete: Button = findViewById(R.id.btn_delete)

        btnBack.setOnClickListener {
            println("CalculatorActivity: Back button clicked")
            startActivity(Intent(this, MainActivity::class.java))
            finish() // Close CalculatorActivity
        }

        btnDelete.setOnClickListener {
            println("CalculatorActivity: Delete button clicked")
            // Placeholder for future delete functionality
        }
    }
}