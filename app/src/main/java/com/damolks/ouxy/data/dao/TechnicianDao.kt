package com.damolks.ouxy.data.dao

import androidx.room.*
import com.damolks.ouxy.data.model.Technician
import kotlinx.coroutines.flow.Flow

@Dao
interface TechnicianDao {
    @Query("SELECT * FROM technicians LIMIT 1")
    fun getTechnician(): Flow<Technician?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTechnician(technician: Technician)

    @Query("DELETE FROM technicians")
    suspend fun deleteTechnician()

    @Query("SELECT COUNT(*) FROM technicians")
    suspend fun hasTechnician(): Int
}