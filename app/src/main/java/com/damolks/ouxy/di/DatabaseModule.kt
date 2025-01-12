package com.damolks.ouxy.di

import android.content.Context
import androidx.room.Room
import com.damolks.ouxy.data.dao.ModuleDao
import com.damolks.ouxy.data.dao.TechnicianDao
import com.damolks.ouxy.data.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "ouxy-database"
        )
        .fallbackToDestructiveMigration() // Permettre la reconstruction de la base si le schéma change
        .build()
    }

    @Provides
    fun provideTechnicianDao(database: AppDatabase): TechnicianDao {
        return database.technicianDao()
    }

    @Provides
    fun provideModuleDao(database: AppDatabase): ModuleDao {
        return database.moduleDao()
    }
}