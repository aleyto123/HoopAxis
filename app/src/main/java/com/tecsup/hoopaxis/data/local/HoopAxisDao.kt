package com.tecsup.hoopaxis.data.local

import androidx.room.*
import com.tecsup.hoopaxis.data.model.RuleCategory
import com.tecsup.hoopaxis.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface HoopAxisDao {
    @Query("SELECT * FROM users LIMIT 1")
    fun getCurrentUser(): Flow<User?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("DELETE FROM users")
    suspend fun logout()

    @Query("SELECT * FROM rule_categories")
    fun getAllCategories(): Flow<List<RuleCategory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<RuleCategory>)
}
