package com.student.scheduler.model

import androidx.annotation.ColorRes

/**
 * UI model for a single task card (Home / Tasks screens).
 * Produced from `TaskWithDetails` (Room join: task + subject + tags) by the repository layer.
 */
data class TaskUi(
    val id: Long,
    val title: String,
    val description: String,
    val dueLabel: String,
    val dueDateMillis: Long,
    val subjectId: Long?,
    val tagName: String,
    @ColorRes val tagColor: Int,
    val tagIds: List<Long> = emptyList(),
    val isDone: Boolean = false
)
