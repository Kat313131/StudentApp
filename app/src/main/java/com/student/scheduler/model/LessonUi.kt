package com.student.scheduler.model

import androidx.annotation.ColorRes

/**
 * UI model for a single lesson card (Home / Schedule screens).
 *
 * TEMPORARY: filled with mock data for now so the UI can be built
 * before the database exists. From week 2 this will be produced by
 * mapping the Room `Lesson` + `Subject` entities in the repository.
 */
data class LessonUi(
    val subjectName: String,
    val timeRange: String,
    val room: String,
    val teacher: String,
    @ColorRes val accentColor: Int
)
