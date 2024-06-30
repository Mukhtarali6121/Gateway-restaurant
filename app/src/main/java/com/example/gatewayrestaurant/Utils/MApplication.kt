package com.example.gatewayrestaurant.Utils

import android.app.Application
import com.example.gatewayrestaurant.room.AppDatabase

class MyApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
}
