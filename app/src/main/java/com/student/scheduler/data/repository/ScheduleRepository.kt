package com.student.scheduler.data.repository

import androidx.lifecycle.LiveData
import com.student.scheduler.data.database.LessonDao
import com.student.scheduler.data.database.SubjectDao
import com.student.scheduler.data.database.SubjectEntity
import com.student.scheduler.data.database.LessonWithSubject

/**
 * Single access point for subjects and lessons. Fragments/ViewModels never
 * touch the DAOs directly — this keeps Room usage isolated to the data layer.
 */
class ScheduleRepository(
    private val subjectDao: SubjectDao,
    private val lessonDao: LessonDao
) {
    fun observeLessonsForDay(dayOfWeek: Int): LiveData<List<LessonWithSubject>> =
        lessonDao.observeByDay(dayOfWeek)

    fun observeAllLessons(): LiveData<List<LessonWithSubject>> = lessonDao.observeAll()

    fun observeSubjects(): LiveData<List<SubjectEntity>> = subjectDao.observeAll()

    suspend fun getSubjects(): List<SubjectEntity> = subjectDao.getAll()
}
