package com.student.scheduler.data.database

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

/**
 * Read-only join model: a task together with its (optional) subject and
 * the list of tags attached to it through [TaskTagCrossRef]. This is the
 * many-to-many relation resolved by Room in a single query.
 */
data class TaskWithDetails(
    @Embedded val task: TaskEntity,
    @Relation(parentColumn = "subjectId", entityColumn = "id")
    val subject: SubjectEntity?,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = TaskTagCrossRef::class,
            parentColumn = "taskId",
            entityColumn = "tagId"
        )
    )
    val tags: List<TagEntity>
)
