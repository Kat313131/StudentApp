package com.student.scheduler.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.student.scheduler.R
import com.student.scheduler.model.LessonUi

/** Renders the "Пары сегодня" / schedule list of lesson cards. */
class LessonAdapter(private val lessons: List<LessonUi>) :
    RecyclerView.Adapter<LessonAdapter.LessonViewHolder>() {

    inner class LessonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val stripe: View = itemView.findViewById(R.id.view_accent_stripe)
        val subject: TextView = itemView.findViewById(R.id.text_subject_name)
        val time: TextView = itemView.findViewById(R.id.text_time_range)
        val room: TextView = itemView.findViewById(R.id.text_room)
        val teacher: TextView = itemView.findViewById(R.id.text_teacher)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lesson_card, parent, false)
        return LessonViewHolder(view)
    }

    override fun onBindViewHolder(holder: LessonViewHolder, position: Int) {
        val lesson = lessons[position]
        val color = holder.itemView.context.getColor(lesson.accentColor)

        holder.subject.text = lesson.subjectName
        holder.time.text = lesson.timeRange
        holder.time.setTextColor(color)
        holder.room.text = "🏛 ${lesson.room}"
        holder.teacher.text = "👤 ${lesson.teacher}"
        holder.stripe.setBackgroundColor(color)
    }

    override fun getItemCount(): Int = lessons.size
}
