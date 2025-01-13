package com.damolks.ouxy.module

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventBus @Inject constructor() {
    private val handlers = mutableMapOf<String, MutableList<(Map<String, Any>) -> Unit>>()

    fun send(name: String, data: Map<String, Any>) {
        handlers[name]?.forEach { handler ->
            handler(data)
        }
    }

    fun register(name: String, handler: (Map<String, Any>) -> Unit) {
        handlers.getOrPut(name) { mutableListOf() }.add(handler)
    }
}
