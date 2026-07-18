package com.tecsup.hoopaxis.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tecsup.hoopaxis.data.model.RuleCategory
import com.tecsup.hoopaxis.data.model.User
import com.tecsup.hoopaxis.data.repository.RuleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import com.tecsup.hoopaxis.data.model.RecentChapter

/**
 * UI State for the Dashboard screen
 */
data class DashboardUiState(
    val user: User? = null,
    val categories: List<RuleCategory> = emptyList(),
    val recentChapters: List<RecentChapter> = emptyList(),
    val isLoading: Boolean = false,
    val greeting: String = "Buenos días"
)

class DashboardViewModel(private val repository: RuleRepository) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    private val _recentChapters = MutableStateFlow<List<RecentChapter>>(emptyList())

    val uiState: StateFlow<DashboardUiState> = combine(
        repository.currentUser,
        repository.allCategories,
        _isLoading,
        _recentChapters
    ) { user, categories, isLoading, recentChapters ->
        DashboardUiState(
            user = user,
            categories = categories,
            recentChapters = recentChapters,
            isLoading = isLoading
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState(isLoading = true)
    )

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _isLoading.value = true
            
            repository.syncCategories(
                listOf(
                    RuleCategory(1, "El Juego", 2, 0.75f, "🏀"),
                    RuleCategory(2, "Terreno y Equipamiento", 2, 0.50f, "🏟️"),
                    RuleCategory(3, "Los Equipos", 2, 0.25f, "👥"),
                    RuleCategory(4, "Jugadores y Uniformes", 2, 0.00f, "👕"),
                    RuleCategory(5, "Reglas de Juego", 2, 0.00f, "⚡"),
                    RuleCategory(6, "Violaciones", 2, 0.75f, "🚫"),
                    RuleCategory(7, "Faltas", 2, 0.25f, "🚨"),
                    RuleCategory(8, "Procedimientos Generales", 2, 0.00f, "📋")
                )
            )

            _recentChapters.value = listOf(
                RecentChapter(1, "Cap. 2 — Duración y Períodos", "El Juego", 0.5f, "⏰"),
                RecentChapter(2, "Cap. 5 — Composición del Equipo", "Los Equipos", 0.5f, "🧩"),
                RecentChapter(3, "Cap. 12 — Reglas de Tiempo", "Violaciones", 0.5f, "⏰")
            )

            _isLoading.value = false
        }
    }

    // ViewModel Factory to inject the singleton repository
    companion object {
        fun provideFactory(repository: RuleRepository): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return DashboardViewModel(repository) as T
            }
        }
    }
}
