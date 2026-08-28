package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.TestSessionDao
import com.example.data.entity.AthleteResultEntity
import com.example.data.entity.TestSessionEntity

@Database(
    entities = [
        TestSessionEntity::class,
        AthleteResultEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class YoYoDatabase : RoomDatabase() {
    abstract fun testSessionDao(): TestSessionDao

    companion object {
        @Volatile
        private var INSTANCE: YoYoDatabase? = null

        fun getDatabase(context: Context): YoYoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    YoYoDatabase::class.java,
                    "yoyo_ir1_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
