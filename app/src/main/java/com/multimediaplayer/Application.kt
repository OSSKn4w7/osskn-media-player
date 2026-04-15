package com.multimediaplayer

import android.app.Application
import com.multimediaplayer.data.local.MediaDatabase

class Application : Application() {
    private lateinit var database: MediaDatabase
    
    override fun onCreate() {
        super.onCreate()
        database = MediaDatabase.getDatabase(this)
    }
    
    fun getDatabase(): MediaDatabase {
        return database
    }
}