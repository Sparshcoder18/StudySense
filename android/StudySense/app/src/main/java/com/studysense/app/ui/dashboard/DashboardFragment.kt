package com.studysense.app.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.studysense.app.StudySenseApplication
import com.studysense.app.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DashboardViewModel by viewModels {
        val app = requireActivity().application as StudySenseApplication
        DashboardViewModelFactory(app.studentRepository, app.subjectRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            binding.textGreeting.text = if (state.studentName != null) {
                getString(com.studysense.app.R.string.dashboard_greeting, state.studentName)
            } else {
                getString(com.studysense.app.R.string.dashboard_greeting_generic)
            }

            binding.textSubjectCount.text = resources.getQuantityString(
                com.studysense.app.R.plurals.subject_count, state.subjectCount, state.subjectCount
            )
        }

        binding.cardSubjects.setOnClickListener {
            findNavController().navigate(
                DashboardFragmentDirections.actionDashboardToSubjects()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
