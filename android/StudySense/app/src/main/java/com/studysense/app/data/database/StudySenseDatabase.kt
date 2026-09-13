package com.studysense.app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.studysense.app.data.dao.StudentDao
import com.studysense.app.data.dao.SubjectDao
import com.studysense.app.data.entity.Student
import com.studysense.app.data.entity.Subject

/**
 * Milestone 3 schema: Student + Subject only. Attendance, StudySession,
 * Assessment and Prediction entities are added in later milestones — adding
 * them now, unused, would just be dead schema to migrate around later.
 */
@Database(
    entities = [Student::class, Subject::class],
    version = 1,
    exportSchema = true
)
abstract class StudySenseDatabase : RoomDatabase() {

    abstract fun studentDao(): StudentDao
    abstract fun subjectDao(): SubjectDao

    companion object {
        private const val DB_NAME = "studysense.db"

        @Volatile
        private var INSTANCE: StudySenseDatabase? = null

        fun getInstance(context: Context): StudySenseDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    StudySenseDatabase::class.java,
                    DB_NAME
                ).build().also { INSTANCE = it }
            }
        }
    }
}
