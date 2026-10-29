package com.student.scheduler.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Room database for the app: 5 tables total (Subject, Lesson, Task, Tag —
 * main entities — plus the TaskTagCrossRef junction table), covering both
 * the required 1-to-many and many-to-many relations.
 */
@Database(
    entities = [
        SubjectEntity::class,
        LessonEntity::class,
        TaskEntity::class,
        TagEntity::class,
        TaskTagCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun subjectDao(): SubjectDao
    abstract fun lessonDao(): LessonDao
    abstract fun taskDao(): TaskDao
    abstract fun tagDao(): TagDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Returns the singleton database instance, creating it (and seeding
         * it with demo data on first launch) if needed. [scope] is used to
         * run the one-off seed insert in the background right after the
         * database file is created.
         */
        fun getInstance(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "student.db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // First-ever launch: populate with demo subjects/lessons/tasks
                            // so the UI is never empty out of the box.
                            scope.launch {
                                INSTANCE?.let { DatabaseSeeder.seed(it) }
                            }
                        }
                    })
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
