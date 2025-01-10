package com.damolks.ouxy.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.damolks.ouxy.data.dao.TechnicianDao
import com.damolks.ouxy.data.model.Technician

@Database(entities = [Technician::class], version = 1)
abstract class OuxyDatabase : RoomDatabase() {

    abstract fun technicianDao(): TechnicianDao

    companion object {
        @Volatile
        private var INSTANCE: OuxyDatabase? = null

        fun getDatabase(context: Context): OuxyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    OuxyDatabase::class.java,
                    "ouxy_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}