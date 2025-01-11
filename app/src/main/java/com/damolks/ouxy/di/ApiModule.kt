package com.damolks.ouxy.di

import com.damolks.ouxy.data.api.GitHubApi
import com.damolks.ouxy.data.api.MockGitHubApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun provideGitHubApi(): GitHubApi = MockGitHubApi()
}