package com.damolks.ouxy.ui.technician

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.damolks.ouxy.databinding.FragmentTechnicianSetupBinding

class TechnicianSetupFragment : Fragment() {

    private var _binding: FragmentTechnicianSetupBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TechnicianSetupViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTechnicianSetupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}