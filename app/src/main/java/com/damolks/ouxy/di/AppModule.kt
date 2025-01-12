package com.damolks.ouxy.di

import android.content.Context
import androidx.room.Room
import com.damolks.ouxy.data.api.GitHubApi
import com.damolks.ouxy.data.api.ModuleStorageApi
import com.damolks.ouxy.data.dao.ModuleDao
import com.damolks.ouxy.data.dao.TechnicianDao
import com.damolks.ouxy.data.database.AppDatabase
import com.damolks.ouxy.data.repository.MarketplaceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ModuleId

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

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
    fun provideTechnicianDao(database: AppDatabase): TechnicianDao {
        return database.technicianDao()
    }

    @Provides
    @Singleton
    fun provideModuleDao(database: AppDatabase): ModuleDao {
        return database.moduleDao()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideGitHubApi(retrofit: Retrofit): GitHubApi {
        return retrofit.create(GitHubApi::class.java)
    }

    @Provides
    @ModuleId
    fun provideDefaultModuleId(): String = "default"

    @Provides
    @Singleton
    fun provideModuleStorageApi(
        @ApplicationContext context: Context,
        @ModuleId moduleId: String
    ): ModuleStorageApi {
        return ModuleStorageApi(context, moduleId)
    }

    @Provides
    @Singleton
    fun provideMarketplaceRepository(gitHubApi: GitHubApi): MarketplaceRepository {
        return MarketplaceRepository(gitHubApi)
    }
}