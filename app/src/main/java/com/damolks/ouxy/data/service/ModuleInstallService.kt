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
import java.io.File
import java.util.jar.JarInputStream
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

                // Créer le dossier de modules si nécessaire
                val modulesDir = context.getDir("modules", Context.MODE_PRIVATE)
                val moduleDir = File(modulesDir, module.id).apply { mkdirs() }

                // Télécharger et sauvegarder le manifest
                val manifest = downloadManifest(module)
                File(moduleDir, "manifest.json").writeText(manifest.toString())
                Log.d("ModuleInstall", "Manifest téléchargé et sauvegardé")

                // Télécharger et installer le JAR
                val jarFile = downloadModuleJar(module, manifest)

                // Inspecter le contenu du JAR
                Log.d("ModuleInstall", "Inspection du contenu du JAR")
                inspectJarContent(jarFile)

                // S'assurer que le JAR final est en lecture seule
                val finalJarFile = File(moduleDir, "module.jar")
                jarFile.copyTo(finalJarFile, overwrite = true)
                finalJarFile.setReadOnly()

                Log.d("ModuleInstall", "JAR installé: ${finalJarFile.absolutePath}")

                // Vérifier le contenu du JAR final
                Log.d("ModuleInstall", "Inspection du JAR final")
                inspectJarContent(finalJarFile)

                // Charger et initialiser le module
                loadedModules[module.id] = loadModule(module.id, finalJarFile, manifest)

                // Enregistrer dans la base de données
                val installedModule = InstalledModule(
                    id = module.id,
                    name = module.name,
                    version = module.version,
                    author = module.author
                )
                moduleDao.insertModule(installedModule)

                // Nettoyer les fichiers temporaires
                jarFile.delete()

                Log.d("ModuleInstall", "Installation terminée avec succès")

            } catch (e: Exception) {
                Log.e("ModuleInstall", "Erreur lors de l'installation", e)
                throw e
            }
        }
    }

    private fun inspectJarContent(jarFile: File) {
        try {
            val entries = mutableListOf<String>()
            jarFile.inputStream().use { input ->
                JarInputStream(input).use { jarInputStream ->
                    var entry = jarInputStream.nextJarEntry
                    while (entry != null) {
                        entries.add("${entry.name} (${entry.size} bytes)")
                        entry = jarInputStream.nextJarEntry
                    }
                }
            }
            Log.d("ModuleInstall", "Contenu du JAR ${jarFile.name}:\n${entries.joinToString("\n")}")
        } catch (e: Exception) {
            Log.e("ModuleInstall", "Erreur lors de l'inspection du JAR", e)
        }
    }

    private suspend fun downloadManifest(module: MarketplaceModule): JSONObject {
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
            JSONObject(decodedContent)

        } catch (e: Exception) {
            Log.e("ModuleInstall", "Erreur lors du téléchargement du manifest", e)
            throw e
        }
    }

    private suspend fun downloadModuleJar(
        module: MarketplaceModule,
        manifest: JSONObject
    ): File {
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

            // Utiliser un fichier temporaire pour le téléchargement
            val tempFile = File.createTempFile("module_dl_", ".jar", context.cacheDir)
            try {
                gitHubApi.downloadFile(downloadUrl).use { body ->
                    tempFile.outputStream().use { output ->
                        val copied = body.byteStream().copyTo(output)
                        Log.d("ModuleInstall", "Taille du JAR téléchargé: $copied bytes")
                    }
                }

                if (!tempFile.exists() || tempFile.length() == 0L) {
                    throw IllegalStateException("Le JAR n'a pas été téléchargé correctement")
                }

                tempFile
            } catch (e: Exception) {
                tempFile.delete() // Nettoyer en cas d'erreur
                throw e
            }

        } catch (e: Exception) {
            Log.e("ModuleInstall", "Erreur lors du téléchargement du JAR", e)
            throw e
        }
    }

    private fun loadModule(
        moduleId: String,
        moduleFile: File,
        manifest: JSONObject
    ): OuxyModule {
        return try {
            if (!moduleFile.exists()) {
                throw IllegalStateException("Le fichier du module n'existe pas: ${moduleFile.absolutePath}")
            }

            if (moduleFile.canWrite()) {
                throw SecurityException("Le fichier du module doit être en lecture seule")
            }

            Log.d("ModuleInstall", "Création du ClassLoader pour ${moduleFile.absolutePath}")
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

            val modulesDir = context.getDir("modules", Context.MODE_PRIVATE)
            File(modulesDir, moduleId).deleteRecursively()

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