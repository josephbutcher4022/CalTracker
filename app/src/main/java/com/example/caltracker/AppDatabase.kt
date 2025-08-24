package com.example.caltracker

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [MealEntity::class, FoodEntity::class, DailyTotalEntity::class], version = 10, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mealDao(): MealDao
    abstract fun foodDao(): FoodDao
    abstract fun dailyTotalDao(): DailyTotalDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_10 = object : Migration(1, 10) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Empty migration - no schema changes
            }
        }

        private val MIGRATION_7_10 = object : Migration(7, 10) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Empty migration - no schema changes
            }
        }

        private val MIGRATION_8_10 = object : Migration(8, 10) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Empty migration - no schema changes
            }
        }

        private val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Empty migration - no schema changes
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "caltracker_database"
                )
                    .addMigrations(MIGRATION_1_10, MIGRATION_7_10, MIGRATION_8_10, MIGRATION_9_10)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}