package com.damolks.ouxy.module

import android.content.Context
import androidx.lifecycle.LifecycleCoroutineScope
import com.damolks.ouxy.data.api.StorageApi
import com.damolks.ouxy.module.ModuleContext
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class DefaultModuleContext(
    override val applicationContext: Context,
    override val lifecycleScope: LifecycleCoroutineScope,
    override val storage: com.damolks.ouxy.data.api.StorageApi
) : ModuleContext {

    private val eventHandlers = mutableMapOf<String, MutableList<(Map<String, Any>) -> Unit>>()
    
    override fun hasPermission(permission: String): Boolean {
        // TODO: Implémentation
        return true
    }
    
    override suspend fun requestPermission(permission: String): Boolean = suspendCancellableCoroutine<Boolean> { continuation ->
        MaterialAlertDialogBuilder(applicationContext)
            .setTitle("Permission Required")
            .setMessage("This module needs additional permissions to work properly.")
            .setPositiveButton("Grant") { dialog, _ ->
                continuation.resume(true)
                dialog.dismiss()
            }
            .setNegativeButton("Deny") { dialog, _ ->
                continuation.resume(false)
                dialog.dismiss()
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