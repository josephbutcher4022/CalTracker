package com.example.caltracker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.caltracker.databinding.DailyTotalItemBinding

class DailyTotalsAdapter(
    private val onClick: (DailyTotalEntity) -> Unit = {}
) : ListAdapter<DailyTotalEntity, DailyTotalsAdapter.DailyTotalViewHolder>(DailyTotalDiffCallback()) {

    class DailyTotalViewHolder(
        private val binding: DailyTotalItemBinding,
        private val onClick: (DailyTotalEntity) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(dailyTotal: DailyTotalEntity) {
            binding.dailyTotal = dailyTotal
            binding.executePendingBindings() // Ensure data binding updates
            binding.root.setOnClickListener { onClick(dailyTotal) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyTotalViewHolder {
        val binding = DailyTotalItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DailyTotalViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: DailyTotalViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DailyTotalDiffCallback : DiffUtil.ItemCallback<DailyTotalEntity>() {
        override fun areItemsTheSame(oldItem: DailyTotalEntity, newItem: DailyTotalEntity): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: DailyTotalEntity, newItem: DailyTotalEntity): Boolean = oldItem == newItem
    }
}