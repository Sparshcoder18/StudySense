package com.studysense.app.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.studysense.app.StudySenseApplication
import com.studysense.app.databinding.FragmentProfileSetupBinding

class ProfileSetupFragment : Fragment() {

    private var _binding: FragmentProfileSetupBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels {
        val app = requireActivity().application as StudySenseApplication
        ProfileViewModelFactory(app.studentRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileSetupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSaveProfile.setOnClickListener {
            viewModel.saveProfile(
                name = binding.inputName.text?.toString().orEmpty(),
                course = binding.inputCourse.text?.toString().orEmpty(),
                semesterText = binding.inputSemester.text?.toString().orEmpty(),
                institution = binding.inputInstitution.text?.toString().orEmpty()
            )
        }

        viewModel.saveResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ProfileSaveResult.Success -> {
                    findNavController().navigate(
                        ProfileSetupFragmentDirections.actionProfileSetupToDashboard()
                    )
                }
                is ProfileSaveResult.Error -> {
                    binding.textError.text = result.message
                    binding.textError.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
