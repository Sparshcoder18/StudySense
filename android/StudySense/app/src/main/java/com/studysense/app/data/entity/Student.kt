package com.studysense.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Local student profile. StudySense is single-profile for the MVP (no auth),
 * but the table is still keyed so Subject/Attendance/etc. can carry a proper
 * foreign key rather than assuming a single implicit row.
 */
@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true)
    val studentId: Long = 0L,
    val name: String,
    val course: String,
    val semester: Int,
    val institution: String
)
