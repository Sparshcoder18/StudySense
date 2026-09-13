package com.studysense.app.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import com.studysense.app.data.entity.Student
import com.studysense.app.data.entity.Subject
import com.studysense.app.repository.StudentRepository
import com.studysense.app.repository.SubjectRepository

/**
 * Dashboard state. Performance/attendance/study-hours are left out until
 * Milestones 4-7 (Attendance/Study/Assessment/Analytics) exist to compute
 * them from real records - the UI renders an explicit empty state for each
 * rather than a fabricated number.
 */
data class DashboardUiState(
    val studentName: String? = null,
    val subjectCount: Int = 0,
    val hasProfile: Boolean = false
)

class DashboardViewModel(
    studentRepository: StudentRepository,
    subjectRepository: SubjectRepository
) : ViewModel() {

    private val studentLiveData: LiveData<Student?> = studentRepository.observeProfile().asLiveData()

    // Re-subscribes to the subjects query whenever the active student changes.
    private val subjectsLiveData: LiveData<List<Subject>> =
        studentLiveData.switchMap { student ->
            if (student == null) {
                val empty = androidx.lifecycle.MutableLiveData<List<Subject>>()
                empty.value = emptyList()
                empty
            } else {
                subjectRepository.observeSubjects(student.studentId).asLiveData()
            }
        }

    val uiState: LiveData<DashboardUiState> = MediatorLiveData<DashboardUiState>().apply {
        fun recompute() {
            value = DashboardUiState(
                studentName = studentLiveData.value?.name,
                subjectCount = subjectsLiveData.value?.size ?: 0,
                hasProfile = studentLiveData.value != null
            )
        }
        addSource(studentLiveData) { recompute() }
        addSource(subjectsLiveData) { recompute() }
    }
}

class DashboardViewModelFactory(
    private val studentRepository: StudentRepository,
    private val subjectRepository: SubjectRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            return DashboardViewModel(studentRepository, subjectRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}
