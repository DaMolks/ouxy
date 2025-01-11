package com.damolks.ouxy.data.repository

import com.damolks.ouxy.data.api.GitHubApi
import com.damolks.ouxy.data.model.MarketplaceModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MarketplaceRepository @Inject constructor(
    private val gitHubApi: GitHubApi
) {

    suspend fun searchModules(query: String = "topic:ouxy-module"): List<MarketplaceModule> {
        return try {
            val searchResult = searchGitHubRepositories(query)
            parseRepositoriesToModules(searchResult)
        } catch (e: Exception) {
            emptyList()
        }
    }

    private suspend fun searchGitHubRepositories(query: String): Map<String, Any> = withContext(Dispatchers.IO) {
        gitHubApi.searchRepositories(query)
    }

    private suspend fun parseRepositoriesToModules(searchResult: Map<String, Any>): List<MarketplaceModule> {
        val modules = mutableListOf<MarketplaceModule>()
        @Suppress("UNCHECKED_CAST")
        val items = (searchResult["items"] as? List<*>)?.filterIsInstance<Map<String, Any>>() ?: return emptyList()

        items.forEach { repo ->
            try {
                val owner = repo["owner"] as? Map<*, *>
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
            val response = gitHubApi.getFileContents(owner, repo, MANIFEST_FILENAME)
            val content = response["content"] as? String ?: return null
            val decodedContent = content.decodeBase64()
            JSONObject(decodedContent)
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