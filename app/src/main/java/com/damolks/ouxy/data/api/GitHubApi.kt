package com.damolks.ouxy.data.api

interface GitHubApi {
    suspend fun searchRepositories(query: String): Map<String, Any>
    suspend fun getFileContents(owner: String, repo: String, path: String): Map<String, Any>
}

class MockGitHubApi : GitHubApi {
    override suspend fun searchRepositories(query: String): Map<String, Any> {
        return mapOf(
            "items" to listOf(
                mapOf(
                    "name" to "inventory-module",
                    "owner" to mapOf("login" to "DaMolks"),
                    "description" to "Module de gestion d'inventaire pour Ouxy",
                    "stargazers_count" to 12,
                    "html_url" to "https://github.com/DaMolks/inventory-module"
                )
            )
        )
    }

    override suspend fun getFileContents(owner: String, repo: String, path: String): Map<String, Any> {
        return mapOf(
            "content" to "ewogICAgXCJuYW1lXCI6IFwiSW52ZW50YWlyZVwiLAogICAgXCJkZXNjcmlwdGlvblwiOiBcIk1vZHVsZSBkZSBnZXN0aW9uIGQnaW52ZW50YWlyZSBwb3VyIE91eHlcIiwKICAgIFwidmVyc2lvblwiOiBcIjEuMC4wXCIsCiAgICBcIm1pbkFwcFZlcnNpb25cIjogXCIxLjAuMFwiCn0="  // base64 encoded JSON
        )
    }
}