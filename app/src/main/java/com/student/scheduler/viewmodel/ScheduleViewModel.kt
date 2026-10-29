package com.student.scheduler.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.student.scheduler.StudentApplication
import com.student.scheduler.model.LessonUi
import com.student.scheduler.model.toUi
import com.student.scheduler.utils.DateFormatting
import java.util.Calendar

/** Backs the "Расписание" screen: full week of lessons, one day (tab) at a time. */
class ScheduleViewModel(application: Application) : AndroidViewModel(application) {

    private val scheduleRepository = (application as StudentApplication).scheduleRepository

    val todayIndex: Int = DateFormatting.dayOfWeekIndex(Calendar.getInstance()).coerceIn(0, 5)

    private val _selectedDayIndex = MutableLiveData(todayIndex)
    val selectedDayIndex: LiveData<Int> = _selectedDayIndex

    val lessons: LiveData<List<LessonUi>> = _selectedDayIndex.switchMap { day ->
        scheduleRepository.observeLessonsForDay(day).map { list ->
            list.map { it.toUi() }.sortedBy { it.timeRange }
        }
    }

    fun selectDay(index: Int) {
        if (index == _selectedDayIndex.value) return
        _selectedDayIndex.value = index
    }
}
