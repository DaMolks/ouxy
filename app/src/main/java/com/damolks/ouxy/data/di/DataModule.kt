package com.damolks.ouxy.data.di

import android.content.Context
import androidx.room.Room
import com.damolks.ouxy.data.api.GitHubApi
import com.damolks.ouxy.data.api.ModuleStorageApi
import com.damolks.ouxy.data.dao.ModuleDao
import com.damolks.ouxy.data.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideGitHubApi(retrofit: Retrofit): GitHubApi {
        return retrofit.create(GitHubApi::class.java)
    }
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "ouxy_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideModuleDao(database: AppDatabase): ModuleDao {
        return database.moduleDao()
    }

    @Provides
    @Singleton
    fun provideModuleStorageApi(@ApplicationContext context: Context, moduleId: String): ModuleStorageApi {
        return ModuleStorageApi(context, moduleId)
    }
}