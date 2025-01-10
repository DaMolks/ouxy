package com.damolks.ouxy.ui.dashboard

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.damolks.ouxy.R
import com.damolks.ouxy.databinding.FragmentDashboardBinding
import com.google.android.material.snackbar.Snackbar

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
                    Snackbar.make(binding.root, "Marketplace", Snackbar.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupModulesRecyclerView() {
        modulesAdapter = ModulesAdapter { module ->
            // Action quand un module est cliqué
            Snackbar.make(binding.root, "Module ${module.name} cliqué", Snackbar.LENGTH_SHORT).show()
        }
        binding.modulesRecyclerView.adapter = modulesAdapter
    }

    private fun observeViewModel() {
        viewModel.modules.observe(viewLifecycleOwner) { modules ->
            modulesAdapter.submitList(modules)
            binding.emptyView.visibility = if (modules.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}