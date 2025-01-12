package com.damolks.ouxy.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.damolks.ouxy.data.model.Module
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor() : ViewModel() {
    private val _modules = MutableLiveData<List<Module>>()
    val modules: LiveData<List<Module>> = _modules

    init {
        // Module par défaut
        _modules.value = listOf(
            Module(
                id = "reports",
                name = "Rapports",
                description = "Création et gestion des rapports",
                iconResId = android.R.drawable.ic_menu_edit
            )
        )
    }

    fun addDynamicModules(modules: List<Module>) {
        val currentModules = _modules.value?.toMutableList() ?: mutableListOf()
        currentModules.addAll(modules)
        _modules.value = currentModules
    }
}