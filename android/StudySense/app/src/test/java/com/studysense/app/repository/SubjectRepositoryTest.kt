package com.studysense.app.repository

import com.studysense.app.data.dao.SubjectDao
import com.studysense.app.data.entity.Subject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

/**
 * Fake DAO so these tests exercise SubjectRepository's validation logic
 * without touching Room/SQLite - fast, no instrumentation required.
 */
private class FakeSubjectDao : SubjectDao {
    val inserted = mutableListOf<Subject>()
    var nextId = 1L

    override suspend fun insert(subject: Subject): Long {
        inserted.add(subject)
        return nextId++
    }

    override suspend fun update(subject: Subject) {
        inserted.add(subject)
    }

    override suspend fun delete(subject: Subject) {
        inserted.remove(subject)
    }

    override fun observeSubjects(studentId: Long): Flow<List<Subject>> = flowOf(inserted)

    override suspend fun getById(subjectId: Long): Subject? =
        inserted.find { it.subjectId == subjectId }

    override suspend fun countForStudent(studentId: Long): Int =
        inserted.count { it.studentId == studentId }
}

class SubjectRepositoryTest {

    private fun repository(): Pair<SubjectRepository, FakeSubjectDao> {
        val dao = FakeSubjectDao()
        return SubjectRepository(dao) to dao
    }

    @Test
    fun `saving a subject with a blank name is rejected`() = runBlocking {
        val (repo, _) = repository()
        try {
            repo.saveSubject(Subject(studentId = 1L, name = "   "))
            fail("Expected InvalidSubjectException")
        } catch (e: InvalidSubjectException) {
            assertTrue(e.message!!.contains("blank"))
        }
    }

    @Test
    fun `saving a subject with a valid name succeeds`() = runBlocking {
        val (repo, dao) = repository()
        repo.saveSubject(Subject(studentId = 1L, name = "Machine Learning"))
        assertEquals(1, dao.inserted.size)
        assertEquals("Machine Learning", dao.inserted.first().name)
    }

    @Test
    fun `target score above 100 is rejected`() = runBlocking {
        val (repo, _) = repository()
        try {
            repo.saveSubject(Subject(studentId = 1L, name = "DSA", targetScore = 150.0))
            fail("Expected InvalidSubjectException")
        } catch (e: InvalidSubjectException) {
            assertTrue(e.message!!.contains("0 and 100"))
        }
    }

    @Test
    fun `target score below 0 is rejected`() = runBlocking {
        val (repo, _) = repository()
        try {
            repo.saveSubject(Subject(studentId = 1L, name = "DSA", targetScore = -5.0))
            fail("Expected InvalidSubjectException")
        } catch (e: InvalidSubjectException) {
            assertTrue(e.message!!.contains("0 and 100"))
        }
    }

    @Test
    fun `target score within range is accepted`() = runBlocking {
        val (repo, dao) = repository()
        repo.saveSubject(Subject(studentId = 1L, name = "DSA", targetScore = 85.0))
        assertEquals(1, dao.inserted.size)
    }

    @Test
    fun `null target score is accepted since it is optional`() = runBlocking {
        val (repo, dao) = repository()
        repo.saveSubject(Subject(studentId = 1L, name = "DSA", targetScore = null))
        assertEquals(1, dao.inserted.size)
    }
}
