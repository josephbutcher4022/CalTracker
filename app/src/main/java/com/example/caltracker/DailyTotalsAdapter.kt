package com.example.caltracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DailyTotalsAdapter(
    private var totals: List<DailyTotalEntity>,
    private val onSelect: (DailyTotalEntity?) -> Unit
) : RecyclerView.Adapter<DailyTotalsAdapter.TotalViewHolder>() {

    private var selectedPosition: Int = -1

    class TotalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate: TextView = itemView.findViewById(R.id.tv_date)
        val tvTotals: TextView = itemView.findViewById(R.id.tv_totals)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TotalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.daily_total_item, parent, false)
        return TotalViewHolder(view)
    }

    override fun onBindViewHolder(holder: TotalViewHolder, position: Int) {
        val total = totals[position]
        holder.tvDate.text = total.date
        holder.tvTotals.text = "${total.totalCalories} cal, ${total.totalProtein}g protein"
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
            onSelect(if (selectedPosition != -1) totals[selectedPosition] else null)
        }
    }

    override fun getItemCount(): Int = totals.size

    fun updateTotals(newTotals: List<DailyTotalEntity>) {
        totals = newTotals
        selectedPosition = -1 // Reset selection
        notifyDataSetChanged()
    }

    fun clearSelection() {
        val previousPosition = selectedPosition
        selectedPosition = -1
        notifyItemChanged(previousPosition)
    }
}