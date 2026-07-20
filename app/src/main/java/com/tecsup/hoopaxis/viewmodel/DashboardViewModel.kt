package com.tecsup.hoopaxis.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tecsup.hoopaxis.data.model.Chapter
import com.tecsup.hoopaxis.data.model.RecentChapter
import com.tecsup.hoopaxis.data.model.RuleCategory
import com.tecsup.hoopaxis.data.model.User
import com.tecsup.hoopaxis.data.repository.RuleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * UI State for the Dashboard screen
 */
data class DashboardUiState(
    val user: User? = null,
    val categories: List<RuleCategory> = emptyList(),
    val recentChapters: List<RecentChapter> = emptyList(),
    val allChapters: List<Chapter> = emptyList(),
    val isLoading: Boolean = false,
    val greeting: String = "Buenos días"
)

class DashboardViewModel(private val repository: RuleRepository) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    private val _recentChapters = MutableStateFlow<List<RecentChapter>>(emptyList())
    private val _allChapters = MutableStateFlow<List<Chapter>>(emptyList())

    val uiState: StateFlow<DashboardUiState> = combine(
        repository.currentUser,
        repository.allCategories,
        _isLoading,
        _recentChapters,
        _allChapters
    ) { user, categories, isLoading, recentChapters, allChapters ->
        DashboardUiState(
            user = user,
            categories = categories,
            recentChapters = recentChapters,
            allChapters = allChapters,
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
            
            // Las categorías y datos iniciales se cargan normalmente
            repository.syncCategories(
                listOf(
                    RuleCategory(1, "El Juego", "Definición, objeto y estructura del partido", 2, 4, 0.75f, "🏀", "#9D50BB"),
                    RuleCategory(2, "Terreno y Equipamiento", "Dimensiones, líneas y material oficial", 2, 4, 0.50f, "🏟️", "#4776E6"),
                    RuleCategory(3, "Los Equipos", "Composición, roles y responsabilidades", 2, 4, 0.25f, "👥", "#E91E63"),
                    RuleCategory(4, "Jugadores y Uniformes", "Requisitos de indumentaria y números", 2, 4, 0.00f, "👕", "#FF9800"),
                    RuleCategory(5, "Reglas de Juego", "Posesión, anotación y procedimientos", 2, 4, 0.00f, "⚡", "#00F2FE"),
                    RuleCategory(6, "Violaciones", "Pasos, dobles, fuera de banda y tiempos", 2, 4, 0.75f, "🚫", "#FF5722"),
                    RuleCategory(7, "Faltas", "Personales, técnicas y antideportivas", 2, 4, 0.25f, "🚨", "#F44336"),
                    RuleCategory(8, "Procedimientos Generales", "Tiempos muertos, árbitros y situaciones especiales", 2, 4, 0.00f, "📋", "#607D8B")
                )
            )

            _recentChapters.value = listOf(
                RecentChapter(1, "Cap. 2 — Duración y Períodos", "El Juego", 0.5f, "⏰"),
                RecentChapter(2, "Cap. 5 — Composición del Equipo", "Los Equipos", 0.5f, "🧩"),
                RecentChapter(3, "Cap. 12 — Reglas de Tiempo", "Violaciones", 0.5f, "⏰")
            )

            _allChapters.value = listOf(
                Chapter(1, 1, 1, "Definición y Objetivos", "El Juego", 2, 1.0f, false, "#9D50BB"),
                Chapter(2, 1, 2, "Duración y Períodos", "El Juego", 2, 0.5f, true, "#9D50BB"),
                Chapter(3, 2, 3, "Dimensiones del Terreno", "Terreno y Equipamiento", 2, 1.0f, false, "#4776E6"),
                Chapter(4, 2, 4, "Equipamiento Oficial", "Terreno y Equipamiento", 2, 0.00f, true, "#4776E6"),
                Chapter(5, 3, 5, "Composición del Equipo", "Los Equipos", 2, 0.5f, false, "#E91E63"),
                Chapter(6, 3, 6, "Capitán y Entrenador", "Los Equipos", 2, 0.0f, true, "#E91E63"),
                Chapter(7, 4, 7, "Uniformes Reglamentarios", "Jugadores y Uniformes", 2, 0.0f, true, "#FF9800"),
                Chapter(8, 4, 8, "Sustituciones", "Jugadores y Uniformes", 2, 0.0f, true, "#FF9800"),
                Chapter(9, 5, 9, "Salto y Posesión Alterna", "Reglas de Juego", 2, 0.0f, false, "#00F2FE"),
                Chapter(10, 5, 10, "Anotación y Marcador", "Reglas de Juego", 2, 0.0f, true, "#00F2FE"),
                Chapter(11, 6, 11, "Pasos y Dobles", "Violaciones", 2, 1.0f, false, "#FF5722"),
                Chapter(12, 6, 12, "Reglas de Tiempo", "Violaciones", 2, 0.5f, true, "#FF5722"),
                Chapter(13, 7, 13, "Faltas Personales", "Faltas", 2, 0.5f, false, "#F44336"),
                Chapter(14, 7, 14, "Faltas Técnicas y Descalificación", "Faltas", 2, 0.0f, true, "#F44336"),
                Chapter(15, 8, 15, "Tiempos Muertos", "Procedimientos Generales", 2, 0.0f, true, "#607D8B"),
                Chapter(16, 8, 16, "Árbitros y Mesa", "Procedimientos Generales", 2, 0.0f, true, "#607D8B")
            )

            _isLoading.value = false
        }
    }

    fun updateUserName(newName: String) {
        viewModelScope.launch {
            val user = uiState.value.user
            user?.let {
                repository.login(it.copy(name = newName))
            }
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
