package com.tecsup.hoopaxis.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.tecsup.hoopaxis.data.model.*
import kotlinx.coroutines.tasks.await

class FirebaseDataSource {
    private val db get() = com.google.firebase.firestore.FirebaseFirestore.getInstance()

    // Reglas
    suspend fun getRules(): List<Rule> {
        return try {
            db.collection("reglas").get().await().toObjects(Rule::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun saveRule(rule: Rule) {
        db.collection("reglas").document(rule.id).set(rule).await()
    }

    suspend fun deleteRule(ruleId: String) {
        db.collection("reglas").document(ruleId).delete().await()
    }

    // Capítulos
    suspend fun getChapters(): List<Chapter> {
        return db.collection("capitulos").get().await().toObjects(Chapter::class.java)
    }

    suspend fun saveChapter(chapter: Chapter) {
        db.collection("capitulos").document(chapter.id).set(chapter).await()
    }

    suspend fun deleteChapter(chapterId: String) {
        db.collection("capitulos").document(chapterId).delete().await()
    }

    // Artículos
    suspend fun getArticles(): List<Article> {
        return db.collection("articulos").get().await().toObjects(Article::class.java)
    }

    suspend fun saveArticle(article: Article) {
        db.collection("articulos").document(article.id).set(article).await()
    }

    suspend fun deleteArticle(articleId: String) {
        db.collection("articulos").document(articleId).delete().await()
    }

    // Quiz Questions
    suspend fun getQuizQuestions(): List<QuizQuestion> {
        return db.collection("quiz_questions").get().await().toObjects(QuizQuestion::class.java)
    }

    suspend fun saveQuizQuestion(question: QuizQuestion) {
        db.collection("quiz_questions").document(question.id).set(question).await()
    }

    suspend fun deleteQuizQuestion(questionId: String) {
        db.collection("quiz_questions").document(questionId).delete().await()
    }
}
