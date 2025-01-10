package com.damolks.ouxy.ui.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.damolks.ouxy.databinding.FragmentSplashBinding

class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.navigationAction.observe(viewLifecycleOwner) { action ->
            when (action) {
                SplashNavigationAction.NavigateToTechnicianSetup -> {
                    findNavController().navigate(
                        SplashFragmentDirections.actionSplashToTechnicianSetup()
                    )
                }
                SplashNavigationAction.NavigateToDashboard -> {
                    findNavController().navigate(
                        SplashFragmentDirections.actionSplashToDashboard()
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}