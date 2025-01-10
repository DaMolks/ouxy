package com.damolks.ouxy.ui.marketplace

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.damolks.ouxy.data.model.MarketplaceModule
import kotlinx.coroutines.launch

class MarketplaceViewModel : ViewModel() {

    private val _marketplaceModules = MutableLiveData<List<MarketplaceModule>>()
    val marketplaceModules: LiveData<List<MarketplaceModule>> = _marketplaceModules

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        loadModules()
    }

    private fun loadModules() {
        viewModelScope.launch {
            try {
                _loading.value = true
                // TODO: Implémenter la recherche GitHub
                val modules = searchGitHubModules()
                _marketplaceModules.value = modules
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    private suspend fun searchGitHubModules(): List<MarketplaceModule> {
        // TODO: Implémenter la vraie recherche
        return listOf(
            MarketplaceModule(
                id = "inventory",
                name = "Inventaire",
                description = "Module de gestion d'inventaire pour Ouxy",
                author = "DaMolks",
                version = "1.0.0",
                minAppVersion = "1.0.0",
                stars = 12,
                repoUrl = "https://github.com/DaMolks/ouxy-module-inventory"
            ),
            MarketplaceModule(
                id = "planning",
                name = "Planning",
                description = "Module de gestion du planning des interventions",
                author = "DaMolks",
                version = "1.1.0",
                minAppVersion = "1.0.0",
                stars = 8,
                repoUrl = "https://github.com/DaMolks/ouxy-module-planning"
            )
        )
    }

    fun onModuleSelected(module: MarketplaceModule) {
        // TODO: Navigation vers les détails du module
    }

    fun refreshModules() {
        loadModules()
    }
}