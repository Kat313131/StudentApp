package com.student.scheduler.ui.home

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.student.scheduler.R
import com.student.scheduler.adapters.LessonAdapter
import com.student.scheduler.adapters.TaskAdapter
import com.student.scheduler.adapters.WeekDayAdapter
import com.student.scheduler.viewmodel.HomeViewModel

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewModel: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerWeekDays = view.findViewById<RecyclerView>(R.id.recycler_week_days)
        val recyclerLessons = view.findViewById<RecyclerView>(R.id.recycler_lessons)
        val recyclerTasks = view.findViewById<RecyclerView>(R.id.recycler_tasks)
        val textNoLessons = view.findViewById<TextView>(R.id.text_no_lessons)
        val textLessonsHeader = view.findViewById<TextView>(R.id.text_lessons_header)

        recyclerLessons.layoutManager = LinearLayoutManager(requireContext())
        recyclerTasks.layoutManager = LinearLayoutManager(requireContext())

        viewModel.lessonsHeader.observe(viewLifecycleOwner) { header ->
            textLessonsHeader.text = header
        }

        viewModel.weekDays.observe(viewLifecycleOwner) { days ->
            recyclerWeekDays.adapter = WeekDayAdapter(days) { position ->
                viewModel.selectDay(position)
            }
        }

        viewModel.todayLessons.observe(viewLifecycleOwner) { lessons ->
            recyclerLessons.adapter = LessonAdapter(lessons)
            val hasLessons = lessons.isNotEmpty()
            recyclerLessons.visibility = if (hasLessons) View.VISIBLE else View.GONE
            textNoLessons.visibility = if (hasLessons) View.GONE else View.VISIBLE
        }

        viewModel.todayTasks.observe(viewLifecycleOwner) { tasks ->
            recyclerTasks.adapter = TaskAdapter(tasks) { position, isChecked ->
                viewModel.onTaskCheckedChanged(position, isChecked)
            }
        }
    }
}