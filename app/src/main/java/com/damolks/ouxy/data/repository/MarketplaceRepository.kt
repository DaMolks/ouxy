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
            val searchResult = search_repositories(query)
            parseRepositoriesToModules(searchResult)
        } catch (e: Exception) {
            emptyList()
        }
    }

    private suspend fun parseRepositoriesToModules(searchResult: Map<String, Any>): List<MarketplaceModule> {
        val modules = mutableListOf<MarketplaceModule>()
        val items = searchResult["items"] as? List<Map<String, Any>> ?: return emptyList()

        items.forEach { repo ->
            try {
                val owner = repo["owner"] as? Map<String, Any>
                val ownerLogin = owner?.get("login") as? String ?: return@forEach
                val repoName = repo["name"] as? String ?: return@forEach

                // Vérifie si le manifest existe
                val manifest = getModuleManifest(ownerLogin, repoName)
                if (manifest != null) {
                    modules.add(
                        MarketplaceModule(
                            id = repoName,
                            name = manifest.getString("name"),
                            description = manifest.getString("description"),
                            author = ownerLogin,
                            version = manifest.getString("version"),
                            minAppVersion = manifest.getString("minAppVersion"),
                            stars = (repo["stargazers_count"] as? Number)?.toInt() ?: 0,
                            repoUrl = repo["html_url"] as? String ?: "",
                            screenshotUrls = manifest.optJSONArray("screenshots")?.let { array ->
                                List(array.length()) { array.getString(it) }
                            } ?: emptyList()
                        )
                    )
                }
            } catch (e: Exception) {
                // Skip this repo
            }
        }

        return modules
    }

    private suspend fun getModuleManifest(owner: String, repo: String): JSONObject? {
        return try {
            val response = get_file_contents(owner, repo, MANIFEST_FILENAME)
            val content = response["content"] as? String ?: return null
            val decodedContent = content.decodeBase64()
            JSONObject(decodedContent)
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun search_repositories(query: String) = withContext(Dispatchers.IO) {
        // Utilise la fonction search_repositories fournie
        @Suppress("UNCHECKED_CAST")
        search_repositories(mapOf("query" to query)) as Map<String, Any>
    }

    private suspend fun get_file_contents(owner: String, repo: String, path: String) = withContext(Dispatchers.IO) {
        // Utilise la fonction get_file_contents fournie
        @Suppress("UNCHECKED_CAST")
        get_file_contents(mapOf(
            "owner" to owner,
            "repo" to repo,
            "path" to path
        )) as Map<String, Any>
    }

    private fun String.decodeBase64(): String {
        return android.util.Base64.decode(this, android.util.Base64.DEFAULT)
            .toString(Charsets.UTF_8)
    }

    companion object {
        private const val MANIFEST_FILENAME = "ouxy-module.json"
    }
}