package com.student.scheduler.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.student.scheduler.R
import com.student.scheduler.model.WeekDay

/**
 * Shows the Пн–Сб day strip at the top of Home / Schedule.
 * [onDayClick] lets the fragment react to a new day being picked.
 */
class WeekDayAdapter(
    private val days: List<WeekDay>,
    private val onDayClick: (Int) -> Unit
) : RecyclerView.Adapter<WeekDayAdapter.WeekDayViewHolder>() {

    inner class WeekDayViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        val container: android.view.View = itemView.findViewById(R.id.day_container)
        val name: android.widget.TextView = itemView.findViewById(R.id.text_day_name)
        val number: android.widget.TextView = itemView.findViewById(R.id.text_day_number)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekDayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_week_day, parent, false)
        return WeekDayViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeekDayViewHolder, position: Int) {
        val day = days[position]
        holder.name.text = day.shortName
        holder.number.text = day.dayNumber.toString()

        val context = holder.itemView.context
        if (day.isSelected) {
            holder.container.setBackgroundResource(R.drawable.bg_day_selected)
            holder.name.setTextColor(context.getColor(android.R.color.white))
            holder.number.setTextColor(context.getColor(android.R.color.white))
        } else {
            holder.container.setBackgroundResource(R.drawable.bg_day_unselected)
            holder.name.setTextColor(context.getColor(R.color.text_secondary))
            holder.number.setTextColor(context.getColor(R.color.text_primary))
        }

        holder.container.setOnClickListener { onDayClick(position) }
    }

    override fun getItemCount(): Int = days.size
}
