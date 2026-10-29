package com.student.scheduler.model

import com.student.scheduler.data.database.LessonWithSubject
import com.student.scheduler.data.database.TaskWithDetails
import com.student.scheduler.utils.ColorPalette
import com.student.scheduler.utils.DateFormatting

/** Maps Room join models to the plain UI models the adapters render. */

fun LessonWithSubject.toUi(): LessonUi = LessonUi(
    id = lesson.id,
    subjectName = subject.name,
    timeRange = "${lesson.startTime}–${lesson.endTime}",
    room = lesson.room,
    teacher = subject.teacher,
    lessonType = lesson.lessonType,
    accentColor = ColorPalette.colorRes(subject.colorIndex)
)

fun TaskWithDetails.toUi(): TaskUi {
    val primaryTag = tags.firstOrNull()
    return TaskUi(
        id = task.id,
        title = task.title,
        description = task.description,
        dueLabel = DateFormatting.dueLabel(task.dueDateMillis),
        dueDateMillis = task.dueDateMillis,
        subjectId = task.subjectId,
        tagName = primaryTag?.name ?: subject?.name.orEmpty(),
        tagColor = ColorPalette.colorRes(primaryTag?.colorIndex ?: subject?.colorIndex ?: 0),
        tagIds = tags.map { it.id },
        isDone = task.isDone
    )
}
