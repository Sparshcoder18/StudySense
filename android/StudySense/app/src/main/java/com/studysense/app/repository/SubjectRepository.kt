package com.studysense.app.repository

import com.studysense.app.data.dao.SubjectDao
import com.studysense.app.data.entity.Subject
import kotlinx.coroutines.flow.Flow

/** Thrown when a Subject fails validation before being persisted. */
class InvalidSubjectException(message: String) : Exception(message)

class SubjectRepository(private val subjectDao: SubjectDao) {

    fun observeSubjects(studentId: Long): Flow<List<Subject>> =
        subjectDao.observeSubjects(studentId)

    suspend fun getById(subjectId: Long): Subject? = subjectDao.getById(subjectId)

    /**
     * Validates and saves (inserts or updates) a subject.
     * Validation rules (per SRS input-validation requirements):
     *  - name is required and cannot be blank
     *  - targetScore, if provided, must be within 0..100
     */
    suspend fun saveSubject(subject: Subject): Long {
        validate(subject)
        return if (subject.subjectId == 0L) {
            subjectDao.insert(subject)
        } else {
            subjectDao.update(subject)
            subject.subjectId
        }
    }

    suspend fun deleteSubject(subject: Subject) = subjectDao.delete(subject)

    private fun validate(subject: Subject) {
        if (subject.name.isBlank()) {
            throw InvalidSubjectException("Subject name cannot be blank.")
        }
        subject.targetScore?.let { score ->
            if (score < 0.0 || score > 100.0) {
                throw InvalidSubjectException("Target score must be between 0 and 100.")
            }
        }
    }
}
