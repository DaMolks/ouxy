package com.damolks.ouxy.data.repository

import com.damolks.ouxy.data.model.MarketplaceModule

class MarketplaceRepository {
    
    suspend fun searchModules(query: String = "topic:ouxy-module"): List<MarketplaceModule> {
        return try {
            val response = searchGitHubRepositories(query)
            return parseRepositoriesToModules(response)
        } catch (e: Exception) {
            // Log l'erreur
            emptyList()
        }
    }

    private suspend fun searchGitHubRepositories(query: String) = withContext(Dispatchers.IO) {
        // TODO: Utiliser l'API search_repositories
    }

    suspend fun getModuleManifest(owner: String, repo: String): MarketplaceModule? {
        return try {
            val response = getFileContents(owner, repo, "ouxy-module.json")
            parseModuleManifest(response)
        } catch (e: Exception) {
            // Log l'erreur
            null
        }
    }

    companion object {
        private const val MANIFEST_FILENAME = "ouxy-module.json"
    }
}