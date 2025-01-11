package com.damolks.ouxy.di

import android.content.Context
import com.damolks.ouxy.data.api.ModuleStorageApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ModuleId

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    @Provides
    @ModuleId
    fun provideModuleId(): String = "default"

    @Provides
    fun provideModuleStorageApi(
        @ApplicationContext context: Context,
        @ModuleId moduleId: String
    ): ModuleStorageApi {
        return ModuleStorageApi(context, moduleId)
    }
}