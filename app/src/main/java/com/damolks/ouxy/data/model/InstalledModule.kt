package com.damolks.ouxy.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "installed_modules")
data class InstalledModule(
    @PrimaryKey
    val id: String,
    val name: String,
    val version: String,
    val author: String,
    val installDate: Long = System.currentTimeMillis()
)
