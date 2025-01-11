package com.damolks.ouxy.data.api

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Implémentation de StorageApi utilisant DataStore pour stocker les données des modules.
 */
class ModuleStorageApi @Inject constructor(
    @ApplicationContext private val context: Context,
    private val moduleId: String
) : StorageApi {

    private val Context.moduleDataStore by preferencesDataStore(name = "module_$moduleId")

    override suspend fun <T> save(key: String, value: T) {
        context.moduleDataStore.edit { preferences ->
            when (value) {
                is String -> preferences[stringPreferencesKey(key)] = value
                is Int -> preferences[intPreferencesKey(key)] = value
                is Long -> preferences[longPreferencesKey(key)] = value
                is Float -> preferences[floatPreferencesKey(key)] = value
                is Double -> preferences[doublePreferencesKey(key)] = value
                is Boolean -> preferences[booleanPreferencesKey(key)] = value
                else -> throw IllegalArgumentException("Type non supporté: ${value?.javaClass}")
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun <T> get(key: String, defaultValue: T): T {
        val preferences = context.moduleDataStore.data.first()
        return when (defaultValue) {
            is String -> preferences[stringPreferencesKey(key)] as? T ?: defaultValue
            is Int -> preferences[intPreferencesKey(key)] as? T ?: defaultValue
            is Long -> preferences[longPreferencesKey(key)] as? T ?: defaultValue
            is Float -> preferences[floatPreferencesKey(key)] as? T ?: defaultValue
            is Double -> preferences[doublePreferencesKey(key)] as? T ?: defaultValue
            is Boolean -> preferences[booleanPreferencesKey(key)] as? T ?: defaultValue
            else -> defaultValue
        }
    }

    override suspend fun remove(key: String) {
        context.moduleDataStore.edit { preferences ->
            preferences.remove(stringPreferencesKey(key))
            preferences.remove(intPreferencesKey(key))
            preferences.remove(longPreferencesKey(key))
            preferences.remove(floatPreferencesKey(key))
            preferences.remove(doublePreferencesKey(key))
            preferences.remove(booleanPreferencesKey(key))
        }
    }

    override suspend fun clear() {
        context.moduleDataStore.edit { it.clear() }
    }
}