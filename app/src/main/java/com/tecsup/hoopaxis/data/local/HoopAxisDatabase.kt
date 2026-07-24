package com.tecsup.hoopaxis.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tecsup.hoopaxis.data.model.Article
import com.tecsup.hoopaxis.data.model.Chapter
import com.tecsup.hoopaxis.data.model.Rule
import com.tecsup.hoopaxis.data.model.User
import com.tecsup.hoopaxis.data.model.QuizQuestion

@Database(entities = [User::class, Rule::class, Chapter::class, Article::class, QuizQuestion::class], version = 5, exportSchema = false)
@TypeConverters(Converters::class)
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
                )
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
