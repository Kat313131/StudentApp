package com.student.scheduler.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface LessonDao {

    /** All lessons for a given day of week (0 = Monday), joined with their subject. */
    @Transaction
    @Query("SELECT * FROM lessons WHERE dayOfWeek = :dayOfWeek ORDER BY startTime ASC")
    fun observeByDay(dayOfWeek: Int): LiveData<List<LessonWithSubject>>

    @Transaction
    @Query("SELECT * FROM lessons ORDER BY dayOfWeek ASC, startTime ASC")
    fun observeAll(): LiveData<List<LessonWithSubject>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(lesson: LessonEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(lessons: List<LessonEntity>)
}
