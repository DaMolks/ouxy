package com.damolks.ouxy.data.dao

import androidx.room.*
import com.damolks.ouxy.data.model.InstalledModule
import kotlinx.coroutines.flow.Flow

@Dao
interface ModuleDao {
    @Query("SELECT * FROM installed_modules ORDER BY installDate DESC")
    fun getAllModules(): Flow<List<InstalledModule>>

    @Query("SELECT * FROM installed_modules WHERE id = :moduleId")
    suspend fun getModuleById(moduleId: String): InstalledModule?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModule(module: InstalledModule)

    @Delete
    suspend fun deleteModule(module: InstalledModule)

    @Query("DELETE FROM installed_modules WHERE id = :moduleId")
    suspend fun deleteModuleById(moduleId: String)
}