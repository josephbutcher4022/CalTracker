package com.example.caltracker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.caltracker.databinding.DailyTotalItemBinding

class DailyTotalsAdapter(private val onClick: (DailyTotalEntity) -> Unit = {}) : ListAdapter<DailyTotalEntity, DailyTotalsAdapter.DailyTotalViewHolder>(DailyTotalDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyTotalViewHolder {
        val binding = DailyTotalItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DailyTotalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DailyTotalViewHolder, position: Int) {
        val dailyTotal = getItem(position)
        holder.bind(dailyTotal)
        holder.itemView.setOnClickListener { onClick(dailyTotal) }
    }

    class DailyTotalViewHolder(private val binding: DailyTotalItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(dailyTotal: DailyTotalEntity) {
            binding.tvDate.text = dailyTotal.date
            binding.tvTotalCalories.text = dailyTotal.totalCalories.toString()
            binding.tvTotalProtein.text = dailyTotal.totalProtein.toString()
        }
    }

    class DailyTotalDiffCallback : DiffUtil.ItemCallback<DailyTotalEntity>() {
        override fun areItemsTheSame(oldItem: DailyTotalEntity, newItem: DailyTotalEntity): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: DailyTotalEntity, newItem: DailyTotalEntity): Boolean = oldItem == newItem
    }
}