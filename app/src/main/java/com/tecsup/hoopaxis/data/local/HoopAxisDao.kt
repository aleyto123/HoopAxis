package com.tecsup.hoopaxis.data.local

import androidx.room.*
import com.tecsup.hoopaxis.data.model.Article
import com.tecsup.hoopaxis.data.model.Chapter
import com.tecsup.hoopaxis.data.model.Rule
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

    @Query("SELECT * FROM reglas ORDER BY number ASC")
    fun getAllRules(): Flow<List<Rule>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRules(rules: List<Rule>)

    @Query("SELECT * FROM capitulos WHERE ruleId = :ruleId ORDER BY number ASC")
    fun getChaptersByRule(ruleId: String): Flow<List<Chapter>>

    @Query("SELECT * FROM capitulos ORDER BY number ASC")
    fun getAllChapters(): Flow<List<Chapter>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapters(chapters: List<Chapter>)

    @Query("SELECT * FROM articulos WHERE chapterId = :chapterId")
    fun getArticlesByChapter(chapterId: String): Flow<List<Article>>

    @Query("SELECT * FROM articulos WHERE id = :articleId")
    suspend fun getArticleById(articleId: String): Article?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<Article>)
}
