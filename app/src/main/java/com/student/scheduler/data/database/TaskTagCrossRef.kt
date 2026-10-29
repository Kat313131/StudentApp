package com.student.scheduler.data.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Junction (4th required) table implementing the many-to-many relation
 * between [TaskEntity] and [TagEntity]: one task can have several tags,
 * and one tag can be attached to several tasks. Both foreign keys cascade
 * on delete so removing a task or a tag cleans up the links automatically.
 */
@Entity(
    tableName = "task_tag_cross_ref",
    primaryKeys = ["taskId", "tagId"],
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TagEntity::class,
            parentColumns = ["id"],
            childColumns = ["tagId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("taskId"), Index("tagId")]
)
data class TaskTagCrossRef(
    val taskId: Long,
    val tagId: Long
)
