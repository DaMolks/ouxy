package com.damolks.ouxy.ui.technician

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TechnicianSetupViewModel @Inject constructor() : ViewModel() {
    private val _navigateToHome = MutableLiveData<Boolean>()
    val navigateToHome: LiveData<Boolean> = _navigateToHome

    fun saveTechnician(firstName: String, lastName: String, sector: String, identifier: String, supervisor: String?) {
        viewModelScope.launch {
            // TODO: Save to Room
            _navigateToHome.value = true
        }
    }
}