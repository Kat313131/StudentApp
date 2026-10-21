package com.student.scheduler.model

/**
 * One item in the horizontal week-day selector at the top of the
 * Home / Schedule screens (Пн 11, Вт 12, ...).
 */
data class WeekDay(
    val shortName: String,
    val dayNumber: Int,
    val isSelected: Boolean
)
