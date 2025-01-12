package com.damolks.ouxy.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.damolks.ouxy.R
import com.damolks.ouxy.databinding.FragmentDashboardBinding
import com.damolks.ouxy.module.ModuleClassLoader
import com.damolks.ouxy.module.OuxyModule
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var modulesAdapter: ModulesAdapter
    private val moduleList = mutableListOf<OuxyModule>()

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
        loadModules()
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
                    findNavController().navigate(DashboardFragmentDirections.actionDashboardToMarketplace())
                    true
                }
                else -> false
            }
        }
    }

    private fun setupModulesRecyclerView() {
        modulesAdapter = ModulesAdapter { module ->
            Snackbar.make(binding.root, "Module ${module::class.simpleName} cliqué", Snackbar.LENGTH_SHORT).show()
        }
        binding.modulesRecyclerView.adapter = modulesAdapter
    }

    private fun loadModules() {
        val modulesFolder = File(requireContext().filesDir, "modules")
        modulesFolder.mkdirs() // Crée le dossier s'il n'existe pas

        modulesFolder.listFiles { file -> file.extension == "jar" }?.forEach { moduleFile ->
            try {
                val classLoader = ModuleClassLoader(moduleFile, javaClass.classLoader!!)
                // Note : Remplacez par le nom de classe de votre module si différent
                val module = classLoader.loadModuleClass("com.damolks.ouxy.modules.notes.NotesModule")
                moduleList.add(module)
            } catch (e: Exception) {
                Log.e("ModuleLoader", "Erreur de chargement du module", e)
            }
        }

        modulesAdapter.submitList(moduleList)
    }

    private fun observeViewModel() {
        viewModel.modules.observe(viewLifecycleOwner) { modules ->
            // Combine les modules du ViewModel avec les modules chargés dynamiquement
            val combinedModules = modules + moduleList
            modulesAdapter.submitList(combinedModules)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}