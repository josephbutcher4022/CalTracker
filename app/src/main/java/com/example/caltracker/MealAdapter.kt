package com.example.caltracker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.caltracker.databinding.MealItemBinding

class MealAdapter : ListAdapter<MealEntity, MealAdapter.MealViewHolder>(MealDiffCallback()) {

    class MealViewHolder(private val binding: MealItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(meal: MealEntity) {
            binding.meal = meal
            binding.executePendingBindings() // Force binding update
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val binding = MealItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MealViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MealDiffCallback : DiffUtil.ItemCallback<MealEntity>() {
        override fun areItemsTheSame(oldItem: MealEntity, newItem: MealEntity): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: MealEntity, newItem: MealEntity): Boolean = oldItem == newItem
    }
}