package com.damolks.ouxy.data.api

/**
 * API de stockage fournie aux modules pour sauvegarder leurs données.
 * Chaque module dispose de son propre espace de stockage isolé.
 */
interface StorageApi {
    /**
     * Sauvegarde une donnée dans le stockage du module.
     */
    suspend fun <T> save(key: String, value: T)

    /**
     * Récupère une donnée du stockage du module.
     */
    suspend fun <T> get(key: String, defaultValue: T): T

    /**
     * Supprime une donnée du stockage.
     */
    suspend fun remove(key: String)

    /**
     * Efface toutes les données du module.
     */
    suspend fun clear()
}