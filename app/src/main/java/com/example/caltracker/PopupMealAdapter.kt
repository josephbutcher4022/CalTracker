package com.example.caltracker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.caltracker.databinding.MealItemPopupBinding
import timber.log.Timber

class PopupMealAdapter(
    private val onMealSelected: (MealEntity?) -> Unit
) : ListAdapter<MealEntity, PopupMealAdapter.MealViewHolder>(MealDiffCallback()) {

    private var selectedMeal: MealEntity? = null

    class MealViewHolder(
        private val binding: MealItemPopupBinding,
        private val onMealSelected: (MealEntity?) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(meal: MealEntity, isSelected: Boolean) {
            binding.meal = meal
            binding.isSelected = isSelected
            binding.root.setOnClickListener {
                onMealSelected(meal)
            }
            binding.executePendingBindings()
            Timber.d("PopupMealAdapter: Bound meal: $meal, isSelected: $isSelected")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val binding = MealItemPopupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MealViewHolder(binding, onMealSelected)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        val meal = getItem(position)
        holder.bind(meal, meal == selectedMeal)
    }

    fun selectMeal(meal: MealEntity?) {
        selectedMeal = meal
        notifyDataSetChanged() // Refresh to update selection highlight
    }

    fun getSelectedMeal(): MealEntity? = selectedMeal

    class MealDiffCallback : DiffUtil.ItemCallback<MealEntity>() {
        override fun areItemsTheSame(oldItem: MealEntity, newItem: MealEntity): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: MealEntity, newItem: MealEntity): Boolean = oldItem == newItem
    }
}