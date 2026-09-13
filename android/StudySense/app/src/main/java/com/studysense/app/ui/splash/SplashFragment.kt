package com.studysense.app.ui.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.studysense.app.StudySenseApplication
import com.studysense.app.databinding.FragmentSplashBinding
import kotlinx.coroutines.launch

/**
 * Lightweight startup screen: checks whether a profile already exists and
 * routes accordingly. No animation beyond the default fragment transition -
 * per spec, the splash should not linger.
 */
class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val app = requireActivity().application as StudySenseApplication

        viewLifecycleOwner.lifecycleScope.launch {
            val hasProfile = app.studentRepository.hasProfile()
            val destination = if (hasProfile) {
                SplashFragmentDirections.actionSplashToDashboard()
            } else {
                SplashFragmentDirections.actionSplashToProfileSetup()
            }
            findNavController().navigate(destination)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
