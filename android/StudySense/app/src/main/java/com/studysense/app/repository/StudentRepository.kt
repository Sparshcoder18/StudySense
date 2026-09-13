package com.studysense.app.repository

import com.studysense.app.data.dao.StudentDao
import com.studysense.app.data.entity.Student
import kotlinx.coroutines.flow.Flow

/**
 * Isolates the UI/ViewModel layer from Room. Thin on purpose — there's no
 * business logic here yet beyond delegating to the DAO, but it gives us a
 * seam to add validation/caching later without touching ViewModels.
 */
class StudentRepository(private val studentDao: StudentDao) {

    fun observeProfile(): Flow<Student?> = studentDao.observeProfile()

    suspend fun hasProfile(): Boolean = studentDao.hasProfile()

    suspend fun saveProfile(student: Student): Long {
        return if (student.studentId == 0L) {
            studentDao.insert(student)
        } else {
            studentDao.update(student)
            student.studentId
        }
    }
}
