package com.studysense.app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.studysense.app.data.entity.Subject
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(subject: Subject): Long

    @Update
    suspend fun update(subject: Subject)

    @Delete
    suspend fun delete(subject: Subject)

    @Query("SELECT * FROM subjects WHERE studentId = :studentId ORDER BY name ASC")
    fun observeSubjects(studentId: Long): Flow<List<Subject>>

    @Query("SELECT * FROM subjects WHERE subjectId = :subjectId")
    suspend fun getById(subjectId: Long): Subject?

    @Query("SELECT COUNT(*) FROM subjects WHERE studentId = :studentId")
    suspend fun countForStudent(studentId: Long): Int
}
