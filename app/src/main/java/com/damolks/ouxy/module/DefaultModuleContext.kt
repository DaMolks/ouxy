package com.damolks.ouxy.module

import android.content.Context
import android.content.pm.PackageManager
import androidx.lifecycle.LifecycleCoroutineScope
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Implémentation par défaut du contexte fourni aux modules.
 */
class DefaultModuleContext @Inject constructor(
    @ApplicationContext override val applicationContext: Context,
    private val moduleId: String,
    override val storage: StorageApi,
    override val lifecycleScope: LifecycleCoroutineScope
) : ModuleContext {

    private val eventHandlers = mutableMapOf<String, (Map<String, Any>) -> Unit>()

    override fun hasPermission(permission: String): Boolean {
        return applicationContext.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    override suspend fun requestPermission(permission: String): Boolean {
        // TODO: Implémenter la demande de permission via l'activité
        return hasPermission(permission)
    }

    override fun sendEvent(name: String, data: Map<String, Any>) {
        // TODO: Implémenter l'envoi d'événements entre modules
    }

    override fun registerEventHandler(name: String, handler: (Map<String, Any>) -> Unit) {
        eventHandlers[name] = handler
    }
}