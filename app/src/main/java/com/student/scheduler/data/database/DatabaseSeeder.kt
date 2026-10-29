package com.student.scheduler.data.database

import java.util.Calendar

/**
 * Fills a freshly created database with demo data (subjects, a week of
 * lessons and a handful of tasks) matching the approved UI mockup, so a
 * first-time user immediately sees a populated app instead of empty screens.
 */
object DatabaseSeeder {

    suspend fun seed(db: AppDatabase) {
        val subjectDao = db.subjectDao()
        val lessonDao = db.lessonDao()
        val taskDao = db.taskDao()
        val tagDao = db.tagDao()

        val mathId = subjectDao.insert(SubjectEntity(name = "Математический анализ", teacher = "Проф. Иванов А.М.", colorIndex = 0))
        val physicsId = subjectDao.insert(SubjectEntity(name = "Физика", teacher = "Проф. Петрова Е.В.", colorIndex = 1))
        val progId = subjectDao.insert(SubjectEntity(name = "Программирование", teacher = "Доц. Сидоров К.П.", colorIndex = 2))
        val langId = subjectDao.insert(SubjectEntity(name = "Иностранный язык", teacher = "Доц. Кузнецова Л.И.", colorIndex = 2))

        lessonDao.insertAll(
            listOf(
                LessonEntity(subjectId = mathId, dayOfWeek = 2, startTime = "09:00", endTime = "10:30", room = "Ауд. 305", lessonType = "Лекция"),
                LessonEntity(subjectId = physicsId, dayOfWeek = 2, startTime = "10:45", endTime = "12:15", room = "Ауд. 112", lessonType = "Лекция"),
                LessonEntity(subjectId = progId, dayOfWeek = 2, startTime = "13:00", endTime = "14:30", room = "Ауд. 408", lessonType = "Практика"),
                LessonEntity(subjectId = physicsId, dayOfWeek = 0, startTime = "08:30", endTime = "10:00", room = "Ауд. 110", lessonType = "Практика"),
                LessonEntity(subjectId = langId, dayOfWeek = 0, startTime = "10:15", endTime = "11:45", room = "Ауд. 202", lessonType = "Практика"),
                LessonEntity(subjectId = mathId, dayOfWeek = 1, startTime = "09:00", endTime = "10:30", room = "Ауд. 305", lessonType = "Практика"),
                LessonEntity(subjectId = progId, dayOfWeek = 1, startTime = "10:45", endTime = "12:15", room = "Ауд. 408", lessonType = "Лекция"),
                LessonEntity(subjectId = langId, dayOfWeek = 3, startTime = "09:00", endTime = "10:30", room = "Ауд. 202", lessonType = "Практика"),
                LessonEntity(subjectId = mathId, dayOfWeek = 3, startTime = "10:45", endTime = "12:15", room = "Ауд. 305", lessonType = "Лекция"),
                LessonEntity(subjectId = progId, dayOfWeek = 4, startTime = "09:00", endTime = "10:30", room = "Ауд. 408", lessonType = "Практика"),
                LessonEntity(subjectId = physicsId, dayOfWeek = 4, startTime = "10:45", endTime = "12:15", room = "Ауд. 110", lessonType = "Практика")
            )
        )

        val urgentTagId = tagDao.insert(TagEntity(name = "Срочно", colorIndex = 1))
        val courseworkTagId = tagDao.insert(TagEntity(name = "Курсовая", colorIndex = 2))

        val today = todayAtHour(18)
        val tomorrow = today + DAY_MILLIS
        val inFiveDays = today + 5 * DAY_MILLIS

        val labTaskId = taskDao.insert(
            TaskEntity(subjectId = physicsId, title = "Лабораторная работа №3", description = "Оформить отчёт по колебаниям маятника.", dueDateMillis = today, priority = 2)
        )
        taskDao.insert(TaskEntity(subjectId = mathId, title = "Домашнее задание №5", description = "Решить задачи 12-20 из практикума.", dueDateMillis = tomorrow, priority = 1))
        val courseworkTaskId = taskDao.insert(
            TaskEntity(subjectId = progId, title = "Курсовой проект — Глава 2", description = "Спроектировать структуру базы данных.", dueDateMillis = inFiveDays, priority = 2)
        )
        taskDao.insert(TaskEntity(subjectId = physicsId, title = "Подготовка к коллоквиуму", description = "Повторить темы 1-4.", dueDateMillis = inFiveDays + DAY_MILLIS, priority = 0))

        tagDao.addTagsToTask(listOf(TaskTagCrossRef(labTaskId, urgentTagId)))
        tagDao.addTagsToTask(listOf(TaskTagCrossRef(courseworkTaskId, courseworkTagId)))
    }

    private fun todayAtHour(hour: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private const val DAY_MILLIS = 24L * 60 * 60 * 1000
}
