package com.studysense.app

import android.app.Application
import com.studysense.app.data.database.StudySenseDatabase
import com.studysense.app.repository.StudentRepository
import com.studysense.app.repository.SubjectRepository

/**
 * Application class. Holds process-wide singletons (database, repositories).
 *
 * Repositories are constructed lazily so the database is only opened once it's
 * actually needed, and every ViewModel in the app can share the same instances
 * instead of each opening its own Room connection.
 */
class StudySenseApplication : Application() {

    val database: StudySenseDatabase by lazy {
        StudySenseDatabase.getInstance(this)
    }

    val studentRepository: StudentRepository by lazy {
        StudentRepository(database.studentDao())
    }

    val subjectRepository: SubjectRepository by lazy {
        SubjectRepository(database.subjectDao())
    }
}
