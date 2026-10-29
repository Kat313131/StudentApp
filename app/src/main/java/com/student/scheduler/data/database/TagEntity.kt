package com.student.scheduler.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Main entity: a free-form label a task can be marked with (e.g. "Срочно", "Курсовая").
 * Linked to [TaskEntity] through the [TaskTagCrossRef] junction table (many-to-many).
 */
@Entity(tableName = "tags")
data class TagEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val colorIndex: Int
)
