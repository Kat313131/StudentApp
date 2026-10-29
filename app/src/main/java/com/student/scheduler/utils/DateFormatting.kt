package com.student.scheduler.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/** Formatting helpers shared by Home / Schedule / Tasks screens. */
object DateFormatting {

    val WEEK_DAY_SHORT_NAMES = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб")
    val WEEK_DAY_FULL_NAMES = listOf("Понедельник", "Вторник", "Среду", "Четверг", "Пятницу", "Субботу")

    private val dayMonthFormat = SimpleDateFormat("d MMMM", Locale("ru"))

    /** 0 = Monday ... 5 = Saturday for a given calendar, or -1 for Sunday (no classes). */
    fun dayOfWeekIndex(calendar: Calendar): Int {
        // Calendar.DAY_OF_WEEK: Sunday = 1, Monday = 2, ... Saturday = 7
        val raw = calendar.get(Calendar.DAY_OF_WEEK)
        return if (raw == Calendar.SUNDAY) -1 else raw - 2
    }

    fun startOfDay(calendar: Calendar): Long {
        val c = calendar.clone() as Calendar
        c.set(Calendar.HOUR_OF_DAY, 0)
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        c.set(Calendar.MILLISECOND, 0)
        return c.timeInMillis
    }

    /** Human-friendly deadline label: "Срок: Сегодня, 18:00" / "Срок: Завтра" / "Срок: 16 ноября". */
    fun dueLabel(dueMillis: Long): String {
        val now = Calendar.getInstance()
        val today = startOfDay(now)
        val tomorrow = today + DAY_MILLIS
        val dayAfterTomorrow = tomorrow + DAY_MILLIS

        val dueCalendar = Calendar.getInstance().apply { timeInMillis = dueMillis }
        val time = SimpleDateFormat("HH:mm", Locale("ru")).format(dueCalendar.time)

        return when {
            dueMillis < today -> "Просрочено — ${dayMonthFormat.format(dueCalendar.time)}"
            dueMillis < tomorrow -> "Срок: Сегодня, $time"
            dueMillis < dayAfterTomorrow -> "Срок: Завтра, $time"
            else -> "Срок: ${dayMonthFormat.format(dueCalendar.time)}"
        }
    }

    const val DAY_MILLIS = 24L * 60 * 60 * 1000
}
