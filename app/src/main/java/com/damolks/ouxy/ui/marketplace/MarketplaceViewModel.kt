package com.damolks.ouxy.ui.marketplace

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.damolks.ouxy.data.model.MarketplaceModule
import com.damolks.ouxy.data.repository.MarketplaceRepository
import com.damolks.ouxy.data.service.ModuleInstallService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MarketplaceViewModel @Inject constructor(
    private val marketplaceRepository: MarketplaceRepository,
    private val moduleInstallService: ModuleInstallService
) : ViewModel() {

    private val _marketplaceModules = MutableLiveData<List<MarketplaceModule>>()
    val marketplaceModules: LiveData<List<MarketplaceModule>> = _marketplaceModules

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _moduleInstallStates = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val moduleInstallStates: StateFlow<Map<String, Boolean>> = _moduleInstallStates

    init {
        loadModules()
    }

    fun loadModules() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _loading.postValue(true)
                val modules = marketplaceRepository.searchModules()
                _marketplaceModules.postValue(modules)
                
                val states = modules.associate { module ->
                    module.id to moduleInstallService.isModuleInstalled(module.id)
                }
                _moduleInstallStates.update { states }
                
                if (modules.isEmpty()) {
                    _error.postValue("Aucun module trouvé. Vérifiez que vos repos ont le tag 'ouxy-module'")
                } else {
                    _error.postValue(null)
                }
            } catch (e: Exception) {
                _error.postValue("Erreur: ${e.message}")
            } finally {
                _loading.postValue(false)
            }
        }
    }

    fun installModule(module: MarketplaceModule) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("ModuleInstall", "Début de l'installation du module: ${module.id}")
                _loading.postValue(true)
                
                Log.d("ModuleInstall", "Téléchargement du manifest")
                val manifestResult = moduleInstallService.installModule(module)
                
                Log.d("ModuleInstall", "Mise à jour des états de module")
                _moduleInstallStates.update { states ->
                    states + (module.id to true)
                }
                
                Log.d("ModuleInstall", "Module installé avec succès")
                _error.postValue("Module ${module.name} installé avec succès")
            } catch (e: Exception) {
                Log.e("ModuleInstall", "Erreur lors de l'installation", e)
                _error.postValue("Erreur lors de l'installation: ${e.localizedMessage}")
            } finally {
                _loading.postValue(false)
            }
        }
    }

    fun uninstallModule(module: MarketplaceModule) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _loading.postValue(true)
                moduleInstallService.uninstallModule(module.id)
                _moduleInstallStates.update { states ->
                    states + (module.id to false)
                }
                _error.postValue("Module ${module.name} désinstallé avec succès")
            } catch (e: Exception) {
                _error.postValue("Erreur lors de la désinstallation: ${e.message}")
            } finally {
                _loading.postValue(false)
            }
        }
    }

    fun refreshModules() {
        loadModules()
    }
}