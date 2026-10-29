package com.student.scheduler.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Main entity: a study subject (e.g. "Математический анализ").
 * One Subject can have many Lessons and many Tasks (1-to-many).
 */
@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val teacher: String,
    /** Index into the fixed accent color palette used across the UI (0, 1, 2, ...). */
    val colorIndex: Int
)
