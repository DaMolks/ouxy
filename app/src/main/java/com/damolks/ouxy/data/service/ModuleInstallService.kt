package com.damolks.ouxy.data.service

import android.content.Context
import android.util.Log
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
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class ModuleInstallService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gitHubApi: GitHubApi,
    private val moduleDao: ModuleDao,
    private val storageProvider: Provider<ModuleStorageApi>
) {
    private val loadedModules = mutableMapOf<String, OuxyModule>()
    private val moduleClassLoaders = mutableMapOf<String, ModuleClassLoader>()

    suspend fun installModule(module: MarketplaceModule) {
        withContext(Dispatchers.IO) {
            Log.d("ModuleInstall", "Création du répertoire du module: ${module.id}")
            val moduleDir = context.getDir("modules", Context.MODE_PRIVATE)
                .resolve(module.id)
                .apply { mkdirs() }

            Log.d("ModuleInstall", "Téléchargement du manifest")
            val manifest = downloadManifest(module, moduleDir)
            
            Log.d("ModuleInstall", "Téléchargement du JAR module")
            val moduleFile = downloadModuleJar(module, moduleDir, manifest)
            
            Log.d("ModuleInstall", "Chargement et initialisation du module")
            loadedModules[module.id] = loadModule(module.id, moduleFile, manifest)
            
            Log.d("ModuleInstall", "Enregistrement dans la base de données")
            val installedModule = InstalledModule(
                id = module.id,
                name = module.name,
                version = module.version,
                author = module.author
            )
            moduleDao.insertModule(installedModule)
        }
    }

    private suspend fun downloadManifest(module: MarketplaceModule, moduleDir: java.io.File): JSONObject {
        Log.d("ModuleInstall", "Récupération du manifest pour ${module.id}")
        val manifestContent = gitHubApi.getFileContents(
            owner = module.author,
            repo = module.id,
            path = "ouxy-module.json"
        )["content"] as String
        
        moduleDir.resolve("manifest.json").writeText(manifestContent)
        return JSONObject(manifestContent)
    }

    private suspend fun downloadModuleJar(
        module: MarketplaceModule,
        moduleDir: java.io.File,
        manifest: JSONObject
    ): java.io.File {
        val jarPath = manifest.optString("jarPath", "module/build/module.jar")
        
        Log.d("ModuleInstall", "Récupération du JAR : $jarPath")
        
        val jarInfo = gitHubApi.getFileContents(
            owner = module.author,
            repo = module.id,
            path = jarPath
        )
        
        val downloadUrl = jarInfo["download_url"] as? String
            ?: throw IllegalStateException("URL de téléchargement non trouvée pour le JAR")

        val moduleFile = moduleDir.resolve("module.jar")
        
        Log.d("ModuleInstall", "Téléchargement du JAR depuis $downloadUrl")
        
        gitHubApi.downloadFile(downloadUrl).use { body ->
            moduleFile.outputStream().use { output ->
                body.byteStream().copyTo(output)
            }
        }
        
        return moduleFile
    }
    
    private fun loadModule(
        moduleId: String,
        moduleFile: java.io.File,
        manifest: JSONObject
    ): OuxyModule {
        val classLoader = ModuleClassLoader(moduleFile, javaClass.classLoader!!)
        moduleClassLoaders[moduleId] = classLoader
        
        val moduleClass = manifest.getString("entry")
        val moduleInstance = classLoader.loadModuleClass(moduleClass)
        
        val moduleContext = DefaultModuleContext(
            context = context,
            moduleId = moduleId,
            moduleStorage = storageProvider.get()
        )
        
        moduleInstance.initialize(moduleContext)
        return moduleInstance
    }

    suspend fun uninstallModule(moduleId: String) {
        withContext(Dispatchers.IO) {
            loadedModules[moduleId]?.cleanup()
            loadedModules.remove(moduleId)
            moduleClassLoaders[moduleId]?.cleanup()
            moduleClassLoaders.remove(moduleId)

            val moduleDir = context.getDir("modules", Context.MODE_PRIVATE)
                .resolve(moduleId)
            moduleDir.deleteRecursively()

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