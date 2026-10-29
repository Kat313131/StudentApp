package com.student.scheduler.data.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Main entity: a homework / assignment item.
 * `subjectId` is nullable so a task can optionally be linked to a subject
 * (many Tasks -> 1 Subject). If the subject is deleted, the task is kept
 * but its link is cleared (SET_NULL), so users never silently lose tasks.
 */
@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = SubjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("subjectId")]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subjectId: Long?,
    val title: String,
    val description: String,
    /** Deadline, stored as epoch millis so it can be sorted and compared. */
    val dueDateMillis: Long,
    val isDone: Boolean = false,
    /** 0 = low, 1 = normal, 2 = high — used for sorting and the statistics screen. */
    val priority: Int = 1,
    val createdAtMillis: Long = System.currentTimeMillis(),
    /** True while a deadline reminder notification is scheduled for this task (week 11 feature). */
    val reminderScheduled: Boolean = false
)
