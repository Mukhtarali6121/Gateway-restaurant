package com.example.gatewayrestaurant.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.gatewayrestaurant.RoomInterface.MenuDao
import com.example.gatewayrestaurant.RoomModel.MenuEntity

@Database(entities = [User::class, MenuEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun menuDao(): MenuDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration() // Use this if you want to handle migrations more easily during development
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}


