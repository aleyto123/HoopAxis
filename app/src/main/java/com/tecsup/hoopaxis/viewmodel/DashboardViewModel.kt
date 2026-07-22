package com.tecsup.hoopaxis.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tecsup.hoopaxis.data.model.Article
import com.tecsup.hoopaxis.data.model.Chapter
import com.tecsup.hoopaxis.data.model.Rule
import com.tecsup.hoopaxis.data.model.User
import com.tecsup.hoopaxis.data.repository.RuleRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class DashboardUiState(
    val user: User? = null,
    val rules: List<Rule> = emptyList(),
    val allChapters: List<Chapter> = emptyList(),
    val isLoading: Boolean = false
)

class DashboardViewModel(private val repository: RuleRepository) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    private val _selectedRuleId = MutableStateFlow<String?>(null)
    private val _selectedChapterId = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val chapters: StateFlow<List<Chapter>> = _selectedRuleId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList())
        else if (id == "all") repository.allChapters
        else repository.getChaptersByRule(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val articles: StateFlow<List<Article>> = _selectedChapterId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList())
        else repository.getArticlesByChapter(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val uiState: StateFlow<DashboardUiState> = combine(
        repository.currentUser,
        repository.allRules,
        repository.allChapters,
        _isLoading
    ) { user, rules, chapters, isLoading ->
        DashboardUiState(
            user = user,
            rules = rules,
            allChapters = chapters,
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

    fun selectRule(ruleId: String) {
        _selectedRuleId.value = ruleId
    }

    fun selectChapter(chapterId: String) {
        _selectedChapterId.value = chapterId
    }

    suspend fun getArticle(articleId: String): Article? {
        return repository.getArticleById(articleId)
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _isLoading.value = true
            
            // 8 Rules
            val rules = listOf(
                Rule("r1", 1, "El Juego", "Definición, objeto y estructura del partido", "🏀", "#C96BFF", "#C96BFF44", 2, 0.75f),
                Rule("r2", 2, "Terreno y Equipamiento", "Dimensiones, líneas y material oficial", "🏟️", "#5BC8FF", "#5BC8FF44", 2, 0.50f),
                Rule("r3", 3, "Los Equipos", "Composición, roles y responsabilidades", "👥", "#FF6B9D", "#FF6B9D44", 2, 0.25f),
                Rule("r4", 4, "Jugadores y Uniformes", "Requisitos de indumentaria y números", "👕", "#FFD166", "#FFD16644", 2, 0.00f),
                Rule("r5", 5, "Reglas de Juego", "Posesión, anotación y procedimientos", "⚡", "#50DC78", "#50DC7844", 2, 0.00f),
                Rule("r6", 6, "Violaciones", "Pasos, dobles, fuera de banda y tiempos", "🚫", "#FF9F43", "#FF9F4344", 2, 0.75f),
                Rule("r7", 7, "Faltas", "Personales, técnicas y antideportivas", "🚨", "#FF6B6B", "#FF6B6B44", 2, 0.25f),
                Rule("r8", 8, "Procedimientos Generales", "Tiempos muertos, árbitros y situaciones especiales", "📋", "#A29BFE", "#A29BFE44", 2, 0.00f)
            )
            repository.syncRules(rules)

            // 16 Chapters (2 per Rule)
            val chapters = listOf(
                Chapter("c1", "r1", 1, "Definición y Objetivos", "📌", 2, 1.0f),
                Chapter("c2", "r1", 2, "Duración y Períodos", "⏱️", 2, 0.5f),
                Chapter("c3", "r2", 3, "Dimensiones del Terreno", "📐", 2, 1.0f),
                Chapter("c4", "r2", 4, "Equipamiento Oficial", "⚙️", 2, 0.0f),
                Chapter("c5", "r3", 5, "Composición del Equipo", "🧩", 2, 0.5f),
                Chapter("c6", "r3", 6, "Capitán y Entrenador", "🎯", 2, 0.0f),
                Chapter("c7", "r4", 7, "Uniformes Reglamentarios", "🎽", 2, 0.0f),
                Chapter("c8", "r4", 8, "Sustituciones", "🔄", 2, 0.0f),
                Chapter("c9", "r5", 9, "Salto y Posesión Alterna", "🤾", 2, 0.0f),
                Chapter("c10", "r5", 10, "Anotación y Marcador", "🎯", 2, 0.0f),
                Chapter("c11", "r6", 11, "Pasos y Dobles", "🦶", 2, 1.0f),
                Chapter("c12", "r6", 12, "Reglas de Tiempo", "⏰", 2, 0.5f),
                Chapter("c13", "r7", 13, "Faltas Personales", "🤚", 2, 0.5f),
                Chapter("c14", "r7", 14, "Faltas Técnicas y Descalificación", "📋", 2, 0.0f),
                Chapter("c15", "r8", 15, "Tiempos Muertos", "🛑", 2, 0.0f),
                Chapter("c16", "r8", 16, "Árbitros y Mesa", "👨‍⚖️", 2, 0.0f)
            )
            repository.syncChapters(chapters)

            // Articles distributed in Chapters
            val articles = listOf(
                // Rule 1, Chapter 1
                Article("a1", "c1", "¿Qué es el baloncesto?", "Art. 1", 
                    "El baloncesto es un deporte de equipo en el que dos equipos de cinco jugadores cada uno intentan anotar puntos lanzando el balón a través del aro rival, que está ubicado a 3,05 m del suelo. El equipo que logra más puntos al finalizar el tiempo reglamentario gana el partido.", 
                    "Art. 1 — Definiciones. El baloncesto es jugado por dos (2) equipos de cinco (5) jugadores cada uno. El objetivo de cada equipo es encestar en la cesta del equipo contrario e impedir que el otro equipo tome posesión del balón o enceste.", 
                    listOf("2 equipos de 5 jugadores", "Aro a 3,05 m de altura", "Más puntos = victoria"), false),
                Article("a2", "c1", "El balón en juego", "Art. 1.2", 
                    "Un balón está en juego cuando el árbitro lo pone en circulación válida: durante un salto entre dos, cuando un jugador lanza a canasta, o cuando el árbitro lo entrega para saque o tiro libre. Fuera del juego, el reloj se detiene.", 
                    "Art. 1.2 — El balón está en juego cuando: (a) durante un salto entre dos, el árbitro lanza el balón al aire y es tocado legalmente por un saltador. (b) durante un tiro libre, el árbitro entrega el balón al tirador. (c) durante un saque de banda, el árbitro entrega el balón al jugador que saca o lo pone a su disposición.", 
                    listOf("Salto entre dos", "Entrega para tiro libre", "Saque de banda"), false),
                
                // Rule 1, Chapter 2
                Article("a3", "c2", "Estructura temporal del partido", "Art. 8", 
                    "Un partido oficial FIBA se juega en 4 cuartos de 10 minutos cronometrados cada uno. Entre el primer y segundo cuarto, y entre el tercero y cuarto, hay un descanso de 2 minutos. El medio tiempo dura 15 minutos. Si al final del cuarto período hay empate, se juegan prórrogas de 5 minutos.", 
                    "Art. 8 — Períodos de juego. El partido constará de cuatro (4) períodos de diez (10) minutos. Si el marcador está empatado al final del cuarto período, el partido continuará con un período extra de cinco (5) minutos, o tantos períodos extra de cinco (5) minutos como sea necesario para romper el empate.", 
                    listOf("4 × 10 minutos", "Descanso 2 min entre cuartos", "Medio tiempo 15 min", "Prórroga 5 min"), false),
                Article("a4", "c2", "Tiempo de juego efectivo", "Art. 8.3", 
                    "El reloj se para cada vez que el árbitro pita. Solo cuenta el tiempo en que el balón está vivo. En los últimos 2 minutos de cualquier período, el reloj también se detiene después de cada canasta anotada para conservar el tiempo efectivo de juego.", 
                    "Art. 8.3 — El reloj del partido deberá detenerse cada vez que el silbato del árbitro suene mientras el balón está en juego. El reloj del partido no se pondrá en marcha hasta que el balón sea tocado en el terreno de juego por un jugador que participe en el juego.", 
                    listOf("Reloj para con cada pito", "Últimos 2 min: para tras canasta", "Solo tiempo de balón vivo"), true)
                // Add more articles for other chapters...
            )
            repository.syncArticles(articles)

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

    companion object {
        fun provideFactory(repository: RuleRepository): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DashboardViewModel(repository) as T
            }
        }
    }
}
