package com.damolks.ouxy.module

import dalvik.system.DexClassLoader
import java.io.File

/**
 * Chargeur de classes personnalisé pour les modules Ouxy.
 * Gère le chargement des classes depuis les JARs des modules.
 */
class ModuleClassLoader(
    moduleFile: File,
    parentClassLoader: ClassLoader
) {
    private val dexOutputDir: File
    private val classLoader: DexClassLoader

    init {
        // Crée un répertoire temporaire pour l'extraction du DEX
        dexOutputDir = File(moduleFile.parent, "dex_${moduleFile.nameWithoutExtension}")
        dexOutputDir.mkdir()

        // Crée un DexClassLoader pour charger les classes du module
        classLoader = DexClassLoader(
            moduleFile.absolutePath,
            dexOutputDir.absolutePath,
            null,
            parentClassLoader
        )
    }

    /**
     * Charge et instancie la classe principale du module.
     *
     * @param className Nom complet de la classe principale du module
     * @return Instance de la classe du module
     */
    fun loadModuleClass(className: String): OuxyModule {
        val moduleClass = classLoader.loadClass(className)
        return moduleClass.getDeclaredConstructor().newInstance() as OuxyModule
    }

    /**
     * Nettoie les fichiers temporaires.
     */
    fun cleanup() {
        dexOutputDir.deleteRecursively()
    }
}