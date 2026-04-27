package com.osskn.mediaplayer

import android.app.Application
import androidx.room.Room
import com.osskn.mediaplayer.data.local.AppDatabase

class OssknMediaPlayerApp : Application() {
    val database: AppDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).build()
    }

    companion object {
        lateinit var instance: OssknMediaPlayerApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
