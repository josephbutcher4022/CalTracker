package com.example.caltracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MealAdapter(
    private var meals: List<MealEntity>,
    private val onSelect: (MealEntity?) -> Unit
) : RecyclerView.Adapter<MealAdapter.MealViewHolder>() {

    private var selectedPosition: Int = -1

    class MealViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDateTime: TextView = itemView.findViewById(R.id.tv_date_time)
        val tvDescription: TextView = itemView.findViewById(R.id.tv_meal_description)
        val tvCaloriesProtein: TextView = itemView.findViewById(R.id.tv_calories_protein)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.meal_item, parent, false)
        return MealViewHolder(view)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        val meal = meals[position]
        holder.tvDateTime.text = "${meal.mealType}, ${meal.time}"
        if (meal.description.isNotEmpty()) {
            holder.tvDescription.text = meal.description
            holder.tvDescription.visibility = View.VISIBLE
        } else {
            holder.tvDescription.visibility = View.GONE
        }
        holder.tvCaloriesProtein.text = "${meal.calories} cal, ${meal.protein}g protein"
        // Apply highlight for selected item
        holder.itemView.isSelected = position == selectedPosition
        holder.itemView.setBackgroundColor(
            if (position == selectedPosition) 0xFF555555.toInt() else 0xFF333333.toInt()
        )
        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = if (selectedPosition == position) -1 else position
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
            onSelect(if (selectedPosition != -1) meals[selectedPosition] else null)
        }
    }

    override fun getItemCount(): Int = meals.size

    fun updateMeals(newMeals: List<MealEntity>) {
        meals = newMeals
        selectedPosition = -1 // Reset selection
        notifyDataSetChanged()
    }

    fun clearSelection() {
        val previousPosition = selectedPosition
        selectedPosition = -1
        notifyItemChanged(previousPosition)
    }
}