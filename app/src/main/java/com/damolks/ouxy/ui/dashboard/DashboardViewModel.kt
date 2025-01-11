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
        // TODO: Load installed modules
        _modules.value = listOf(
            Module(
                id = "reports",
                name = "Rapports",
                description = "Création et gestion des rapports",
                iconResId = android.R.drawable.ic_menu_edit
            )
        )
    }
}