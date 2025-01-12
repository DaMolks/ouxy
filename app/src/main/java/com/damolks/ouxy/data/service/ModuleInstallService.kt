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
            try {
                Log.d("ModuleInstall", "Début de l'installation du module: ${module.id}")
                
                val moduleDir = context.getDir("modules", Context.MODE_PRIVATE)
                    .resolve(module.id)
                    .apply { mkdirs() }
                Log.d("ModuleInstall", "Répertoire créé: ${moduleDir.absolutePath}")

                val manifest = downloadManifest(module, moduleDir)
                Log.d("ModuleInstall", "Manifest téléchargé: ${manifest.toString(2)}")
                
                val moduleFile = downloadModuleJar(module, moduleDir, manifest)
                Log.d("ModuleInstall", "JAR téléchargé: ${moduleFile.absolutePath}, taille: ${moduleFile.length()}")
                
                Log.d("ModuleInstall", "Chargement du module")
                loadedModules[module.id] = loadModule(module.id, moduleFile, manifest)
                
                Log.d("ModuleInstall", "Enregistrement en base de données")
                val installedModule = InstalledModule(
                    id = module.id,
                    name = module.name,
                    version = module.version,
                    author = module.author
                )
                moduleDao.insertModule(installedModule)
                Log.d("ModuleInstall", "Installation terminée avec succès")
                
            } catch (e: Exception) {
                Log.e("ModuleInstall", "Erreur lors de l'installation", e)
                throw e
            }
        }
    }

    private suspend fun downloadManifest(module: MarketplaceModule, moduleDir: java.io.File): JSONObject {
        return try {
            Log.d("ModuleInstall", "Téléchargement du manifest depuis GitHub")
            val response = gitHubApi.getFileContents(
                owner = module.author,
                repo = module.id,
                path = "ouxy-module.json"
            )
            
            val content = response["content"] as? String
                ?: throw IllegalStateException("Contenu du manifest non trouvé")
                
            val decodedContent = android.util.Base64.decode(content, android.util.Base64.DEFAULT)
                .toString(Charsets.UTF_8)
                
            Log.d("ModuleInstall", "Manifest décodé: $decodedContent")
            
            moduleDir.resolve("manifest.json").writeText(decodedContent)
            JSONObject(decodedContent)
            
        } catch (e: Exception) {
            Log.e("ModuleInstall", "Erreur lors du téléchargement du manifest", e)
            throw e
        }
    }

    private suspend fun downloadModuleJar(
        module: MarketplaceModule,
        moduleDir: java.io.File,
        manifest: JSONObject
    ): java.io.File {
        return try {
            val jarPath = manifest.getString("jarPath")
            Log.d("ModuleInstall", "Chemin du JAR: $jarPath")
            
            val jarInfo = gitHubApi.getFileContents(
                owner = module.author,
                repo = module.id,
                path = jarPath
            )
            
            val downloadUrl = jarInfo["download_url"] as? String
                ?: throw IllegalStateException("URL de téléchargement non trouvée pour le JAR")

            Log.d("ModuleInstall", "Téléchargement depuis: $downloadUrl")
            
            val moduleFile = moduleDir.resolve("module.jar")
            gitHubApi.downloadFile(downloadUrl).use { body ->
                moduleFile.outputStream().use { output ->
                    body.byteStream().copyTo(output)
                }
            }
            
            if (!moduleFile.exists() || moduleFile.length() == 0L) {
                throw IllegalStateException("Le JAR n'a pas été téléchargé correctement")
            }
            
            moduleFile
            
        } catch (e: Exception) {
            Log.e("ModuleInstall", "Erreur lors du téléchargement du JAR", e)
            throw e
        }
    }
    
    private fun loadModule(
        moduleId: String,
        moduleFile: java.io.File,
        manifest: JSONObject
    ): OuxyModule {
        return try {
            Log.d("ModuleInstall", "Création du ClassLoader")
            val classLoader = ModuleClassLoader(moduleFile, javaClass.classLoader!!)
            moduleClassLoaders[moduleId] = classLoader
            
            val moduleClass = manifest.getString("entry")
            Log.d("ModuleInstall", "Chargement de la classe: $moduleClass")
            
            val moduleInstance = classLoader.loadModuleClass(moduleClass)
            
            Log.d("ModuleInstall", "Création du contexte du module")
            val moduleContext = DefaultModuleContext(
                context = context,
                moduleId = moduleId,
                moduleStorage = storageProvider.get()
            )
            
            Log.d("ModuleInstall", "Initialisation du module")
            moduleInstance.initialize(moduleContext)
            moduleInstance
            
        } catch (e: Exception) {
            Log.e("ModuleInstall", "Erreur lors du chargement du module", e)
            throw e
        }
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