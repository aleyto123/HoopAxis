package com.tecsup.hoopaxis.data.repository

import com.tecsup.hoopaxis.data.local.HoopAxisDao
import com.tecsup.hoopaxis.data.model.Article
import com.tecsup.hoopaxis.data.model.Chapter
import com.tecsup.hoopaxis.data.model.Rule
import com.tecsup.hoopaxis.data.model.User
import kotlinx.coroutines.flow.Flow

class RuleRepository(private val dao: HoopAxisDao) {
    val currentUser: Flow<User?> = dao.getCurrentUser()
    val allRules: Flow<List<Rule>> = dao.getAllRules()
    val allChapters: Flow<List<Chapter>> = dao.getAllChapters()

    suspend fun login(user: User) {
        dao.insertUser(user)
    }

    suspend fun logout() {
        dao.logout()
    }

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
