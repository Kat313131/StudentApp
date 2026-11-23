package com.student.scheduler

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.student.scheduler.data.database.AppDatabase
import com.student.scheduler.data.repository.ScheduleRepository
import com.student.scheduler.data.repository.TaskRepository
import com.student.scheduler.utils.PreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

/**
 * Application-wide singletons: a long-lived coroutine scope for background
 * database work (seeding, writes) plus the Room database and repositories,
 * built lazily so they are only created once, on first access.
 */
class StudentApplication : Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

    private val database by lazy { AppDatabase.getInstance(this, applicationScope) }

    val scheduleRepository by lazy { ScheduleRepository(database.subjectDao(), database.lessonDao()) }
    val taskRepository by lazy { TaskRepository(database.taskDao(), database.tagDao()) }
    val preferences by lazy { PreferencesManager(this) }

    override fun onCreate() {
        super.onCreate()
        // Apply the saved theme before any Activity is created, so there's no visible flash/flicker.
        AppCompatDelegate.setDefaultNightMode(
            if (preferences.darkTheme) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}
