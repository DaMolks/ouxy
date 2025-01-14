package com.damolks.ouxy.module

import android.content.Context
import androidx.lifecycle.LifecycleCoroutineScope
import com.damolks.ouxy.data.api.StorageApi
// Supprimé l'import de ModuleContext local pour utiliser celui du SDK

class DefaultModuleContext(
    override val applicationContext: Context,
    override val lifecycleScope: LifecycleCoroutineScope,
    override val storage: StorageApi
) : com.damolks.ouxy.module.ModuleContext { // Import complet pour éviter l'ambiguïté

    private val eventHandlers = mutableMapOf<String, MutableList<(Map<String, Any>) -> Unit>>()
    
    override fun hasPermission(permission: String): Boolean {
        // TODO: Implémentation
        return true
    }
    
    override suspend fun requestPermission(permission: String): Boolean = suspendCancellableCoroutine { continuation ->
        MaterialAlertDialogBuilder(applicationContext)
            .setTitle("Permission Required")
            .setMessage("This module needs additional permissions to work properly.")
            .setPositiveButton("Grant") { _, _ ->
                // TODO: Implémentation
                continuation.resume(true)
            }
            .setNegativeButton("Deny") { _, _ ->
                continuation.resume(false)
            }
            .show()
    }
    
    override fun sendEvent(name: String, data: Map<String, Any>) {
        eventHandlers[name]?.forEach { handler ->
            handler(data)
        }
    }

    override fun registerEventHandler(name: String, handler: (Map<String, Any>) -> Unit) {
        val handlers = eventHandlers.getOrPut(name) { mutableListOf() }
        handlers.add(handler)
    }
}