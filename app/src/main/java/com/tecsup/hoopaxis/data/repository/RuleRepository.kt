package com.tecsup.hoopaxis.data.repository

import com.tecsup.hoopaxis.data.local.HoopAxisDao
import com.tecsup.hoopaxis.data.model.Article
import com.tecsup.hoopaxis.data.model.Chapter
import com.tecsup.hoopaxis.data.model.Rule
import com.tecsup.hoopaxis.data.model.User
import com.tecsup.hoopaxis.data.model.QuizQuestion
import com.tecsup.hoopaxis.data.remote.FirebaseDataSource
import kotlinx.coroutines.flow.Flow

class RuleRepository(
    private val dao: HoopAxisDao,
    private val remote: FirebaseDataSource
) {
    val currentUser: Flow<User?> = dao.getCurrentUser()
    val allRules: Flow<List<Rule>> = dao.getAllRules()
    val allChapters: Flow<List<Chapter>> = dao.getAllChapters()
    val allArticles: Flow<List<Article>> = dao.getAllArticles()
    val allQuizQuestions: Flow<List<QuizQuestion>> = dao.getAllQuizQuestions()

    suspend fun login(user: User) {
        dao.insertUser(user)
    }

    suspend fun logout() {
        dao.logout()
    }

    // Admin CRUD Operations (Local + Remote)
    suspend fun addRule(rule: Rule) {
        dao.insertRule(rule) // Primero local para que el admin vea el cambio rápido
        try {
            remote.saveRule(rule)
        } catch (e: Exception) {
            android.util.Log.e("RuleRepo", "Error al subir regla: ${e.message}")
        }
    }
    suspend fun updateRule(rule: Rule) {
        dao.updateRule(rule)
        try {
            remote.saveRule(rule)
        } catch (e: Exception) {
            android.util.Log.e("RuleRepo", "Error al actualizar regla: ${e.message}")
        }
    }
    suspend fun deleteRule(rule: Rule) {
        dao.deleteRule(rule)
        try {
            remote.deleteRule(rule.id)
        } catch (e: Exception) {
            android.util.Log.e("RuleRepo", "Error al eliminar regla: ${e.message}")
        }
    }

    suspend fun addChapter(chapter: Chapter) {
        dao.insertChapter(chapter)
        try {
            remote.saveChapter(chapter)
        } catch (e: Exception) {
            android.util.Log.e("RuleRepo", "Error al subir capítulo: ${e.message}")
        }
    }
    suspend fun updateChapter(chapter: Chapter) {
        dao.updateChapter(chapter)
        try {
            remote.saveChapter(chapter)
        } catch (e: Exception) {
            android.util.Log.e("RuleRepo", "Error al actualizar capítulo: ${e.message}")
        }
    }
    suspend fun deleteChapter(chapter: Chapter) {
        dao.deleteChapter(chapter)
        try {
            remote.deleteChapter(chapter.id)
        } catch (e: Exception) {
            android.util.Log.e("RuleRepo", "Error al eliminar capítulo: ${e.message}")
        }
    }

    suspend fun addArticle(article: Article) {
        dao.insertArticle(article)
        try {
            remote.saveArticle(article)
        } catch (e: Exception) {
            android.util.Log.e("RuleRepo", "Error al subir artículo: ${e.message}")
        }
    }
    suspend fun updateArticle(article: Article) {
        dao.updateArticle(article)
        try {
            remote.saveArticle(article)
        } catch (e: Exception) {
            android.util.Log.e("RuleRepo", "Error al actualizar artículo: ${e.message}")
        }
    }
    suspend fun deleteArticle(article: Article) {
        dao.deleteArticle(article)
        try {
            remote.deleteArticle(article.id)
        } catch (e: Exception) {
            android.util.Log.e("RuleRepo", "Error al eliminar artículo: ${e.message}")
        }
    }

    suspend fun addQuizQuestion(question: QuizQuestion) {
        dao.insertQuizQuestion(question)
        try {
            remote.saveQuizQuestion(question)
        } catch (e: Exception) {
            android.util.Log.e("RuleRepo", "Error al subir pregunta: ${e.message}")
        }
    }
    suspend fun updateQuizQuestion(question: QuizQuestion) {
        dao.updateQuizQuestion(question)
        try {
            remote.saveQuizQuestion(question)
        } catch (e: Exception) {
            android.util.Log.e("RuleRepo", "Error al actualizar pregunta: ${e.message}")
        }
    }
    suspend fun deleteQuizQuestion(question: QuizQuestion) {
        dao.deleteQuizQuestion(question)
        try {
            remote.deleteQuizQuestion(question.id)
        } catch (e: Exception) {
            android.util.Log.e("RuleRepo", "Error al eliminar pregunta: ${e.message}")
        }
    }

    // Sync from Firebase to Local
    suspend fun syncFromRemote() {
        try {
            val rules = remote.getRules()
            if (rules.isNotEmpty()) dao.insertRules(rules)

            val chapters = remote.getChapters()
            if (chapters.isNotEmpty()) dao.insertChapters(chapters)

            val articles = remote.getArticles()
            if (articles.isNotEmpty()) dao.insertArticles(articles)

            val questions = remote.getQuizQuestions()
            if (questions.isNotEmpty()) {
                // We need an insert method for multiple quiz questions
                questions.forEach { dao.insertQuizQuestion(it) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Existing Sync methods
    suspend fun syncRules(rules: List<Rule>) {
        dao.insertRules(rules)
    }

    fun getChaptersByRule(ruleId: String): Flow<List<Chapter>> = dao.getChaptersByRule(ruleId)

    suspend fun syncChapters(chapters: List<Chapter>) {
        dao.insertChapters(chapters)
    }

    fun getArticlesByChapter(chapterId: String): Flow<List<Article>> = dao.getArticlesByChapter(chapterId)

    suspend fun getArticleById(articleId: String): Article? = dao.getArticleById(articleId)

    suspend fun syncArticles(articles: List<Article>) {
        dao.insertArticles(articles)
    }
}
