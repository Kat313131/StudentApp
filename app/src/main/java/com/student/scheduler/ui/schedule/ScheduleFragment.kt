package com.student.scheduler.ui.schedule

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.student.scheduler.R
import com.student.scheduler.adapters.LessonAdapter
import com.student.scheduler.utils.DateFormatting
import com.student.scheduler.viewmodel.ScheduleViewModel

/** "Расписание" — the full week schedule, one day (tab) at a time. */
class ScheduleFragment : Fragment(R.layout.fragment_schedule) {

    private val viewModel: ScheduleViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabDays = view.findViewById<TabLayout>(R.id.tab_days)
        val recycler = view.findViewById<RecyclerView>(R.id.recycler_schedule)
        val textEmpty = view.findViewById<TextView>(R.id.text_no_lessons)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        DateFormatting.WEEK_DAY_SHORT_NAMES.forEach { name -> tabDays.addTab(tabDays.newTab().setText(name)) }
        tabDays.getTabAt(viewModel.todayIndex)?.select()

        tabDays.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) = viewModel.selectDay(tab.position)
            override fun onTabUnselected(tab: TabLayout.Tab) = Unit
            override fun onTabReselected(tab: TabLayout.Tab) = Unit
        })

        viewModel.lessons.observe(viewLifecycleOwner) { lessons ->
            recycler.adapter = LessonAdapter(lessons)
            val hasLessons = lessons.isNotEmpty()
            recycler.visibility = if (hasLessons) View.VISIBLE else View.GONE
            textEmpty.visibility = if (hasLessons) View.GONE else View.VISIBLE
        }
    }
}
