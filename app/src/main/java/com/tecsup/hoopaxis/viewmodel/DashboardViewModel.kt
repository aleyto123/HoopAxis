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
    val allArticles: List<Article> = emptyList(),
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
    val articlesByRule: StateFlow<List<Article>> = _selectedRuleId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList())
        else if (id == "all") repository.allArticles
        else repository.getArticlesByRule(id)
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
        repository.allArticles,
        _isLoading
    ) { user, rules, chapters, articles, isLoading ->
        DashboardUiState(
            user = user,
            rules = rules,
            allChapters = chapters,
            allArticles = articles,
            isLoading = isLoading
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState(isLoading = true)
    )

    init {
        refreshData()
    }

    fun refreshData() {
        viewModelScope.launch {
            _isLoading.value = true
            
            val currentArticles = repository.allArticles.first()
            val hasBadArticles = currentArticles.any { it.articleNumber.contains(".") }
            
            if (currentArticles.size != 50 || hasBadArticles) {
                loadInitialData()
            }
            _isLoading.value = false
        }
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
            
            repository.clearData()

            val rules = listOf(
                Rule("r1", 1, "El Juego", "Definición y objeto", "🏀", "#C96BFF", "#C96BFF44", 1, 0.0f),
                Rule("r2", 2, "Terreno de juego y equipamiento", "Dimensiones y material", "📏", "#5BC8FF", "#5BC8FF44", 2, 0.0f),
                Rule("r3", 3, "Los equipos", "Composición y roles", "👥", "#FF6B9D", "#FF6B9D44", 4, 0.0f),
                Rule("r4", 4, "Reglamentación del juego", "Procedimientos y tiempos", "⚡", "#FFD166", "#FFD16644", 14, 0.0f),
                Rule("r5", 5, "Violaciones", "Infracciones de juego", "🚫", "#50DC78", "#50DC7844", 10, 0.0f),
                Rule("r6", 6, "Faltas", "Contactos y conducta", "🚨", "#FF9F43", "#FF9F4344", 8, 0.0f),
                Rule("r7", 7, "Disposiciones generales", "Situaciones especiales", "📋", "#FF6B6B", "#FF6B6B44", 5, 0.0f),
                Rule("r8", 8, "Árbitros, auxiliares de mesa, comisionado", "Deberes del personal", "👨‍⚖️", "#A29BFE", "#A29BFE44", 6, 0.0f)
            )
            repository.syncRules(rules)

            val articles = mutableListOf<Article>()
            
            val data = listOf(
                Triple("r1", "Definiciones", "#C96BFF"),
                Triple("r2", "Terreno de juego", "#5BC8FF"),
                Triple("r2", "Equipamiento", "#5BC8FF"),
                Triple("r3", "Equipos", "#FF6B9D"),
                Triple("r3", "Jugadores: Lesiones y asistencia", "#FF6B9D"),
                Triple("r3", "Capitán: Obligaciones y facultades", "#FF6B9D"),
                Triple("r3", "Entrenador principal y primer entrenador ayudante: Obligaciones y facultades", "#FF6B9D"),
                Triple("r4", "Tiempo de juego, tanteo empatado y prórrogas", "#FFD166"),
                Triple("r4", "Comienzo y final de un cuarto, prórroga o del partido", "#FFD166"),
                Triple("r4", "Estado del balón", "#FFD166"),
                Triple("r4", "Posición de un jugador y de un árbitro", "#FFD166"),
                Triple("r4", "Salto entre dos y posesión alterna", "#FFD166"),
                Triple("r4", "Cómo se juega el balón", "#FFD166"),
                Triple("r4", "Control del balón", "#FFD166"),
                Triple("r4", "Jugador en acción de tiro", "#FFD166"),
                Triple("r4", "Canasta: Cuándo se marca y su valor", "#FFD166"),
                Triple("r4", "Saque de banda / fondo", "#FFD166"),
                Triple("r4", "Tiempo muerto", "#FFD166"),
                Triple("r4", "Sustitución", "#FFD166"),
                Triple("r4", "Partido perdido por incomparecencia", "#FFD166"),
                Triple("r4", "Partido perdido por inferioridad", "#FFD166"),
                Triple("r5", "Violaciones", "#50DC78"),
                Triple("r5", "Jugador fuera del terreno de juego y balón fuera del terreno de juego", "#50DC78"),
                Triple("r5", "Regate", "#50DC78"),
                Triple("r5", "Avance ilegal (Pasos)", "#50DC78"),
                Triple("r5", "3 segundos", "#50DC78"),
                Triple("r5", "Jugador estrechamente marcado", "#50DC78"),
                Triple("r5", "8 segundos", "#50DC78"),
                Triple("r5", "24 segundos / Reloj de tiro", "#50DC78"),
                Triple("r5", "Balón devuelto a la pista trasera (Campo atrás)", "#50DC78"),
                Triple("r5", "Interferencia e interposición", "#50DC78"),
                Triple("r6", "Faltas", "#FF9F43"),
                Triple("r6", "Contacto: Principios generales", "#FF9F43"),
                Triple("r6", "Falta personal", "#FF9F43"),
                Triple("r6", "Falta doble", "#FF9F43"),
                Triple("r6", "Falta técnica", "#FF9F43"),
                Triple("r6", "Falta antideportiva", "#FF9F43"),
                Triple("r6", "Falta descalificante", "#FF9F43"),
                Triple("r6", "Enfrentamientos", "#FF9F43"),
                Triple("r7", "5 faltas por jugador", "#FF6B6B"),
                Triple("r7", "Faltas de equipo: Penalización", "#FF6B6B"),
                Triple("r7", "Situaciones especiales", "#FF6B6B"),
                Triple("r7", "Tiros libres", "#FF6B6B"),
                Triple("r7", "Errores rectificables", "#FF6B6B"),
                Triple("r8", "Árbitros, auxiliares de mesa y comisionado", "#A29BFE"),
                Triple("r8", "Árbitro principal: Obligaciones y facultades", "#A29BFE"),
                Triple("r8", "Árbitros: Obligaciones y facultades", "#A29BFE"),
                Triple("r8", "Anotador y ayudante de anotador: Obligaciones", "#A29BFE"),
                Triple("r8", "Operador del reloj de tiro: Obligaciones", "#A29BFE"),
                Triple("r8", "Cronometrador: Obligaciones", "#A29BFE")
            )

            data.forEachIndexed { index, triple ->
                val num = index + 1
                val emoji = when (num) {
                    1 -> "🏀"
                    2 -> "📏"
                    3 -> "⚙️"
                    4 -> "👥"
                    5 -> "🩹"
                    6 -> "🎖️"
                    7 -> "📋"
                    8 -> "⏱️"
                    9 -> "🏁"
                    10 -> "🏀"
                    11 -> "🏃"
                    12 -> "🤾"
                    13 -> "👐"
                    14 -> "✋"
                    15 -> "🎯"
                    16 -> "🧺"
                    17 -> "📤"
                    18 -> "🛑"
                    19 -> "🔄"
                    20 -> "❌"
                    21 -> "📉"
                    22 -> "🚫"
                    23 -> "🔲"
                    24 -> "⛹️"
                    25 -> "🦶"
                    26 -> "⏳"
                    27 -> "🛡️"
                    28 -> "⌛"
                    29 -> "⏲️"
                    30 -> "🔙"
                    31 -> "🧤"
                    32 -> "🚨"
                    33 -> "👤"
                    34 -> "🖐️"
                    35 -> "👥"
                    36 -> "⚠️"
                    37 -> "‼️"
                    38 -> "🟥"
                    39 -> "👊"
                    40 -> "🖐️"
                    41 -> "🔢"
                    42 -> "🌟"
                    43 -> "🎯"
                    44 -> "✍️"
                    45 -> "👨‍⚖️"
                    46 -> "⚖️"
                    47 -> "🏁"
                    48 -> "📝"
                    49 -> "⏱️"
                    50 -> "⌚"
                    else -> "📄"
                }
                
                val paraphrase = when (num) {
                    1 -> "• Enfrentamiento 5 vs 5 en pista.\n• Objetivo: Encestar e impedir anotaciones.\n• Cumplir normas y conducta deportiva.\n• Gana el equipo con más puntos."
                    2 -> "• Superficie lisa y dura de 28x15 metros.\n• División en pista trasera y delantera.\n• Líneas de 5 cm de ancho.\n• Áreas técnicas fuera del terreno."
                    3 -> "• Canastas y balones homologados.\n• Relojes de juego y de tiro (24/14s).\n• Señales acústicas y acta del partido.\n• Iluminación uniforme sin deslumbramiento."
                    else -> "Información no disponible"
                }

                val keyPoints = when (num) {
                    1 -> listOf(
                        "1.1 Principio del partido: Es un deporte que enfrenta a dos equipos compuestos por cinco jugadores en pista. El objetivo central es encestar el balón en el aro del equipo contrario e impedir que este anote en el propio. El control del encuentro está a cargo de los árbitros, los auxiliares de mesa y, si lo hubiera, un comisionado.",
                        "1.2 Deberes de los participantes: Los miembros de las delegaciones (jugadores, entrenadores y asistentes) deben acatar las normas y guardar conducta deportiva. Tienen la obligación de notificar de inmediato a los árbitros cualquier falla o descuadre que detecten en el marcador, los relojes o el registro de faltas.",
                        "1.3 Pistas y canastas: La canasta que un equipo ataca es la del oponente; la canasta que defiende es la suya propia.",
                        "1.4 Determinación del ganador: Obtiene la victoria el conjunto que acumule la mayor cantidad de puntos una vez finalizado el tiempo reglamentario de juego."
                    )
                    2 -> listOf(
                        "2.1 Dimensiones principales: El campo debe ser una superficie lisa, dura y libre de obstáculos. Sus medidas deben ser de 28 metros de longitud por 15 metros de ancho, calculadas desde el borde interior de las líneas de límite. Al sumar la zona de seguridad exterior (mínimo 2 metros alrededor), el espacio total de suelo requerido es de al menos 32 metros por 19 metros.",
                        "2.2 División de la cancha: Pista trasera (propia) y Pista delantera (de ataque) delimitadas por la línea central.",
                        "2.3 Demarcaciones y líneas: Todas las líneas miden 5 cm de ancho. Incluye el arco de 3 puntos a 6,75 metros y áreas de banquillo.",
                        "2.4 Áreas técnicas de control: La mesa de los anotadores y las sillas destinadas a los cambios de jugadores deben ubicarse fuera del terreno de juego sobre una zona elevada o claramente delimitada."
                    )
                    3 -> listOf(
                        "Unidades de canasta completas (tableros, aros con sistema abatible o rígido, redes y sus respectivas protecciones de acolchado).",
                        "Balones reglamentarios homologados por la FIBA.",
                        "Reloj principal de juego y tablero electrónico indicador del marcador y faltas.",
                        "Dispositivo del reloj de tiro (pantallas de 24/14 segundos).",
                        "Cronómetro independiente para el control de los tiempos muertos.",
                        "Dos señales acústicas potentes e independientes (una para el reloj de juego y otra para el reloj de tiro).",
                        "Acta del partido (planilla impreso o sistema digital).",
                        "Marcadores de faltas individuales (números del 1 al 5) e indicadores visuales de faltas colectivas por equipo.",
                        "Flecha de posesión alterna para resolver situaciones de balón retenido.",
                        "Sistema de iluminación uniforme que cubra la totalidad del terreno de juego sin generar sombras ni deslumbramiento."
                    )
                    else -> listOf("Información no disponible")
                }

                articles.add(Article(
                    id = "a$num",
                    ruleId = triple.first,
                    chapterId = "c$num",
                    title = triple.second,
                    emoji = emoji,
                    articleNumber = "Art. $num",
                    color = triple.third,
                    sortOrder = num,
                    paraphrase = paraphrase,
                    sourceText = "Información no disponible",
                    keyPoints = keyPoints,
                    progress = 0f,
                    isCompleted = false
                ))
            }

            repository.syncArticles(articles)
            
            // AGREGAMOS PREGUNTAS DEL QUIZ PARA EL ARTÍCULO 1
            val quizQuestions = listOf(
                com.tecsup.hoopaxis.data.model.QuizQuestion(
                    id = "q1",
                    question = "¿Cuál es el objetivo central del basketball según el reglamento?",
                    options = listOf(
                        "Mantener la posesión del balón el mayor tiempo posible sin lanzar.",
                        "Encestar el balón en el aro del equipo contrario e impedir que este anote en el propio.",
                        "Realizar la mayor cantidad de faltas tácticas para frenar al rival.",
                        "Jugar únicamente en la mitad de la pista asignada."
                    ),
                    correctIndex = 1,
                    explanation = "El reglamento establece que el objetivo es encestar en el aro contrario y defender el propio.",
                    category = "a1" // Usamos el ID del artículo como categoría para filtrar
                ),
                com.tecsup.hoopaxis.data.model.QuizQuestion(
                    id = "q2",
                    question = "¿Cuántos jugadores por equipo se encuentran en la pista durante el partido?",
                    options = listOf("Tres jugadores.", "Cuatro jugadores.", "Cinco jugadores.", "Seis jugadores."),
                    correctIndex = 2,
                    explanation = "El deporte enfrenta a dos equipos compuestos por cinco jugadores en pista.",
                    category = "a1"
                ),
                com.tecsup.hoopaxis.data.model.QuizQuestion(
                    id = "q3",
                    question = "¿A quiénes tienen la obligación de notificar los participantes si detectan un descuadre en el marcador o los relojes?",
                    options = listOf("Al capitán del equipo rival.", "A los espectadores en las gradas.", "De inmediato a los árbitros.", "Exclusivamente al comisionado al final del juego."),
                    correctIndex = 2,
                    explanation = "Los miembros de las delegaciones tienen la obligación de notificar de inmediato a los árbitros cualquier falla o descuadre.",
                    category = "a1"
                ),
                com.tecsup.hoopaxis.data.model.QuizQuestion(
                    id = "q4",
                    question = "¿Cómo se determina al equipo ganador de un encuentro de basketball?",
                    options = listOf(
                        "El conjunto que anote primero en la prórroga.",
                        "El equipo que cometa menos faltas personales.",
                        "El conjunto que acumule la mayor cantidad de puntos una vez finalizado el tiempo reglamentario de juego.",
                        "Por decisión de los oficiales de mesa."
                    ),
                    correctIndex = 2,
                    explanation = "Obtiene la victoria el conjunto que acumule la mayor cantidad de puntos al finalizar el tiempo reglamentario.",
                    category = "a1"
                )
            )

            quizQuestions.forEach { repository.addQuizQuestion(it) }

            // Sincronizamos específicamente los Artículos 1, 2 y 3 a Firebase
            articles.filter { it.id in listOf("a1", "a2", "a3") }.forEach { art ->
                repository.addArticle(art)
            }

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
