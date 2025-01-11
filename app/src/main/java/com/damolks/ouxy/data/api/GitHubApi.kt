package com.damolks.ouxy.data.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GitHubApi {
    @GET("search/repositories")
    suspend fun searchRepositories(@Query("q") query: String): Map<String, Any>

    @GET("repos/{owner}/{repo}/contents/{path}")
    suspend fun getFileContents(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("path") path: String
    ): Map<String, Any>
}