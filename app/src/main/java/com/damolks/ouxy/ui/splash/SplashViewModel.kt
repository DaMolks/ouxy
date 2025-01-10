package com.damolks.ouxy.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {

    private val _navigationAction = MutableLiveData<SplashNavigationAction>()
    val navigationAction: LiveData<SplashNavigationAction> = _navigationAction

    init {
        checkTechnicianProfile()
    }

    private fun checkTechnicianProfile() {
        viewModelScope.launch {
            delay(1500) // Délai pour l'effet visuel
            // TODO: Vérifier si un technicien existe
            _navigationAction.value = SplashNavigationAction.NavigateToTechnicianSetup
        }
    }
}

sealed class SplashNavigationAction {
    object NavigateToTechnicianSetup : SplashNavigationAction()
    object NavigateToDashboard : SplashNavigationAction()
}