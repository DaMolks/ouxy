package com.damolks.ouxy.ui.dashboard

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.damolks.ouxy.R
import com.damolks.ouxy.databinding.FragmentDashboardBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var modulesAdapter: ModulesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupModulesRecyclerView()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.inflateMenu(R.menu.dashboard_menu)
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_settings -> {
                    Snackbar.make(binding.root, "Paramètres", Snackbar.LENGTH_SHORT).show()
                    true
                }
                R.id.action_sites -> {
                    Snackbar.make(binding.root, "Sites", Snackbar.LENGTH_SHORT).show()
                    true
                }
                R.id.action_marketplace -> {
                    findNavController().navigate(R.id.action_dashboard_to_marketplace)
                    true
                }
                else -> false
            }
        }
    }

    private fun setupModulesRecyclerView() {
        modulesAdapter = ModulesAdapter { module ->
            Snackbar.make(binding.root, "Module ${module.name} cliqué", Snackbar.LENGTH_SHORT).show()
        }
        binding.modulesRecyclerView.adapter = modulesAdapter
    }

    private fun observeViewModel() {
        viewModel.modules.observe(viewLifecycleOwner) { modules ->
            modulesAdapter.submitList(modules)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}