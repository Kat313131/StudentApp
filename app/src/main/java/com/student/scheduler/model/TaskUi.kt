package com.student.scheduler.model

import androidx.annotation.ColorRes

/**
 * UI model for a single task card (Home / Tasks screens).
 *
 * TEMPORARY mock data — from week 2 onwards this maps to the Room
 * `Task` entity (with its linked Subject tag) via the repository.
 */
data class TaskUi(
    val title: String,
    val dueLabel: String,
    val tagName: String,
    @ColorRes val tagColor: Int,
    val isDone: Boolean = false
)
