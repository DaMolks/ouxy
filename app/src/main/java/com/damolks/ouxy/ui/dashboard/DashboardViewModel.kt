package com.damolks.ouxy.ui.dashboard

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.damolks.ouxy.R
import com.damolks.ouxy.data.model.Module
import com.damolks.ouxy.module.ModuleClassLoader
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _modules = MutableLiveData<List<Module>>()
    val modules: LiveData<List<Module>> = _modules

    init {
        discoverModules()
    }

    private fun discoverModules() {
        viewModelScope.launch {
            val discoveredModules = findModulesInStorage()
            _modules.value = discoveredModules
        }
    }

    private suspend fun findModulesInStorage(): List<Module> = withContext(Dispatchers.IO) {
        // Chemins potentiels pour rechercher les modules
        val modulePaths = listOf(
            File(context.filesDir, "modules"),
            File(context.getExternalFilesDir(null), "modules"),
            File(context.cacheDir, "modules")
        )

        val discoveredModules = mutableListOf<Module>()

        modulePaths.forEach { modulesDir ->
            modulesDir.mkdirs()
            modulesDir.listFiles { file -> file.extension == "jar" }?.forEach { moduleFile ->
                try {
                    val classLoader = ModuleClassLoader(moduleFile, javaClass.classLoader!!)
                    // Adaptez le nom de classe selon votre implémentation
                    val ouxyModule = classLoader.loadModuleClass("com.damolks.ouxy.modules.notes.NotesModule")
                    
                    val module = Module(
                        id = ouxyModule::class.simpleName?.lowercase() ?: "unknown_module",
                        name = determineModuleName(ouxyModule),
                        description = "Module dynamique",
                        iconResId = determineModuleIcon(ouxyModule)
                    )
                    
                    discoveredModules.add(module)
                } catch (e: Exception) {
                    Log.e("ModuleLoader", "Erreur de chargement du module", e)
                }
            }
        }

        return@withContext discoveredModules
    }

    private fun determineModuleName(module: Any): String {
        return when (module::class.simpleName) {
            "NotesModule" -> "Notes"
            else -> module::class.simpleName ?: "Module Inconnu"
        }
    }

    private fun determineModuleIcon(module: Any): Int {
        return when (module::class.simpleName) {
            "NotesModule" -> R.drawable.ic_notes
            else -> android.R.drawable.ic_dialog_info
        }
    }
}