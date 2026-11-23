package com.student.scheduler.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Thin wrapper around SharedPreferences for the "Настройки" screen —
 * theme, notification toggles and the default task sort order all need
 * to survive app restarts, so they're never kept only in memory.
 */
class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var darkTheme: Boolean
        get() = prefs.getBoolean(KEY_DARK_THEME, false)
        set(value) = prefs.edit().putBoolean(KEY_DARK_THEME, value).apply()

    var remindLessons: Boolean
        get() = prefs.getBoolean(KEY_REMIND_LESSONS, true)
        set(value) = prefs.edit().putBoolean(KEY_REMIND_LESSONS, value).apply()

    var remindTasks: Boolean
        get() = prefs.getBoolean(KEY_REMIND_TASKS, true)
        set(value) = prefs.edit().putBoolean(KEY_REMIND_TASKS, value).apply()

    var notificationSound: Boolean
        get() = prefs.getBoolean(KEY_SOUND, false)
        set(value) = prefs.edit().putBoolean(KEY_SOUND, value).apply()

    /** 0 = by due date, 1 = by title, 2 = by priority — same encoding as TaskDao's sortMode. */
    var defaultSortMode: Int
        get() = prefs.getInt(KEY_DEFAULT_SORT, 0)
        set(value) = prefs.edit().putInt(KEY_DEFAULT_SORT, value).apply()

    companion object {
        private const val PREFS_NAME = "student_prefs"
        private const val KEY_DARK_THEME = "dark_theme"
        private const val KEY_REMIND_LESSONS = "remind_lessons"
        private const val KEY_REMIND_TASKS = "remind_tasks"
        private const val KEY_SOUND = "notification_sound"
        private const val KEY_DEFAULT_SORT = "default_sort_mode"
    }
}
