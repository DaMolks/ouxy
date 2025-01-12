package com.damolks.ouxy.data.api

import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody

class MockGitHubApi : GitHubApi {
    override suspend fun searchRepositories(query: String): Map<String, Any> {
        // Simuler une réponse GitHub basique
        return mapOf(
            "items" to listOf(
                mapOf(
                    "name" to "test-module",
                    "owner" to mapOf("login" to "test-author"),
                    "html_url" to "https://github.com/test-author/test-module",
                    "stargazers_count" to 5
                )
            )
        )
    }

    override suspend fun getFileContents(owner: String, repo: String, path: String): Map<String, Any> {
        // Simuler une réponse de manifest basique
        val manifest = """
            {
                "name": "Test Module",
                "description": "A test module for development",
                "version": "1.0.0",
                "minAppVersion": "1.0.0",
                "entry": "com.example.TestModule",
                "screenshots": []
            }
        """.trimIndent()

        return mapOf(
            "content" to android.util.Base64.encodeToString(
                manifest.toByteArray(),
                android.util.Base64.NO_WRAP
            )
        )
    }

    override suspend fun downloadFile(url: String): ResponseBody {
        // Retourner un fichier JAR fictif vide
        return ByteArray(0).toResponseBody(null)
    }
}