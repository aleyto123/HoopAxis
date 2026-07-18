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

/**
 * UI State for the Dashboard screen
 */
data class DashboardUiState(
    val user: User? = null,
    val categories: List<RuleCategory> = emptyList(),
    val isLoading: Boolean = false,
    val greeting: String = "Buenos días"
)

class DashboardViewModel(private val repository: RuleRepository) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)

    val uiState: StateFlow<DashboardUiState> = combine(
        repository.currentUser,
        repository.allCategories,
        _isLoading
    ) { user, categories, isLoading ->
        DashboardUiState(
            user = user,
            categories = categories,
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
            // Mocking a small delay and check if categories exist
            // In a real app, this would be a sync with API
            repository.syncCategories(
                listOf(
                    RuleCategory(1, "El Juego", 2, 0.75f, "🏀"),
                    RuleCategory(2, "Terreno y Equipamiento", 2, 0.50f, "🏟️"),
                    RuleCategory(3, "Los Equipos", 2, 0.25f, "👥"),
                    RuleCategory(4, "Jugadores y Uniformes", 2, 0.00f, "👕")
                )
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
