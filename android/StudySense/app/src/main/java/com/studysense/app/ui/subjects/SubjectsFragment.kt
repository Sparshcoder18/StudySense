package com.studysense.app.ui.subjects

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.studysense.app.StudySenseApplication
import com.studysense.app.databinding.FragmentSubjectsBinding

class SubjectsFragment : Fragment() {

    private var _binding: FragmentSubjectsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SubjectViewModel by viewModels {
        val app = requireActivity().application as StudySenseApplication
        SubjectViewModelFactory(app.studentRepository, app.subjectRepository)
    }

    private lateinit var adapter: SubjectAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSubjectsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = SubjectAdapter(
            onClick = { subject ->
                findNavController().navigate(
                    SubjectsFragmentDirections.actionSubjectsToAddEditSubject(subject.subjectId)
                )
            },
            onDeleteClick = { subject -> viewModel.deleteSubject(subject) }
        )

        binding.recyclerSubjects.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerSubjects.adapter = adapter

        viewModel.subjects.observe(viewLifecycleOwner) { subjects ->
            adapter.submitList(subjects)
            val isEmpty = subjects.isEmpty()
            binding.recyclerSubjects.visibility = if (isEmpty) View.GONE else View.VISIBLE
            binding.emptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
        }

        binding.fabAddSubject.setOnClickListener {
            findNavController().navigate(
                SubjectsFragmentDirections.actionSubjectsToAddEditSubject(subjectId = 0L)
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
