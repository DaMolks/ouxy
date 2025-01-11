package com.damolks.ouxy.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.damolks.ouxy.data.dao.ModuleDao
import com.damolks.ouxy.data.dao.TechnicianDao
import com.damolks.ouxy.data.model.InstalledModule
import com.damolks.ouxy.data.model.Technician

@Database(
    entities = [
        Technician::class,
        InstalledModule::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun technicianDao(): TechnicianDao
    abstract fun moduleDao(): ModuleDao
}