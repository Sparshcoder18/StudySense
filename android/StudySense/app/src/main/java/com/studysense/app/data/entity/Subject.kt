package com.studysense.app.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A subject/course the student is tracking. Attendance, StudySession and
 * Assessment records (later milestones) will each carry a subjectId FK
 * pointing here, with CASCADE delete so removing a subject cleans up its
 * dependent records instead of leaving orphans.
 */
@Entity(
    tableName = "subjects",
    foreignKeys = [
        ForeignKey(
            entity = Student::class,
            parentColumns = ["studentId"],
            childColumns = ["studentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("studentId")]
)
data class Subject(
    @PrimaryKey(autoGenerate = true)
    val subjectId: Long = 0L,
    val studentId: Long,
    val name: String,
    val teacher: String? = null,
    val targetScore: Double? = null
)
