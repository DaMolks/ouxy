package com.damolks.ouxy.data.api

import retrofit2.http.*

interface GitHubApi {
    @GET("search/repositories")
    suspend fun searchRepositories(@Query("q") query: String): Map<String, Any>

    @GET("repos/{owner}/{repo}/contents/{path}")
    suspend fun getFileContents(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("path") path: String
    ): Map<String, Any>
    
    @GET
    @Streaming
    suspend fun downloadFile(@Url url: String): okhttp3.ResponseBody
}