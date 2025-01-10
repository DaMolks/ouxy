package com.damolks.ouxy.data.model

data class Module(
    val id: String,
    val name: String,
    val description: String,
    val version: String,
    val iconResId: Int,
    val isInstalled: Boolean = false,
    val isEnabled: Boolean = true
)