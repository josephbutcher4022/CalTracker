package com.example.caltracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DailyTotalsAdapter(
    private var totals: List<DailyTotalEntity>,
    private val onTotalClick: (DailyTotalEntity) -> Unit
) : RecyclerView.Adapter<DailyTotalsAdapter.TotalViewHolder>() {

    private var selectedPosition: Int = -1 // Track selected item position

    inner class TotalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate: TextView = itemView.findViewById(R.id.tv_date)
        val tvTotalCalories: TextView = itemView.findViewById(R.id.tv_total_calories)
        val tvTotalProtein: TextView = itemView.findViewById(R.id.tv_total_protein)

        fun bind(total: DailyTotalEntity, isSelected: Boolean) {
            tvDate.text = total.date
            tvTotalCalories.text = "Calories: ${total.totalCalories}"
            tvTotalProtein.text = "Protein: ${total.totalProtein}g"
            // Highlight selected item
            itemView.setBackgroundColor(
                if (isSelected) 0xFF555555.toInt() else 0xFF333333.toInt()
            )
            println("DailyTotalsAdapter: Binding total at position $adapterPosition: $total, Selected: $isSelected")
            itemView.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = adapterPosition
                onTotalClick(total)
                // Notify changes to update highlighting
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TotalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.daily_total_item, parent, false)
        return TotalViewHolder(view)
    }

    override fun onBindViewHolder(holder: TotalViewHolder, position: Int) {
        holder.bind(totals[position], position == selectedPosition)
    }

    override fun getItemCount(): Int = totals.size

    fun updateTotals(newTotals: List<DailyTotalEntity>) {
        totals = newTotals
        selectedPosition = -1 // Reset selection when totals change
        notifyDataSetChanged()
    }

    fun clearSelection() {
        val previousPosition = selectedPosition
        selectedPosition = -1
        notifyItemChanged(previousPosition)
    }
}