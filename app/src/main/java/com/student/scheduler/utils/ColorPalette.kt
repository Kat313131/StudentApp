package com.student.scheduler.utils

import com.student.scheduler.R

/**
 * Maps the small `colorIndex` stored in [com.student.scheduler.data.database.SubjectEntity]
 * / `TagEntity` to one of the fixed accent colors used across the UI, so the
 * palette stays consistent everywhere a subject or tag is shown.
 */
object ColorPalette {

    private val accentColors = intArrayOf(
        R.color.accent_blue,
        R.color.accent_coral,
        R.color.accent_green
    )

    fun colorRes(index: Int): Int = accentColors[index.mod(accentColors.size)]
}
