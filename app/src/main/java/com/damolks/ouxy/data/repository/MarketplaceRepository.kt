package com.damolks.ouxy.data.repository

import com.damolks.ouxy.data.model.MarketplaceModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MarketplaceRepository @Inject constructor() {

    suspend fun searchModules(query: String = "topic:ouxy-module"): List<MarketplaceModule> {
        return try {
            val response = searchGitHubRepositories(query)
            parseRepositoriesToModules(response)
        } catch (e: Exception) {
            emptyList()
        }
    }

    private suspend fun searchGitHubRepositories(query: String): List<Map<String, Any>> = withContext(Dispatchers.IO) {
        val result = mutableListOf<Map<String, Any>>()
        
        // Pour le moment, retourner des données de test
        result.add(mapOf(
            "name" to "inventory-module",
            "owner" to mapOf("login" to "DaMolks"),
            "description" to "Module de gestion d'inventaire pour Ouxy",
            "stargazers_count" to 12
        ))
        
        result
    }

    private suspend fun parseRepositoriesToModules(repositories: List<Map<String, Any>>): List<MarketplaceModule> {
        val modules = mutableListOf<MarketplaceModule>()

        for (repo in repositories) {
            try {
                val owner = repo["owner"] as? Map<String, Any>
                modules.add(MarketplaceModule(
                    id = repo["name"] as String,
                    name = "Inventaire",
                    description = repo["description"] as String,
                    author = owner?.get("login") as String,
                    version = "1.0.0",
                    minAppVersion = "1.0.0",
                    stars = (repo["stargazers_count"] as? Number)?.toInt() ?: 0,
                    repoUrl = "https://github.com/${owner?.get("login")}/${repo["name"]}"
                ))
            } catch (e: Exception) {
                // Skip this repo
            }
        }

        return modules
    }

    companion object {
        private const val MANIFEST_FILENAME = "ouxy-module.json"
    }
}