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
        
        // TODO: Implémenter la recherche des modules
        // search_repositories(query)
        
        result
    }

    private suspend fun parseRepositoriesToModules(repositories: List<Map<String, Any>>): List<MarketplaceModule> {
        val modules = mutableListOf<MarketplaceModule>()

        for (repo in repositories) {
            try {
                val owner = repo["owner"] as? Map<String, Any>
                val manifest = getModuleManifest(
                    owner = owner?.get("login") as? String ?: continue,
                    repo = repo["name"] as? String ?: continue
                )
                manifest?.let { modules.add(it) }
            } catch (e: Exception) {
                // Skip this repo
            }
        }

        return modules
    }

    suspend fun getModuleManifest(owner: String, repo: String): MarketplaceModule? {
        return try {
            val response = getFileContents(owner, repo, MANIFEST_FILENAME)
            val content = response["content"] as? String ?: return null
            val decodedContent = content.decodeBase64()
            parseModuleManifest(decodedContent)
        } catch (e: Exception) {
            null
        }
    }

    private fun parseModuleManifest(content: String): MarketplaceModule? {
        return try {
            val json = JSONObject(content)
            MarketplaceModule(
                id = json.getString("id"),
                name = json.getString("name"),
                description = json.getString("description"),
                author = json.getString("author"),
                version = json.getString("version"),
                minAppVersion = json.getString("minAppVersion"),
                stars = 0, // Sera mis à jour avec les données du repo
                repoUrl = "", // Sera mis à jour avec les données du repo
                screenshotUrls = json.optJSONArray("screenshots")?.let { array ->
                    List(array.length()) { array.getString(it) }
                } ?: emptyList()
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun String.decodeBase64(): String {
        return android.util.Base64.decode(this, android.util.Base64.DEFAULT)
            .toString(Charsets.UTF_8)
    }

    companion object {
        private const val MANIFEST_FILENAME = "ouxy-module.json"
    }
}