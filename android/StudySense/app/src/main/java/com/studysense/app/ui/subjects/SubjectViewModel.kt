package com.studysense.app.ui.subjects

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.studysense.app.data.entity.Subject
import com.studysense.app.repository.InvalidSubjectException
import com.studysense.app.repository.StudentRepository
import com.studysense.app.repository.SubjectRepository
import kotlinx.coroutines.launch

sealed class SubjectSaveResult {
    object Success : SubjectSaveResult()
    data class Error(val message: String) : SubjectSaveResult()
}

/**
 * MVP is single-profile (no auth/multi-user), so the ViewModel resolves the
 * active studentId internally from StudentRepository rather than requiring
 * callers to know it up front - subjects simply appear empty until a profile
 * exists.
 */
class SubjectViewModel(
    private val studentRepository: StudentRepository,
    private val subjectRepository: SubjectRepository
) : ViewModel() {

    private val studentIdLiveData: LiveData<Long?> =
        studentRepository.observeProfile().asLiveData().map { it?.studentId }

    val subjects: LiveData<List<Subject>> =
        studentIdLiveData.switchMap { studentId ->
            if (studentId == null) {
                val empty = MutableLiveData<List<Subject>>()
                empty.value = emptyList()
                empty
            } else {
                subjectRepository.observeSubjects(studentId).asLiveData()
            }
        }

    private val _saveResult = MutableLiveData<SubjectSaveResult>()
    val saveResult: LiveData<SubjectSaveResult> = _saveResult

    fun saveSubject(subjectId: Long, name: String, teacher: String, targetScoreText: String) {
        val targetScore = targetScoreText.trim().let { if (it.isEmpty()) null else it.toDoubleOrNull() }
        if (targetScoreText.isNotBlank() && targetScore == null) {
            _saveResult.value = SubjectSaveResult.Error("Target score must be a number.")
            return
        }

        viewModelScope.launch {
            val studentId = studentIdLiveData.value
            if (studentId == null) {
                _saveResult.value = SubjectSaveResult.Error("Set up your profile before adding subjects.")
                return@launch
            }
            try {
                subjectRepository.saveSubject(
                    Subject(
                        subjectId = subjectId,
                        studentId = studentId,
                        name = name.trim(),
                        teacher = teacher.trim().ifBlank { null },
                        targetScore = targetScore
                    )
                )
                _saveResult.value = SubjectSaveResult.Success
            } catch (e: InvalidSubjectException) {
                _saveResult.value = SubjectSaveResult.Error(e.message ?: "Invalid subject.")
            }
        }
    }

    fun deleteSubject(subject: Subject) {
        viewModelScope.launch {
            subjectRepository.deleteSubject(subject)
        }
    }
}

class SubjectViewModelFactory(
    private val studentRepository: StudentRepository,
    private val subjectRepository: SubjectRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SubjectViewModel::class.java)) {
            return SubjectViewModel(studentRepository, subjectRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}
