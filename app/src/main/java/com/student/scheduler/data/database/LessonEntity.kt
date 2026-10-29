package com.student.scheduler.data.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Main entity: a single lesson (pair) in the weekly schedule.
 * `subjectId` is a FOREIGN KEY to [SubjectEntity] — the "1 Subject -> many Lessons" relation.
 * Deleting a subject cascades and removes all of its lessons.
 */
@Entity(
    tableName = "lessons",
    foreignKeys = [
        ForeignKey(
            entity = SubjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("subjectId")]
)
data class LessonEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subjectId: Long,
    /** 0 = Monday ... 5 = Saturday, matches the day tabs on Home/Schedule screens. */
    val dayOfWeek: Int,
    val startTime: String,
    val endTime: String,
    val room: String,
    /** "Лекция" / "Практика" / "Лабораторная" — shown as a small badge on the lesson card. */
    val lessonType: String
)
