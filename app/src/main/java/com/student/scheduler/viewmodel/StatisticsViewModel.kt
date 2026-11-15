package com.student.scheduler.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.student.scheduler.StudentApplication
import com.student.scheduler.model.StatisticsUi
import com.student.scheduler.model.SubjectProgress
import com.student.scheduler.utils.ColorPalette
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

/**
 * Backs the "Статистика" screen: overall completion rate and a
 * per-subject breakdown, computed from all tasks currently in Room.
 */
class StatisticsViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as StudentApplication
    private val taskRepository = app.taskRepository
    private val scheduleRepository = app.scheduleRepository

    private val _statistics = MutableLiveData<StatisticsUi>()
    val statistics: LiveData<StatisticsUi> = _statistics

    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> = _isLoading

    fun load() {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val tasks = taskRepository.getAllTasksOnce()
            val subjects = scheduleRepository.getSubjects()
            val now = Calendar.getInstance().timeInMillis

            val total = tasks.size
            val done = tasks.count { it.task.isDone }
            val overdue = tasks.count { !it.task.isDone && it.task.dueDateMillis < now }
            val percent = if (total == 0) 0 else (done * 100) / total

            val bySubject = subjects.map { subject ->
                val subjectTasks = tasks.filter { it.task.subjectId == subject.id }
                val subjectDone = subjectTasks.count { it.task.isDone }
                val subjectPercent = if (subjectTasks.isEmpty()) 0 else (subjectDone * 100) / subjectTasks.size
                SubjectProgress(
                    subjectName = subject.name,
                    totalTasks = subjectTasks.size,
                    doneTasks = subjectDone,
                    percent = subjectPercent,
                    accentColor = ColorPalette.colorRes(subject.colorIndex)
                )
            }.filter { it.totalTasks > 0 }

            val result = StatisticsUi(
                totalTasks = total,
                doneTasks = done,
                overdueTasks = overdue,
                completionPercent = percent,
                bySubject = bySubject
            )
            withContext(Dispatchers.Main) {
                _statistics.value = result
                _isLoading.value = false
            }
        }
    }
}
