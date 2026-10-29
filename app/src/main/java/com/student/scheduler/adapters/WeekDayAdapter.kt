package com.student.scheduler.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.student.scheduler.R
import com.student.scheduler.model.WeekDay

/** Горизонтальный выбор дня недели (Пн 11, Вт 12, ...) на экране «Сегодня». */
class WeekDayAdapter(
    private val days: List<WeekDay>,
    private val onDayClick: (position: Int) -> Unit
) : RecyclerView.Adapter<WeekDayAdapter.DayViewHolder>() {

    class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.text_day_name)
        val number: TextView = itemView.findViewById(R.id.text_day_number)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_week_day, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val day = days[position]
        val context = holder.itemView.context

        holder.name.text = day.shortName
        holder.number.text = day.dayNumber.toString()

        if (day.isSelected) {
            holder.itemView.setBackgroundResource(R.drawable.bg_day_selected)
            holder.name.setTextColor(context.getColor(R.color.surface))
            holder.number.setTextColor(context.getColor(R.color.surface))
        } else {
            holder.itemView.setBackgroundResource(R.drawable.bg_day_unselected)
            holder.name.setTextColor(context.getColor(R.color.text_secondary))
            holder.number.setTextColor(context.getColor(R.color.text_primary))
        }

        holder.itemView.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) onDayClick(pos)
        }
    }

    override fun getItemCount(): Int = days.size
}
