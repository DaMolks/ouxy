package com.damolks.ouxy.ui.technician

import android.app.Application
import androidx.lifecycle.*
import com.damolks.ouxy.OuxyApplication
import com.damolks.ouxy.data.model.Technician
import kotlinx.coroutines.launch

class TechnicianSetupViewModel(application: Application) : AndroidViewModel(application) {

    private val technicianDao = (application as OuxyApplication).database.technicianDao()

    private val _navigateToHome = MutableLiveData<Boolean>()
    val navigateToHome: LiveData<Boolean> = _navigateToHome

    fun saveTechnician(firstName: String, lastName: String, sector: String, identifier: String, supervisor: String?) {
        if(validateInput(firstName, lastName, sector, identifier)) {
            viewModelScope.launch {
                val technician = Technician(
                    firstName = firstName.trim(),
                    lastName = lastName.trim(),
                    sector = sector.trim(),
                    identifier = identifier.trim(),
                    supervisor = supervisor?.trim()
                )
                technicianDao.insertTechnician(technician)
                _navigateToHome.value = true
            }
        }
    }

    private fun validateInput(firstName: String, lastName: String, sector: String, identifier: String): Boolean {
        return firstName.isNotBlank() && 
               lastName.isNotBlank() && 
               sector.isNotBlank() && 
               identifier.isNotBlank()
    }
}