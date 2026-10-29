package com.student.scheduler.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.student.scheduler.R
import com.student.scheduler.model.LessonUi

/** Карточки пар для экранов «Сегодня» и «Расписание». */
class LessonAdapter(
    private val lessons: List<LessonUi>
) : RecyclerView.Adapter<LessonAdapter.LessonViewHolder>() {

    class LessonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val accent: View = itemView.findViewById(R.id.view_accent)
        val time: TextView = itemView.findViewById(R.id.text_time)
        val subject: TextView = itemView.findViewById(R.id.text_subject)
        val details: TextView = itemView.findViewById(R.id.text_details)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lesson, parent, false)
        return LessonViewHolder(view)
    }

    override fun onBindViewHolder(holder: LessonViewHolder, position: Int) {
        val lesson = lessons[position]
        holder.time.text = lesson.timeRange
        holder.subject.text = lesson.subjectName
        holder.details.text = listOf(lesson.lessonType, lesson.room, lesson.teacher)
            .filter { it.isNotBlank() }
            .joinToString(" · ")
        holder.accent.setBackgroundColor(holder.itemView.context.getColor(lesson.accentColor))
    }

    override fun getItemCount(): Int = lessons.size
}
