package com.damolks.ouxy.module

import android.content.Context
import com.damolks.ouxy.data.api.StorageApi

/**
 * Contexte fourni aux modules pour accéder aux APIs d'Ouxy de manière sécurisée.
 */
interface ModuleContext {
    /**
     * Contexte Android de l'application
     */
    val applicationContext: Context

    /**
     * API de stockage pour les données du module
     */
    val storage: StorageApi

    /**
     * Vérifie si une permission est accordée au module
     */
    fun hasPermission(permission: String): Boolean

    /**
     * Demande une permission déclarée dans le manifest du module
     */
    suspend fun requestPermission(permission: String): Boolean
}