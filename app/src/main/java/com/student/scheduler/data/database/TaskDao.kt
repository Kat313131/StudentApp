package com.student.scheduler.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

/**
 * Full CRUD + search/filter/sort for tasks, as required by the assignment.
 * Filtering and sorting are done in SQL so the UI thread never has to
 * post-process large lists.
 */
@Dao
interface TaskDao {

    @Transaction
    @Query(
        """
        SELECT * FROM tasks
        WHERE (:query = '' OR title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%')
          AND (:subjectId IS NULL OR subjectId = :subjectId)
          AND (:onlyPending = 0 OR isDone = 0)
        ORDER BY
          CASE WHEN :sortMode = 0 THEN dueDateMillis END ASC,
          CASE WHEN :sortMode = 1 THEN title END ASC,
          CASE WHEN :sortMode = 2 THEN priority END DESC
        """
    )
    fun observeFiltered(
        query: String,
        subjectId: Long?,
        onlyPending: Boolean,
        sortMode: Int
    ): LiveData<List<TaskWithDetails>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun observeById(taskId: Long): LiveData<TaskWithDetails?>

    @Transaction
    @Query("SELECT * FROM tasks WHERE dueDateMillis BETWEEN :startMillis AND :endMillis ORDER BY dueDateMillis ASC")
    fun observeDueBetween(startMillis: Long, endMillis: Long): LiveData<List<TaskWithDetails>>

    @Transaction
    @Query("SELECT * FROM tasks ORDER BY dueDateMillis ASC")
    suspend fun getAllOnce(): List<TaskWithDetails>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(tasks: List<TaskEntity>)

    @Update
    suspend fun update(task: TaskEntity)

    @Delete
    suspend fun delete(task: TaskEntity)

    @Query("UPDATE tasks SET isDone = :isDone WHERE id = :taskId")
    suspend fun setDone(taskId: Long, isDone: Boolean)
}
