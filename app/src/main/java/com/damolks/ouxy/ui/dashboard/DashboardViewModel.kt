package com.damolks.ouxy.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.damolks.ouxy.R
import com.damolks.ouxy.data.model.Module

class DashboardViewModel : ViewModel() {

    private val _modules = MutableLiveData<List<Module>>()
    val modules: LiveData<List<Module>> = _modules

    init {
        loadModules()
    }

    private fun loadModules() {
        // Modules de test
        val testModules = listOf(
            Module(
                id = "reports",
                name = "Rapports",
                description = "Création et gestion des rapports d'intervention",
                version = "1.0",
                iconResId = R.drawable.ic_module_reports,
                isInstalled = true
            ),
            Module(
                id = "planning",
                name = "Planning",
                description = "Gestion des rendez-vous et interventions",
                version = "1.0",
                iconResId = R.drawable.ic_module_planning,
                isInstalled = false
            ),
            Module(
                id = "inventory",
                name = "Inventaire",
                description = "Gestion du matériel et des pièces",
                version = "1.0",
                iconResId = R.drawable.ic_module_inventory,
                isInstalled = false,
                isEnabled = false
            )
        )
        _modules.value = testModules
    }
}