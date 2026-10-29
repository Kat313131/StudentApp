package com.student.scheduler

import android.app.Application
import com.student.scheduler.data.database.AppDatabase
import com.student.scheduler.data.repository.ScheduleRepository
import com.student.scheduler.data.repository.TaskRepository
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
}
