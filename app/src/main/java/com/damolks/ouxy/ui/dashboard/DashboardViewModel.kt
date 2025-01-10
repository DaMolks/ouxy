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
        val installedModules = listOf(
            Module(
                id = "reports",
                name = "Rapports",
                description = "Création et gestion des rapports d'intervention",
                iconResId = R.drawable.ic_module_reports
            )
        )
        _modules.value = installedModules
    }
}