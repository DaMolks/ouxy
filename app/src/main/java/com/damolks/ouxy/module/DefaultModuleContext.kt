package com.damolks.ouxy.module

import android.content.Context
import android.content.pm.PackageManager
import com.damolks.ouxy.data.api.StorageApi
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Implémentation par défaut du contexte fourni aux modules.
 */
class DefaultModuleContext @Inject constructor(
    @ApplicationContext private val context: Context,
    private val moduleId: String,
    private val moduleStorage: StorageApi
) : ModuleContext {

    override val applicationContext: Context = context

    override val storage: StorageApi = moduleStorage

    override fun hasPermission(permission: String): Boolean {
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    override suspend fun requestPermission(permission: String): Boolean {
        // TODO: Implémenter la demande de permission via l'activité
        return hasPermission(permission)
    }
}