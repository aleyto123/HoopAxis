package com.tecsup.hoopaxis

import android.app.Application
import com.tecsup.hoopaxis.data.local.HoopAxisDatabase
import com.tecsup.hoopaxis.data.repository.RuleRepository
import com.tecsup.hoopaxis.data.remote.FirebaseDataSource
import com.google.firebase.FirebaseApp

class HoopAxisApplication : Application() {
    // Singleton pattern for Database and Repository
    private val database by lazy { HoopAxisDatabase.getDatabase(this) }
    private val firebaseDataSource by lazy { FirebaseDataSource() }
    val repository by lazy { RuleRepository(database.dao(), firebaseDataSource) }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
