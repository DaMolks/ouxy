package com.damolks.ouxy.di

import com.damolks.ouxy.data.api.GitHubApi
import com.damolks.ouxy.data.repository.MarketplaceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideMarketplaceRepository(gitHubApi: GitHubApi): MarketplaceRepository {
        return MarketplaceRepository(gitHubApi)
    }
}