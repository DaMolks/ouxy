package com.damolks.ouxy.data.service

import android.content.Context
import com.damolks.ouxy.data.api.GitHubApi
import com.damolks.ouxy.data.api.ModuleStorageApi
import com.damolks.ouxy.data.dao.ModuleDao
import com.damolks.ouxy.data.model.InstalledModule
import com.damolks.ouxy.data.model.MarketplaceModule
import com.damolks.ouxy.module.DefaultModuleContext
import com.damolks.ouxy.module.ModuleClassLoader
import com.damolks.ouxy.module.OuxyModule
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ModuleInstallService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gitHubApi: GitHubApi,
    private val moduleDao: ModuleDao
) {
    private val loadedModules = mutableMapOf<String, OuxyModule>()
    private val moduleClassLoaders = mutableMapOf<String, ModuleClassLoader>()

    suspend fun installModule(module: MarketplaceModule) {
        withContext(Dispatchers.IO) {
            val moduleDir = prepareModuleDirectory(module.id)
            val manifest = downloadManifest(module, moduleDir)
            val moduleFile = downloadModuleJar(module, moduleDir, manifest)
            
            // Charger et initialiser le module
            val moduleInstance = loadModule(module.id, moduleFile, manifest)
            
            // Enregistrer dans la base de données
            val installedModule = InstalledModule(
                id = module.id,
                name = module.name,
                version = module.version,
                author = module.author
            )
            moduleDao.insertModule(installedModule)
        }
    }

    private fun prepareModuleDirectory(moduleId: String) = 
        context.getDir("modules", Context.MODE_PRIVATE)
            .resolve(moduleId)
            .apply { mkdirs() }

    private suspend fun downloadManifest(module: MarketplaceModule, moduleDir: File): JSONObject {
        val manifestFile = moduleDir.resolve("manifest.json")
        val manifestContent = gitHubApi.getFileContents(
            owner = module.author,
            repo = module.id,
            path = "ouxy-module.json"
        )["content"] as String
        manifestFile.writeText(manifestContent)
        return JSONObject(manifestContent)
    }

    private suspend fun downloadModuleJar(module: MarketplaceModule, moduleDir: File, manifest: JSONObject): File {
        // Créer le fichier JAR de destination
        val moduleFile = moduleDir.resolve("module.jar")
        
        // TODO: Télécharger le JAR depuis GitHub
        // Cette partie nécessitera probablement l'ajout d'une nouvelle méthode à GitHubApi
        
        return moduleFile
    }
    
    private fun loadModule(moduleId: String, moduleFile: File, manifest: JSONObject): OuxyModule {
        val classLoader = ModuleClassLoader(moduleFile, javaClass.classLoader!!)
        moduleClassLoaders[moduleId] = classLoader
        
        val moduleClass = manifest.getString("entry")
        val moduleInstance = classLoader.loadModuleClass(moduleClass)
        
        // Créer et initialiser le contexte du module
        val moduleContext = DefaultModuleContext(
            context = context,
            moduleId = moduleId,
            moduleStorage = ModuleStorageApi(context, moduleId)
        )
        
        moduleInstance.initialize(moduleContext)
        loadedModules[moduleId] = moduleInstance
        
        return moduleInstance
    }

    suspend fun uninstallModule(moduleId: String) {
        withContext(Dispatchers.IO) {
            // Nettoyage du module chargé
            loadedModules[moduleId]?.cleanup()
            loadedModules.remove(moduleId)
            moduleClassLoaders[moduleId]?.cleanup()
            moduleClassLoaders.remove(moduleId)

            // Suppression des fichiers
            val moduleDir = context.getDir("modules", Context.MODE_PRIVATE)
                .resolve(moduleId)
            moduleDir.deleteRecursively()

            // Suppression de la base de données
            moduleDao.deleteModuleById(moduleId)
        }
    }

    suspend fun isModuleInstalled(moduleId: String): Boolean {
        return moduleDao.getModuleById(moduleId) != null
    }

    fun getLoadedModule(moduleId: String): OuxyModule? {
        return loadedModules[moduleId]
    }
}