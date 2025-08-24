package com.example.caltracker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.caltracker.databinding.ItemFoodBinding

class FoodAdapter : ListAdapter<FoodEntity, FoodAdapter.FoodViewHolder>(FoodDiffCallback()) {

    var selectedFood: FoodEntity? = null
        private set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemFoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val food = getItem(position)
        holder.bind(food)
        holder.itemView.setOnClickListener {
            selectedFood = food
            notifyDataSetChanged()
        }
        holder.itemView.isSelected = food == selectedFood
    }

    class FoodViewHolder(private val binding: ItemFoodBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(food: FoodEntity) {
            binding.tvFoodName.text = food.name
            binding.tvCaloriesPer100g.text = food.caloriesPer100g.toString()
        }
    }

    class FoodDiffCallback : DiffUtil.ItemCallback<FoodEntity>() {
        override fun areItemsTheSame(oldItem: FoodEntity, newItem: FoodEntity): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: FoodEntity, newItem: FoodEntity): Boolean = oldItem == newItem
    }
}