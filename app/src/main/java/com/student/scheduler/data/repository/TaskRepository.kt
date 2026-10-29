package com.student.scheduler.data.repository

import androidx.lifecycle.LiveData
import com.student.scheduler.data.database.TagDao
import com.student.scheduler.data.database.TagEntity
import com.student.scheduler.data.database.TaskDao
import com.student.scheduler.data.database.TaskEntity
import com.student.scheduler.data.database.TaskTagCrossRef
import com.student.scheduler.data.database.TaskWithDetails

/**
 * Single access point for tasks and tags. Implements full CRUD plus the
 * search / filter / sort query used by the Tasks screen (week 8) and the
 * "due soon" query used by Home (week 4) and the reminder worker (week 11).
 */
class TaskRepository(
    private val taskDao: TaskDao,
    private val tagDao: TagDao
) {
    fun observeTasks(
        query: String = "",
        subjectId: Long? = null,
        onlyPending: Boolean = false,
        sortMode: Int = 0
    ): LiveData<List<TaskWithDetails>> =
        taskDao.observeFiltered(query, subjectId, onlyPending, sortMode)

    fun observeTask(taskId: Long): LiveData<TaskWithDetails?> = taskDao.observeById(taskId)

    fun observeDueBetween(startMillis: Long, endMillis: Long): LiveData<List<TaskWithDetails>> =
        taskDao.observeDueBetween(startMillis, endMillis)

    fun observeTags(): LiveData<List<TagEntity>> = tagDao.observeAll()

    suspend fun getAllTasksOnce(): List<TaskWithDetails> = taskDao.getAllOnce()

    suspend fun getTags(): List<TagEntity> = tagDao.getAll()

    /** Creates or updates a task, and rewrites its tag links atomically. */
    suspend fun saveTask(task: TaskEntity, tagIds: List<Long>): Long {
        val id = taskDao.insert(task)
        val effectiveId = if (task.id != 0L) task.id else id
        tagDao.clearTagsForTask(effectiveId)
        if (tagIds.isNotEmpty()) {
            tagDao.addTagsToTask(tagIds.map { TaskTagCrossRef(effectiveId, it) })
        }
        return effectiveId
    }

    suspend fun deleteTask(task: TaskEntity) = taskDao.delete(task)

    suspend fun setDone(taskId: Long, isDone: Boolean) = taskDao.setDone(taskId, isDone)

    suspend fun createTag(name: String, colorIndex: Int): Long =
        tagDao.insert(TagEntity(name = name, colorIndex = colorIndex))
}
