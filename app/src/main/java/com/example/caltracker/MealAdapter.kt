package com.example.caltracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MealAdapter(
    private var meals: List<MealEntity>,
    private val onMealClick: (MealEntity) -> Unit
) : RecyclerView.Adapter<MealAdapter.MealViewHolder>() {

    private var selectedPosition: Int = -1 // Track selected item position

    inner class MealViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDateTime: TextView = itemView.findViewById(R.id.tv_date_time)
        val tvMealDescription: TextView = itemView.findViewById(R.id.tv_meal_description)
        val tvCaloriesProtein: TextView = itemView.findViewById(R.id.tv_calories_protein)

        fun bind(meal: MealEntity, isSelected: Boolean) {
            tvDateTime.text = "${meal.date} ${meal.time}"
            tvMealDescription.text = if (meal.description.isEmpty()) meal.mealType else "${meal.mealType} - ${meal.description}"
            tvCaloriesProtein.text = "Calories: ${meal.calories}, Protein: ${meal.protein}g"
            // Highlight selected item
            itemView.setBackgroundColor(
                if (isSelected) 0xFF555555.toInt() else 0xFF333333.toInt()
            )
            println("MealAdapter: Binding meal at position $adapterPosition: $meal, Selected: $isSelected")
            itemView.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = adapterPosition
                onMealClick(meal)
                // Notify changes to update highlighting
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.meal_item, parent, false)
        return MealViewHolder(view)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        holder.bind(meals[position], position == selectedPosition)
    }

    override fun getItemCount(): Int = meals.size

    fun updateMeals(newMeals: List<MealEntity>) {
        meals = newMeals
        selectedPosition = -1 // Reset selection when meals change
        notifyDataSetChanged()
    }

    fun clearSelection() {
        val previousPosition = selectedPosition
        selectedPosition = -1
        notifyItemChanged(previousPosition)
    }
}