package com.tecsup.hoopaxis.data.local

import androidx.room.*
import com.tecsup.hoopaxis.data.model.Article
import com.tecsup.hoopaxis.data.model.Chapter
import com.tecsup.hoopaxis.data.model.Rule
import com.tecsup.hoopaxis.data.model.User
import com.tecsup.hoopaxis.data.model.QuizQuestion
import kotlinx.coroutines.flow.Flow

@Dao
interface HoopAxisDao {
    @Query("SELECT * FROM users LIMIT 1")
    fun getCurrentUser(): Flow<User?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("DELETE FROM users")
    suspend fun logout()

    // Reglas
    @Query("SELECT * FROM reglas ORDER BY number ASC")
    fun getAllRules(): Flow<List<Rule>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRule(rule: Rule)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRules(rules: List<Rule>)

    @Update
    suspend fun updateRule(rule: Rule)

    @Delete
    suspend fun deleteRule(rule: Rule)

    // Capítulos
    @Query("SELECT * FROM capitulos WHERE ruleId = :ruleId ORDER BY number ASC")
    fun getChaptersByRule(ruleId: String): Flow<List<Chapter>>

    @Query("SELECT * FROM capitulos ORDER BY number ASC")
    fun getAllChapters(): Flow<List<Chapter>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapter(chapter: Chapter)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapters(chapters: List<Chapter>)

    @Update
    suspend fun updateChapter(chapter: Chapter)

    @Delete
    suspend fun deleteChapter(chapter: Chapter)

    // Artículos
    @Query("SELECT * FROM articulos")
    fun getAllArticles(): Flow<List<Article>>

    @Query("SELECT * FROM articulos WHERE chapterId = :chapterId")
    fun getArticlesByChapter(chapterId: String): Flow<List<Article>>

    @Query("SELECT * FROM articulos WHERE id = :articleId")
    suspend fun getArticleById(articleId: String): Article?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: Article)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<Article>)

    @Update
    suspend fun updateArticle(article: Article)

    @Delete
    suspend fun deleteArticle(article: Article)

    // Quiz Questions
    @Query("SELECT * FROM quiz_questions")
    fun getAllQuizQuestions(): Flow<List<QuizQuestion>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizQuestion(question: QuizQuestion)

    @Update
    suspend fun updateQuizQuestion(question: QuizQuestion)

    @Delete
    suspend fun deleteQuizQuestion(question: QuizQuestion)
}
