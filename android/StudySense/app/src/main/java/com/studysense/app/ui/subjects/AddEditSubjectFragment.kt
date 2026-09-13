package com.studysense.app.ui.subjects

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.studysense.app.StudySenseApplication
import com.studysense.app.databinding.FragmentAddEditSubjectBinding
import kotlinx.coroutines.launch

class AddEditSubjectFragment : Fragment() {

    private var _binding: FragmentAddEditSubjectBinding? = null
    private val binding get() = _binding!!

    private val args: AddEditSubjectFragmentArgs by navArgs()

    private val viewModel: SubjectViewModel by viewModels {
        val app = requireActivity().application as StudySenseApplication
        SubjectViewModelFactory(app.studentRepository, app.subjectRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditSubjectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val isEditing = args.subjectId != 0L
        binding.textTitle.text = if (isEditing) "Edit subject" else "Add subject"

        if (isEditing) {
            val app = requireActivity().application as StudySenseApplication
            viewLifecycleOwner.lifecycleScope.launch {
                val subject = app.subjectRepository.getById(args.subjectId)
                subject?.let {
                    binding.inputSubjectName.setText(it.name)
                    binding.inputTeacher.setText(it.teacher.orEmpty())
                    binding.inputTargetScore.setText(it.targetScore?.toString().orEmpty())
                }
            }
        }

        binding.buttonSaveSubject.setOnClickListener {
            viewModel.saveSubject(
                subjectId = args.subjectId,
                name = binding.inputSubjectName.text?.toString().orEmpty(),
                teacher = binding.inputTeacher.text?.toString().orEmpty(),
                targetScoreText = binding.inputTargetScore.text?.toString().orEmpty()
            )
        }

        viewModel.saveResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is SubjectSaveResult.Success -> findNavController().popBackStack()
                is SubjectSaveResult.Error -> {
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
