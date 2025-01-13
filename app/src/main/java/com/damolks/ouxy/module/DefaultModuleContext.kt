package com.damolks.ouxy.module

import android.content.Context
import androidx.lifecycle.LifecycleCoroutineScope
import com.damolks.ouxy.data.api.StorageApi
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class DefaultModuleContext @Inject constructor(
    @ApplicationContext override val context: Context,
    override val lifecycleScope: LifecycleCoroutineScope,
    private val moduleId: String,
    override val storage: StorageApi,
    private val eventBus: EventBus
) : ModuleContext {

    override fun sendEvent(name: String, data: Map<String, Any>) {
        eventBus.send(name, data)
    }

    override fun registerEventHandler(name: String, handler: (Map<String, Any>) -> Unit) {
        eventBus.register(name, handler)
    }
}