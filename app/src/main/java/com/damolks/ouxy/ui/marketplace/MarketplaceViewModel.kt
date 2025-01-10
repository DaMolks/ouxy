package com.damolks.ouxy.ui.marketplace

import androidx.lifecycle.*
import com.damolks.ouxy.data.model.MarketplaceModule
import com.damolks.ouxy.data.repository.MarketplaceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MarketplaceViewModel @Inject constructor(
    private val marketplaceRepository: MarketplaceRepository
) : ViewModel() {

    private val _marketplaceModules = MutableLiveData<List<MarketplaceModule>>()
    val marketplaceModules: LiveData<List<MarketplaceModule>> = _marketplaceModules

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private var currentQuery = "topic:ouxy-module"

    init {
        loadModules()
    }

    fun loadModules() {
        viewModelScope.launch {
            try {
                _loading.value = true
                val modules = marketplaceRepository.searchModules(currentQuery)
                _marketplaceModules.value = modules
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun onModuleSelected(module: MarketplaceModule) {
        // TODO: Navigation vers les détails du module
    }

    fun refreshModules() {
        loadModules()
    }
}