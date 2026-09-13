package com.studysense.app.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studysense.app.data.entity.Student
import com.studysense.app.repository.StudentRepository
import kotlinx.coroutines.launch

sealed class ProfileSaveResult {
    data class Success(val studentId: Long) : ProfileSaveResult()
    data class Error(val message: String) : ProfileSaveResult()
}

class ProfileViewModel(private val repository: StudentRepository) : ViewModel() {

    private val _saveResult = MutableLiveData<ProfileSaveResult>()
    val saveResult: LiveData<ProfileSaveResult> = _saveResult

    fun saveProfile(name: String, course: String, semesterText: String, institution: String) {
        val trimmedName = name.trim()
        val trimmedCourse = course.trim()
        val trimmedInstitution = institution.trim()

        if (trimmedName.isBlank()) {
            _saveResult.value = ProfileSaveResult.Error("Name is required.")
            return
        }
        if (trimmedCourse.isBlank()) {
            _saveResult.value = ProfileSaveResult.Error("Course is required.")
            return
        }
        if (trimmedInstitution.isBlank()) {
            _saveResult.value = ProfileSaveResult.Error("Institution is required.")
            return
        }
        val semester = semesterText.trim().toIntOrNull()
        if (semester == null || semester <= 0) {
            _saveResult.value = ProfileSaveResult.Error("Enter a valid semester number.")
            return
        }

        viewModelScope.launch {
            val id = repository.saveProfile(
                Student(
                    name = trimmedName,
                    course = trimmedCourse,
                    semester = semester,
                    institution = trimmedInstitution
                )
            )
            _saveResult.value = ProfileSaveResult.Success(id)
        }
    }
}

class ProfileViewModelFactory(
    private val repository: StudentRepository
) : androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}
