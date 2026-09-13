package com.studysense.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.studysense.app.data.entity.Student
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(student: Student): Long

    @Update
    suspend fun update(student: Student)

    @Query("SELECT * FROM students LIMIT 1")
    fun observeProfile(): Flow<Student?>

    @Query("SELECT * FROM students LIMIT 1")
    suspend fun getProfileOnce(): Student?

    @Query("SELECT EXISTS(SELECT 1 FROM students LIMIT 1)")
    suspend fun hasProfile(): Boolean
}
