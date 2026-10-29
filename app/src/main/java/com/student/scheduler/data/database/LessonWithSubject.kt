package com.student.scheduler.data.database

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Read-only join model: a lesson together with its parent subject.
 * Produced by Room from the "1 Subject -> many Lessons" relation so the
 * UI never has to issue a second query to show the subject's name/teacher.
 */
data class LessonWithSubject(
    @Embedded val lesson: LessonEntity,
    @Relation(parentColumn = "subjectId", entityColumn = "id")
    val subject: SubjectEntity
)
