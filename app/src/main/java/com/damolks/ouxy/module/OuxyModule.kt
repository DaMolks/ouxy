package com.damolks.ouxy.module

import android.view.View

/**
 * Interface que chaque module Ouxy doit implémenter.
 * Cette interface fournit les points d'entrée et méthodes de cycle de vie nécessaires.
 */
interface OuxyModule {
    /**
     * Appelé lors de l'initialisation du module.
     * Utiliser ce point d'entrée pour initialiser les ressources nécessaires.
     *
     * @param context Contexte fournissant l'accès aux APIs Ouxy
     */
    fun initialize(context: ModuleContext)

    /**
     * Retourne la vue principale du module qui sera affichée dans Ouxy.
     * Cette vue doit être autonome et gérer son propre état.
     */
    fun getMainView(): View

    /**
     * Appelé lors de la désinstallation ou désactivation du module.
     * Utiliser cette méthode pour nettoyer les ressources.
     */
    fun cleanup()
}