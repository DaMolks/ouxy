package com.damolks.ouxy.data.service

import android.content.Context
import com.damolks.ouxy.data.api.GitHubApi
import com.damolks.ouxy.data.dao.ModuleDao
import com.damolks.ouxy.data.model.InstalledModule
import com.damolks.ouxy.data.model.MarketplaceModule
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ModuleInstallService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gitHubApi: GitHubApi,
    private val moduleDao: ModuleDao
) {

    suspend fun installModule(module: MarketplaceModule) {
        withContext(Dispatchers.IO) {
            // 1. Créer le dossier pour le module
            val moduleDir = context.getDir("modules", Context.MODE_PRIVATE)
                .resolve(module.id)
                .apply { mkdirs() }

            // 2. Télécharger et sauvegarder le manifest
            val manifestFile = moduleDir.resolve("manifest.json")
            val manifestContent = gitHubApi.getFileContents(
                owner = module.author,
                repo = module.id,
                path = "ouxy-module.json"
            )["content"] as String
            manifestFile.writeText(manifestContent)

            // 3. Enregistrer le module dans la base de données
            val installedModule = InstalledModule(
                id = module.id,
                name = module.name,
                version = module.version,
                author = module.author
            )
            moduleDao.insertModule(installedModule)
        }
    }

    suspend fun uninstallModule(moduleId: String) {
        withContext(Dispatchers.IO) {
            // 1. Supprimer le dossier du module
            val moduleDir = context.getDir("modules", Context.MODE_PRIVATE)
                .resolve(moduleId)
            moduleDir.deleteRecursively()

            // 2. Supprimer l'entrée de la base de données
            moduleDao.deleteModuleById(moduleId)
        }
    }

    suspend fun isModuleInstalled(moduleId: String): Boolean {
        return moduleDao.getModuleById(moduleId) != null
    }
}