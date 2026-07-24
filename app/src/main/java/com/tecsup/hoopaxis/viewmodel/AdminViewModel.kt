package com.tecsup.hoopaxis.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tecsup.hoopaxis.data.model.Article
import com.tecsup.hoopaxis.data.model.Chapter
import com.tecsup.hoopaxis.data.model.Rule
import com.tecsup.hoopaxis.data.model.QuizQuestion
import com.tecsup.hoopaxis.data.repository.RuleRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import android.util.Log

class AdminViewModel(private val repository: RuleRepository) : ViewModel() {

    val rules: StateFlow<List<Rule>> = repository.allRules
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chapters: StateFlow<List<Chapter>> = repository.allChapters
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val quizQuestions: StateFlow<List<QuizQuestion>> = repository.allQuizQuestions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val articles: StateFlow<List<Article>> = repository.allArticles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Rules
    fun addRule(rule: Rule) = viewModelScope.launch { repository.addRule(rule) }
    fun updateRule(rule: Rule) = viewModelScope.launch { repository.updateRule(rule) }
    fun deleteRule(rule: Rule) = viewModelScope.launch { repository.deleteRule(rule) }

    // Chapters
    fun addChapter(chapter: Chapter) = viewModelScope.launch { repository.addChapter(chapter) }
    fun updateChapter(chapter: Chapter) = viewModelScope.launch { repository.updateChapter(chapter) }
    fun deleteChapter(chapter: Chapter) = viewModelScope.launch { repository.deleteChapter(chapter) }

    // Articles
    fun addArticle(article: Article) = viewModelScope.launch { repository.addArticle(article) }
    fun updateArticle(article: Article) = viewModelScope.launch { repository.updateArticle(article) }
    fun deleteArticle(article: Article) = viewModelScope.launch { repository.deleteArticle(article) }

    // Quiz Questions
    fun addQuizQuestion(question: QuizQuestion) = viewModelScope.launch { repository.addQuizQuestion(question) }
    fun updateQuizQuestion(question: QuizQuestion) = viewModelScope.launch { repository.updateQuizQuestion(question) }
    fun deleteQuizQuestion(question: QuizQuestion) = viewModelScope.launch { repository.deleteQuizQuestion(question) }

    fun syncRemote() = viewModelScope.launch {
        try {
            repository.syncFromRemote()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun pushToCloud() = viewModelScope.launch {
        try {
            Log.d("AdminVM", "Iniciando subida masiva a la nube...")
            // Sincronizar reglas
            repository.allRules.first().forEach { 
                Log.d("AdminVM", "Subiendo regla: ${it.title}")
                repository.addRule(it) 
            }
            // Sincronizar capítulos
            repository.allChapters.first().forEach { 
                Log.d("AdminVM", "Subiendo capítulo: ${it.title}")
                repository.addChapter(it) 
            }
            // Sincronizar artículos
            repository.allArticles.first().forEach { 
                Log.d("AdminVM", "Subiendo artículo: ${it.title}")
                repository.addArticle(it) 
            }
            // Sincronizar preguntas
            repository.allQuizQuestions.first().forEach { 
                Log.d("AdminVM", "Subiendo pregunta: ${it.question}")
                repository.addQuizQuestion(it) 
            }
            Log.d("AdminVM", "Subida finalizada con éxito")
        } catch (e: Exception) {
            Log.e("AdminVM", "Error en subida masiva: ${e.message}")
            e.printStackTrace()
        }
    }

    companion object {
        fun provideFactory(repository: RuleRepository): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AdminViewModel(repository) as T
            }
        }
    }
}
