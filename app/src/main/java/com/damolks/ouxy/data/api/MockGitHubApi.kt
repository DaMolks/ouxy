package com.damolks.ouxy.data.api

class MockGitHubApi : GitHubApi {
    override suspend fun searchRepositories(query: String): Map<String, Any> {
        // Simulate a basic GitHub search response
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
        // Simulate a basic manifest file response
        val manifest = """
            {
                "name": "Test Module",
                "description": "A test module for development",
                "version": "1.0.0",
                "minAppVersion": "1.0.0",
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
}