package com.damolks.ouxy.module

import android.content.Context
import androidx.lifecycle.LifecycleCoroutineScope
import com.damolks.ouxy.data.api.StorageApi
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class DefaultModuleContext(
    override val applicationContext: Context,
    override val lifecycleScope: LifecycleCoroutineScope,
    override val storage: StorageApi
) : ModuleContext {

    private val eventHandlers = mutableMapOf<String, MutableList<(Map<String, Any>) -> Unit>>()
    
    override fun hasPermission(permission: String): Boolean {
        // TODO: Implémenter la vérification des permissions
        return true
    }
    
    override suspend fun requestPermission(permission: String): Boolean = suspendCancellableCoroutine { continuation ->
        MaterialAlertDialogBuilder(applicationContext)
            .setTitle("Permission Required")
            .setMessage("This module needs additional permissions to work properly.")
            .setPositiveButton("Grant") { _, _ ->
                // TODO: Implémenter la demande de permission
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