package com.student.scheduler.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.student.scheduler.StudentApplication
import com.student.scheduler.data.database.AppDatabase
import com.student.scheduler.utils.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** Backs the "Настройки" screen: reads/writes [PreferencesManager] and clears local data on request. */
class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as StudentApplication

    val preferences: PreferencesManager = app.preferences
    private val taskRepository = app.taskRepository
    private val scheduleRepository = app.scheduleRepository

    /** Wipes every task (used by "Очистить данные"); the schedule/subjects are kept intact. */
    fun clearTasks(onDone: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            taskRepository.getAllTasksOnce().forEach { taskRepository.deleteTask(it.task) }
            kotlinx.coroutines.withContext(Dispatchers.Main) { onDone() }
        }
    }

    suspend fun buildScheduleExportText(): String {
        val lessons = scheduleRepository.getAllLessonsOnce().groupBy { it.lesson.dayOfWeek }
        val dayNames = com.student.scheduler.utils.DateFormatting.WEEK_DAY_FULL_NAMES
        val builder = StringBuilder("Расписание — \"Студент\"\n\n")
        for (day in dayNames.indices) {
            builder.append(dayNames[day]).append(":\n")
            val dayLessons = lessons[day].orEmpty().sortedBy { it.lesson.startTime }
            if (dayLessons.isEmpty()) {
                builder.append("  — пар нет\n")
            } else {
                dayLessons.forEach { l ->
                    builder.append("  ${l.lesson.startTime}-${l.lesson.endTime}  ${l.subject.name}  (${l.lesson.room})\n")
                }
            }
            builder.append("\n")
        }
        return builder.toString()
    }
}
