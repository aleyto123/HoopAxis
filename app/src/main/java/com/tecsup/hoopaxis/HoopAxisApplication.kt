package com.tecsup.hoopaxis

import android.app.Application
import com.tecsup.hoopaxis.data.local.HoopAxisDatabase
import com.tecsup.hoopaxis.data.repository.RuleRepository

class HoopAxisApplication : Application() {
    // Singleton pattern for Database and Repository
    private val database by lazy { HoopAxisDatabase.getDatabase(this) }
    val repository by lazy { RuleRepository(database.dao()) }

    override fun onCreate() {
        super.onCreate()
    }
}
