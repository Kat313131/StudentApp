package com.student.scheduler.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.student.scheduler.StudentApplication
import com.student.scheduler.model.LessonUi
import com.student.scheduler.model.TaskUi
import com.student.scheduler.model.WeekDay
import com.student.scheduler.model.toUi
import com.student.scheduler.utils.DateFormatting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * Backs the "Сегодня" (Home) screen: the selected week day, its lessons
 * (from Room, mapped to [LessonUi]) and the tasks due in the next 2 days.
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as StudentApplication
    private val scheduleRepository = app.scheduleRepository
    private val taskRepository = app.taskRepository

    private val actualTodayIndex: Int = DateFormatting.dayOfWeekIndex(Calendar.getInstance()).coerceIn(0, 5)
    private val _selectedDayIndex = MutableLiveData(actualTodayIndex)

    val weekDays: LiveData<List<WeekDay>> = _selectedDayIndex.map { selected ->
        DateFormatting.WEEK_DAY_SHORT_NAMES.mapIndexed { index, name ->
            val dayNumber = dayNumberFor(index)
            WeekDay(name, dayNumber, isSelected = index == selected)
        }
    }

    val lessonsHeader: LiveData<String> = _selectedDayIndex.map { selected ->
        if (selected == actualTodayIndex) "Пары сегодня" else "Пары — ${DateFormatting.WEEK_DAY_FULL_NAMES[selected]}"
    }

    /** Re-queries Room whenever the selected day changes (switchMap keeps only the latest observer alive). */
    val todayLessons: LiveData<List<LessonUi>> = _selectedDayIndex.switchMap { day ->
        scheduleRepository.observeLessonsForDay(day).map { list -> list.map { it.toUi() } }
    }

    private val today = DateFormatting.startOfDay(Calendar.getInstance())
    private val horizonEnd = today + 2 * DateFormatting.DAY_MILLIS

    val todayTasks: LiveData<List<TaskUi>> = taskRepository.observeDueBetween(today, horizonEnd).map { list -> list.map { it.toUi() } }

    /** True while the lessons list for the selected day is still empty. Drives the empty-state view. */
    val hasLessons: LiveData<Boolean> = todayLessons.map { it.isNotEmpty() }

    fun dayNumberFor(index: Int): Int {
        val calendar = Calendar.getInstance()
        val diff = index - actualTodayIndex
        calendar.add(Calendar.DAY_OF_MONTH, diff)
        return calendar.get(Calendar.DAY_OF_MONTH)
    }

    fun selectDay(position: Int) {
        if (position == _selectedDayIndex.value) return
        _selectedDayIndex.value = position
    }

    /** Persists the checkbox toggle from the task card in the background. */
    fun onTaskCheckedChanged(taskId: Long, isChecked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            taskRepository.setDone(taskId, isChecked)
        }
    }
}
