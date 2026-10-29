package com.student.scheduler.model

import androidx.annotation.ColorRes

/**
 * UI model for a single lesson card (Home / Schedule screens).
 * Produced from `LessonWithSubject` (Room join) by the repository layer.
 */
data class LessonUi(
    val id: Long,
    val subjectName: String,
    val timeRange: String,
    val room: String,
    val teacher: String,
    val lessonType: String,
    @ColorRes val accentColor: Int
)
