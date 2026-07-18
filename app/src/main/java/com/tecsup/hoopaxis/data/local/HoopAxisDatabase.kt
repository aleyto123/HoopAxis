package com.tecsup.hoopaxis.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tecsup.hoopaxis.data.model.RuleCategory
import com.tecsup.hoopaxis.data.model.User

@Database(entities = [User::class, RuleCategory::class], version = 1, exportSchema = false)
abstract class HoopAxisDatabase : RoomDatabase() {
    abstract fun dao(): HoopAxisDao

    companion object {
        @Volatile
        private var INSTANCE: HoopAxisDatabase? = null

        fun getDatabase(context: Context): HoopAxisDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HoopAxisDatabase::class.java,
                    "hoopaxis_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
