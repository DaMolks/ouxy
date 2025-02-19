package com.damolks.ouxy.ui.marketplace

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.damolks.ouxy.R
import com.damolks.ouxy.databinding.FragmentMarketplaceBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MarketplaceFragment : Fragment() {

    private var _binding: FragmentMarketplaceBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MarketplaceViewModel by viewModels()
    private lateinit var modulesAdapter: MarketplaceModulesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMarketplaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_refresh -> {
                    viewModel.refreshModules()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupRecyclerView() {
        modulesAdapter = MarketplaceModulesAdapter { module, isInstalled ->
            if (isInstalled) {
                viewModel.uninstallModule(module)
            } else {
                viewModel.installModule(module)
            }
        }
        binding.recyclerView.adapter = modulesAdapter
    }

    private fun observeViewModel() {
        viewModel.marketplaceModules.observe(viewLifecycleOwner) { modules ->
            modulesAdapter.submitList(modules)
            binding.emptyView.visibility = if (modules.isEmpty()) View.VISIBLE else View.GONE
            binding.recyclerView.visibility = if (modules.isEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
            }
        }

        // Observer les états d'installation
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.moduleInstallStates.collectLatest { states ->
                modulesAdapter.updateInstallStates(states)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}