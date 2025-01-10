package com.damolks.ouxy.ui.technician

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.saveButton.setOnClickListener {
            viewModel.saveTechnician(
                firstName = binding.firstNameEdit.text.toString(),
                lastName = binding.lastNameEdit.text.toString(),
                sector = binding.sectorEdit.text.toString(),
                identifier = binding.identifierEdit.text.toString(),
                supervisor = binding.supervisorEdit.text.toString().takeIf { it.isNotBlank() }
            )
        }

        viewModel.navigateToHome.observe(viewLifecycleOwner) { shouldNavigate ->
            if (shouldNavigate) {
                findNavController().navigate(
                    TechnicianSetupFragmentDirections.actionTechnicianSetupToDashboard()
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}