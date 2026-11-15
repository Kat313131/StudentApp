package com.student.scheduler.model

/** Aggregate numbers shown on the "Статистика" screen. */
data class StatisticsUi(
    val totalTasks: Int,
    val doneTasks: Int,
    val overdueTasks: Int,
    val completionPercent: Int,
    val bySubject: List<SubjectProgress>
)

data class SubjectProgress(
    val subjectName: String,
    val totalTasks: Int,
    val doneTasks: Int,
    val percent: Int,
    val accentColor: Int
)
