package com.tecsup.hoopaxis.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tecsup.hoopaxis.data.model.*
import com.tecsup.hoopaxis.data.repository.RuleRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class DashboardUiState(
    val user: User? = null,
    val rules: List<Rule> = emptyList(),
    val allArticles: List<Article> = emptyList(),
    val filteredArticles: List<Article> = emptyList(),
    val chapterFilteredArticles: List<Article> = emptyList(),
    val isLoading: Boolean = true
)

class DashboardViewModel(private val repository: RuleRepository) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    private val _selectedRuleId = MutableStateFlow<String?>(null)
    private val _selectedChapterId = MutableStateFlow<String?>(null)

    val uiState: StateFlow<DashboardUiState> = combine(
        combine(repository.currentUser, repository.allRules, repository.allArticles) { user, rules, articles ->
            Triple(user, rules, articles)
        },
        combine(_selectedRuleId, _selectedChapterId, _isLoading) { selRuleId, selChapterId, loading ->
            Triple(selRuleId, selChapterId, loading)
        }
    ) { part1, part2 ->
        val (user, rules, articles) = part1
        val (selRuleId, selChapterId, loading) = part2
        
        val filtered = when {
            selRuleId == null -> emptyList()
            selRuleId == "all" -> articles
            else -> articles.filter { it.ruleId == selRuleId }
        }

        val chapterFiltered = if (selChapterId == null) emptyList()
        else articles.filter { it.chapterId == selChapterId }

        DashboardUiState(
            user = user,
            rules = rules,
            allArticles = articles,
            filteredArticles = filtered,
            chapterFilteredArticles = chapterFiltered,
            // NAVEGACIÓN INSTANTÁNEA: Solo bloqueamos si no hay artículos en la base de datos
            isLoading = loading && articles.isEmpty()
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
            loadInitialData()
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
        val article = repository.getArticleById(articleId)
        if (article != null && article.progress == 0f) {
            viewModelScope.launch {
                repository.updateArticle(article.copy(progress = 0.1f))
            }
        }
        return article
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val currentArticles = repository.allArticles.first()
            val articlesMap = currentArticles.associateBy { it.id }

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

            val articlesList = mutableListOf<Article>()
            for (i in 1..50) {
                val id = "a$i"
                val existing = articlesMap[id]
                
                val ruleId = when {
                    i <= 1 -> "r1"
                    i <= 3 -> "r2"
                    i <= 7 -> "r3"
                    i <= 21 -> "r4"
                    i <= 31 -> "r5"
                    i <= 39 -> "r6"
                    i <= 44 -> "r7"
                    else -> "r8"
                }

                articlesList.add(Article(
                    id = id,
                    ruleId = ruleId,
                    chapterId = "c$i",
                    title = when(i) {
                        1 -> "Definiciones"
                        2 -> "Terreno de Juego"
                        3 -> "Equipamiento"
                        4 -> "Equipos"
                        5 -> "Jugadores: Lesiones y asistencia"
                        6 -> "Capitán: Obligaciones y facultades"
                        7 -> "Entrenador principal y ayudante"
                        8 -> "Tiempo de juego, tanteo empatado y prórrogas"
                        9 -> "Comienzo y final de un cuarto"
                        10 -> "Estado del balón"
                        11 -> "Posición de un jugador y de un árbitro"
                        12 -> "Salto entre dos y posesión alterna"
                        13 -> "Cómo se juega el balón"
                        14 -> "Control del balón"
                        15 -> "Jugador en acción de tiro"
                        16 -> "Canasta: Cuándo se marca y su valor"
                        17 -> "Saque de banda / fondo"
                        18 -> "Tiempo muerto"
                        19 -> "Sustitución"
                        20 -> "Partido perdido por incomparecencia"
                        21 -> "Partido perdido por inferioridad"
                        32 -> "Faltas"
                        33 -> "Contacto: Principios generales"
                        34 -> "Falta personal"
                        35 -> "Falta doble"
                        36 -> "Falta técnica"
                        37 -> "Falta antideportiva"
                        38 -> "Falta descalificante"
                        39 -> "Enfrentamientos"
                        40 -> "5 faltas por jugador"
                        41 -> "Faltas de equipo: Penalización"
                        42 -> "Situaciones especiales"
                        43 -> "Tiros libres"
                        44 -> "Errores rectificables"
                        45 -> "Árbitros, oficiales de mesa y comisionado"
                        46 -> "Árbitro principal: Obligaciones y facultades"
                        47 -> "Árbitros: Obligaciones y facultades"
                        48 -> "Anotador y ayudante de anotador: Obligaciones"
                        49 -> "Operador del reloj de tiro: Obligaciones"
                        50 -> "Cronometrador: Obligaciones"
                        else -> "Artículo $i del Reglamento"
                    },
                    articleNumber = "Art. $i",
                    sortOrder = i,
                    emoji = when(i) {
                        1 -> "🏀"
                        2 -> "📏"
                        3 -> "🛡️"
                        4 -> "👥"
                        5 -> "🩹"
                        6 -> "🎖️"
                        7 -> "📋"
                        else -> "📄"
                    },
                    color = rules.find { it.id == ruleId }?.color ?: "#C96BFF",
                    keyPoints = when (i) {
                        1 -> "• Partido entre dos equipos de 5 jugadores.\n• Objetivo: encestar y defender el aro.\n• Control a cargo de árbitros y oficiales.\n• Gana el equipo con más puntos al final."
                        2 -> "• Superficie lisa y dura de 28x15 metros.\n• División en pista trasera y delantera.\n• Líneas perimetrales y de marcación de 5 cm.\n• Ubicación de la mesa de control fuera de banda."
                        3 -> "• Unidades de canasta completas y tableros.\n• Balones homologados y relojes principales.\n• Actas oficiales y marcadores de faltas.\n• Flecha de posesión y sistemas de iluminación."
                        4 -> "• Registro de 12 jugadores y hasta 8 acompañantes.\n• Indumentaria uniforme con números visibles.\n• Prohibición de joyas u objetos cortantes.\n• Accesorios de compresión con tono uniforme."
                        5 -> "• Interrupción por lesión en balón muerto.\n• Sustitución obligatoria si hay atención médica.\n• Regreso condicionado a heridas cubiertas.\n• Excepción mediante solicitud de tiempo muerto."
                        6 -> "• Capitán como único interlocutor respetuoso.\n• Derecho a aclaraciones en balón muerto.\n• Obligación de firmar el acta en caso de protesta.\n• Representación formal del equipo en la pista."
                        7 -> "• Trámites previos de listas y quinteto inicial.\n• Entrenador principal autorizado a estar de pie.\n• Gestión de tiempos muertos y sustituciones.\n• Responsabilidad sobre el desafío de entrenador."
                        8 -> "• Encuentro dividido en 4 periodos de 10 minutos.\n• Pausas de 2 minutos y descanso de 15 minutos.\n• Prórrogas de 5 minutos en caso de empate.\n• Tiempos reglamentarios de tiempo real de juego."
                        9 -> "• Inicio con salto entre dos en el 1er cuarto.\n• Saques de banda para iniciar otros periodos.\n• Obligación de contar con 5 jugadores listos.\n• Finalización dictada por la alarma acústica."
                        10 -> "• Balón vivo en salto, tiros libres o saques.\n• Balón muerto por canasta, silbato o alarma.\n• Delimitación exacta del estado del balón.\n• Control regulado por los oficiales de mesa."
                        11 -> "• Posición espacial fijada por el contacto de los pies.\n• Mantenimiento de la condición estando en el aire.\n• Árbitros integrados como parte de la superficie.\n• Impacto en colegiado asimilado al suelo."
                        12 -> "• Salto entre dos exclusivo del primer cuarto.\n• Flecha de posesión alterna para situaciones de lucha.\n• Rotación de la dirección tras cada utilización.\n• Resolución dinámica de dudas de posesión."
                        13 -> "• Manejo del balón exclusivamente con las manos.\n• Violación por golpe intencionado con el pie.\n• Continuidad del juego ante rebotes involuntarios.\n• Prohibición general de golpear con el puño."
                        14 -> "• Control individual mediante posesión o bote.\n• Control de equipo durante pases internos.\n• Finalización del control por robo o tiro.\n• Transición del balón fuera de las manos."
                        15 -> "• Inicio formal en el movimiento continuo de tiro.\n• Implica elevación de brazos e impulso corporal.\n• Finalización al soltar el balón y aterrizar.\n• Delimitación temporal de la acción técnica."
                        16 -> "• Canasta válida al atravesar el aro superior.\n• Puntuación de 1, 2 o 3 puntos según la zona.\n• Asignación de canasta propia al capitán rival.\n• Sanción de violación por encestar a propósito."
                        17 -> "• Ejecución del saque desde fuera de los límites.\n• Plazo máximo de 5 segundos para soltar el balón.\n• Prohibición de pisar o botar fuera del campo.\n• Restricción defensiva de cruzar la línea."
                        18 -> "• Pausas tácticas de 1 minuto para el entrenador.\n• Distribución de 2 en primera y 3 en segunda mitad.\n• Concesión de 1 tiempo muerto por prórroga.\n• Control estricto en los últimos dos minutos."
                        19 -> "• Solicitud de cambios presencial ante la mesa.\n• Oportunidad exclusiva en pausas reglamentarias.\n• Ingreso obligatorio por la zona de sustitución.\n• Salida previa del jugador relevado."
                        20 -> "• Incomparecencia por negativa o retraso de 15 min.\n• Pérdida del encuentro con marcador de 20 a 0.\n• Cero puntos en la tabla para el infractor.\n• Aplicación directa de la norma de ausencia."
                        21 -> "• Reducción de jugadores activos a menos de 2.\n• Modificación del marcador a 2 a 0 si iba ganando.\n• Mantenimiento del resultado si iba perdiendo.\n• Asignación de 1 punto en la tabla al derrotado."
                        22 -> "• Infracción menor sin llegar a falta personal.\n• Sanción mediante saque de banda o fondo.\n• Ejecución desde el punto más cercano.\n• Exclusiones normativas específicas."
                        23 -> "• Jugador fuera por tocar elementos externos.\n• Balón fuera al impactar soportes o tableros.\n• Delimitación espacial basada en las líneas.\n• Pérdida de posesión inmediata."
                        24 -> "• Inicio del regate al botar o rodar el balón.\n• Infracción por dobles tras concluir el primero.\n• Excepciones por desvíos o tiros previos.\n• Control continuo del bote en pista."
                        25 -> "• Desplazamiento ilegal manteniendo el balón.\n• Elección reglamentaria del pie de pivote.\n• Obligación de soltar el balón antes de levantar el pie.\n• Restricciones estrictas en el apoyo."
                        26 -> "• Restricción de 3 segundos en la zona pintada.\n• Aplicación en pista delantera con balón vivo.\n• Excepciones por salida activa o acción de tiro.\n• Control del cronómetro de la llave."
                        27 -> "• Marcaje estrecho a menos de 1 metro.\n• Límite de 5 segundos para pasar o botar.\n• Exigencia de posición legal defensiva.\n• Dinámica de presión sobre el poseedor."
                        28 -> "• Obligación de pasar a pista delantera en 8 segundos.\n• Control de la posesión desde la pista trasera.\n• Criterio físico de llegada del balón.\n• Conteo continuo por parte de los oficiales."
                        29 -> "• Límite de 24 segundos de posesión por equipo.\n• Obligación de golpear el aro con el lanzamiento.\n• Restablecimiento a 14 segundos en supuestos clave.\n• Alarma acústica por agotamiento de tiempo."
                        30 -> "• Retorno ilegal del balón a la pista trasera.\n• Exigencia de control previo en pista delantera.\n• Toque inicial prohibido en la zona posterior.\n• Sanción de pérdida de posesión."
                        31 -> "• Prohibición de interferir el balón en el aro.\n• Sanción de goaltending en trayectoria descendente.\n• Validación automática de puntos para la defensa.\n• Anulación de canasta en infracción de ataque."
                        32 -> "• Infracción por contacto ilegal o conducta.\n• Registro obligatorio en el acta oficial.\n• Base para la acumulación de penalizaciones.\n• Sanción conforme a la gravedad."
                        33 -> "• Principio de cilindro y verticalidad defensiva.\n• Respeto a tiempos y distancias sin balón.\n• Reglas específicas para pantallas y bloqueos.\n• Penalización de la simulación de faltas."
                        34 -> "• Contacto ilegal sobre un oponente en juego.\n• Sanción con saque o tiros libres acumulados.\n• Valoración según la situación de tiro.\n• Registro individual del infractor."
                        35 -> "• Faltas mutuas y simultáneas entre rivales.\n• Anulación recíproca de sanciones equivalentes.\n• Reanudación basada en el control previo.\n• Gestión cronológica de incidencias."
                        36 -> "• Infracción de conducta sin contacto físico.\n• Sanción con un tiro libre para el rival.\n• Reanudación en el punto de interrupción.\n• Control de actitudes antideportivas."
                        37 -> "• Contacto excesivo o falta en campo abierto.\n• Sanción con tiros libres y posesión posterior.\n• Protección en transiciones claras de ataque.\n• Restricción en los últimos dos minutos."
                        38 -> "• Falta descalificante por conducta grave.\n• Expulsión inmediata al vestuario o pabellón.\n• Penalización con tiros libres y posesión.\n• Sanción disciplinaria severa."
                        39 -> "• Sanción por altercado o violencia física.\n• Descalificación por abandonar el banquillo.\n• Excepción operativa del primer entrenador.\n• Control estricto de trifulcas en pista."
                        40 -> "• Expulsión obligatoria al acumular 5 faltas.\n• Plazo de 30 segundos para efectuar el cambio.\n• Sanción técnica al banquillo por reingreso.\n• Control riguroso del límite individual."
                        41 -> "• Penalización colectiva a partir de la 4ª falta.\n• Acumulación en descansos y prórrogas.\n• Concesión automática de 2 tiros libres.\n• Supresión del saque por faltas sin tiro."
                        42 -> "• Gestión de múltiples faltas simultáneas.\n• Cancelación mutua de sanciones idénticas.\n• Orden cronológico estricto de aplicación.\n• Imposibilidad de compensación posterior."
                        43 -> "• Lanzamiento libre sin oposición desde la línea.\n• Plazo máximo de 5 segundos para el tirador.\n• Ocupación alternada de los pasillos de rebote.\n• Sanciones específicas por violación de norma."
                        44 -> "• Corrección exclusiva de errores reglamentarios.\n• Límite temporal antes del siguiente balón vivo.\n• Consolidación definitiva tras la expiración.\n• Supuestos tasados de revisión arbitral."
                        45 -> "• Composición del cuerpo arbitral y mesa.\n• Principio de absoluta neutralidad e imparcialidad.\n• Uso de indumentaria oficial homologada.\n• Soporte operativo para el desarrollo del juego."
                        46 -> "• Inspección previa de instalaciones y material.\n• Autoridad para suspender el encuentro.\n• Uso exclusivo del sistema de repetición en vídeo.\n• Cierre y firma definitiva del acta."
                        47 -> "• Potestad plena sobre faltas y violaciones.\n• Detención del juego mediante el uso del silbato.\n• Aplicación estricta del criterio de ventaja.\n• Coherencia arbitral durante el tiempo oficial."
                        48 -> "• Registro cronológico de puntos y faltas.\n• Control de sustituciones y tiempos muertos.\n• Notificación inmediata de la quinta falta.\n• Operación del marcador electrónico público."
                        49 -> "• Cronometraje preciso del tiempo de juego.\n• Medición exacta de pausas y descansos.\n• Activación en saltos, saques y rebotes.\n• Emisión de señales acústicas automáticas."
                        50 -> "• Control riguroso del reloj de 24 y 14 segundos.\n• Restablecimiento normativo según posesión.\n• Detención ante toques defensivos sin cambio.\n• Emisión de alarma de posesión agotada."
                        else -> "Contenido pedagógico del artículo $i en desarrollo..."
                    },
                    paraphrase = when (i) {
                        1 -> listOf(
                            "1.1 Principio del partido: Es un deporte que enfrenta a dos equipos compuestos por cinco jugadores en pista. El objetivo central es encestar el balón en el aro del equipo contrario e impedir que este anote en el propio. El control del encuentro está a cargo de los árbitros, los oficiales de mesa y, si lo hubiera, un comisionado.",
                            "1.2 Deberes de los participantes: Los miembros de las delegaciones (jugadores, entrenadores y asistentes) deben acatar las normas y guardar conducta deportiva. Tienen la obligación de notificar de inmediato a los árbitros cualquier falla o descuadre que detecten en el marcador, los relojes o el registro de faltas.",
                            "1.3 Pistas y canastas: La canasta que un equipo ataca es la del oponente; la canasta que defiende es la suya propia.",
                            "1.4 Determinación del ganador: Obtiene la victoria el conjunto que acumule la mayor cantidad de puntos una vez finalizado el tiempo reglamentario de juego."
                        )
                        2 -> listOf(
                            "2.1 Dimensiones principales: El campo debe ser una superficie lisa, dura y libre de obstáculos. Sus medidas deben ser de 28 metros de longitud por 15 metros de ancho, calculadas desde el borde interior de las líneas de límite. Al sumar la zona de seguridad exterior (mínimo 2 metros alrededor), el espacio total de suelo requerido es de al menos 32 metros por 19 metros.",
                            "2.2 División de la cancha: Pista trasera (propia): Comprende el aro propio, la cara interna del tablero y la sección de cancha delimitada por la línea de fondo posterior, las líneas laterales y el borde de la línea central más cercano a la propia canasta. Pista delantera (de ataque): Comprende el aro del oponente, la cara interna de su tablero y la sección de cancha delimitada por la línea de fondo rival, las líneas laterales y el borde de la línea central más cercano al aro rival.",
                            "2.3 Demarcaciones y líneas: Todas las líneas miden 5 cm de ancho y deben ser de un color uniforme que contraste con el suelo. Las líneas perimetrales (bandas y fondos) determinan el límite y no son parte de la zona de juego válida. Se deben marcar la línea central, el círculo central, las zonas restringidas (botella), las líneas de tiro libre y el arco de 3 puntos (6,75m). Incluye los semicírculos de no-carga y las líneas de saque. Fuera del perímetro se delimitan las zonas de banquillo con 16 asientos.",
                            "2.4 Áreas técnicas de control: La mesa de los anotadores y las sillas destinadas a los cambios de jugadores deben ubicarse fuera del terreno de juego sobre una zona elevada o claramente delimitada."
                        )
                        3 -> listOf(
                            "Unidades de canasta completas (tableros, aros con sistema abatible o rígido, redes y sus respectivas protecciones de acolchado).",
                            "Balones reglamentarios homologados por la FIBA.",
                            "Reloj principal de juego y tablero electrónico indicador del marcador y faltas.",
                            "Dispositivo del reloj de tiro (pantallas de 24/14 segundos).",
                            "Cronómetro independiente para el control de los tiempos muertos.",
                            "Dos señales acústicas potentes e independientes (una para el reloj de juego y otra para el reloj de tiro).",
                            "Acta oficial del partido (planilla impreso o sistema digital).",
                            "Marcadores de faltas individuales (números del 1 al 5) e indicadores visuales de faltas colectivas por equipo.",
                            "Flecha de posesión alterna para resolver situaciones de balón retenido.",
                            "Sistema de iluminación uniforme que cubra la totalidad del terreno de juego sin generar sombras ni deslumbramiento."
                        )
                        4 -> listOf(
                            "4.1 Integración y miembros elegibles: Cada equipo puede registrar hasta 12 jugadores con derecho a jugar (incluido un capitán). La delegación se complementa con 1 primer entrenador, un máximo de 2 entrenadores ayudantes y hasta 5 miembros adicionales del cuerpo técnico o médico (máximo 8 acompañantes en total). Todos deben figurar en el acta oficial antes del inicio del partido. Durante el juego, solo las personas registradas en el acta pueden sentarse en el banquillo.",
                            "4.2 Indumentaria y uniformes: Camisetas: Todos los jugadores de un equipo deben vestir camisetas del mismo tono dominante en la parte delantera y trasera. Debe ir por dentro del pantalón. Pantalones: Mismo color dominante para todos los integrantes. No es obligatorio que coincida con el color de la camiseta, pero sí debe ser idéntico entre compañeros. Calcetines: Todos los jugadores deben llevar calcetines del mismo color dominante visible. Numeración: La camiseta debe llevar números legibles y contrastantes en el pecho (mínimo 10 cm de alto) y la espalda (mínimo 20 cm de alto). Se permiten los números 0, 00 y del 1 al 99. No dos jugadores del mismo equipo pueden llevar el mismo número.",
                            "4.3 Equipamiento accesorio y protecciones: Las prendas de compresión (mangas de brazos, perneras, camisetas interiores) y accesorios (cintas de cabeza, muñequeras, protecciones) deben ser del mismo tono uniforme para todos los jugadores del equipo que los utilicen (o cumplir con las especificaciones neutras de FIBA como negro, blanco o transparente). No se permite ningún objeto que pueda cortar o causar lesiones (joyas, anillos, uñas excesivamente largas o accesorios metálicos sin acolchar)."
                        )
                        5 -> listOf(
                            "5.1 Interrupción por lesión: Si un jugador se lesiona, los árbitros pueden detener el partido. Si el balón está vivo en el momento de la lesión, el juego solo se interrumpirá cuando el equipo con el balón tire a canasta, pierda la posesión o el balón quede muerto, salvo que sea imprescindible actuar de inmediato por la seguridad del jugador.",
                            "5.2 Sustitución obligatoria por atención médica: Si el jugador lesionado no puede recuperarse de inmediato (aproximadamente 15 segundos) o recibe atención de los médicos de su equipo, debe ser sustituido. Excepción: El equipo puede mantener al jugador en cancha si solicita un tiempo muerto dentro de esa pausa y el jugador se recupera por completo antes de que finalice el tiempo muerto.",
                            "5.3 Sangrado y heridas abiertas: Cualquier jugador que presente sangrado o una herida abierta debe abandonar el terreno de juego para ser atendido. Solo podrá regresar una vez que el sangrado se haya detenido y la herida o zona afectada quede totalmente cubierta."
                        )
                        6 -> listOf(
                            "6.1 Representación en cancha: Es el único jugador autorizado para dirigirse a los árbitros durante las pausas del partido para solicitar aclaraciones de manera respetuosa y solo cuando el balón esté muerto.",
                            "6.2 Firma de protesta: Si el equipo decide protestar formalmente el resultado del partido por una irregularidad, el capitán debe firmar la casilla de \"Firma del capitán en caso de protesta\" en el acta oficial al finalizar el encuentro, inmediatamente después de concluido el juego."
                        )
                        7 -> listOf(
                            "7.1 Trámites antes del partido: Al menos 40 minutos antes de la hora programada, el primer entrenador debe confirmar la lista de nombres y números de los jugadores elegibles. Al menos 10 minutos antes del partido, debe firmar el acta ratificando el quinteto inicial y a los miembros del banquillo.",
                            "7.2 Permanencia en pista durante el juego: Únicamente el primer entrenador está autorizado a permanecer de pie durante el partido dentro de la zona de banquillo para dar instrucciones a su equipo. Si el primer entrenador debe abandonar el campo por expulsión, descalificación o enfermedad, el primer entrenador ayudante asumirá todas sus funciones y responsabilidades.",
                            "7.3 Solicitudes técnicas: Corresponde al primer entrenador solicitar los tiempos muertos, realizar los pedidos de sustitución a la mesa de control y pedir el desafío de entrenador (Head Coach Challenge - HCC) cuando esté permitido por el reglamento."
                        )
                        8 -> listOf(
                            "8.1 Duración del encuentro: El partido se divide en 4 periodos (cuartos) de 10 minutos de tiempo real de juego.",
                            "8.2 Pausas de descanso: Se concede un intervalo de 2 minutos entre el primer y segundo cuarto, entre el tercer y cuarto cuarto, y previo a cada tiempo suplementario. El descanso de mitad de partido (entre el segundo y tercer cuarto) es de 15 minutos.",
                            "8.3 Desempate (Prórrogas): Si el marcador está igualado al terminar el cuarto periodo, el juego se extenderá mediante períodos adicionales de 5 minutos cada uno, repitiéndose hasta que un equipo obtenga la victoria al concluir un periodo suplementario."
                        )
                        9 -> listOf(
                            "9.1 Inicio del juego: El primer cuarto arranca formalmente cuando el balón es palmeado por uno de los saltadores en el salto entre dos del círculo central. Los demás periodos y tiempos suplementarios inician cuando el balón queda a disposición del jugador encargado de realizar el saque de banda.",
                            "9.2 Condición de inicio: Ningún partido puede dar comienzo si alguno de los dos equipos no cuenta con 5 jugadores en pista debidamente equipados.",
                            "9.3 Conclusión: Un cuarto, tiempo extra o el partido finaliza en el instante en que suena la alarma acústica del reloj principal de juego."
                        )
                        10 -> listOf(
                            "10.1 Balón vivo: Ocurre cuando sale de la mano del árbitro durante un salto entre dos, cuando el colegiado lo pone a disposición de un jugador para ejecutar un tiro libre o un saque de banda/fondo.",
                            "10.2 Balón muerto: Se presenta cuando: Se encesta un tiro de campo o tiro libre válido. Un árbitro hace sonar su silbato con el balón en juego. Suena la señal acústica de finalización de tiempo del reloj de juego o del reloj de tiro de 24/14 segundos."
                        )
                        11 -> listOf(
                            "11.1 Ubicación en cancha: La posición espacial de un jugador está determinada por el punto del terreno de juego donde sus pies hacen contacto. Si está suspendido en el aire, mantiene exactamente la misma condición del lugar donde pisó por última vez.",
                            "11.2 Contacto con los árbitros: Los árbitros forman parte del terreno de juego. Si el balón toca a un colegiado, se considera que ha impactado en la superficie sobre la cual este se encuentra de pie."
                        )
                        12 -> listOf(
                            "12.1 Salto entre dos: Solo se realiza al arranque del primer cuarto en el círculo central entre dos oponentes.",
                            "12.2 Flecha de posesión alterna: Todas las situaciones de balón retenido (lucha), comienzo del segundo, tercer y cuarto periodo, o dudas reglamentarias sobre la posesión, se resuelven entregando el balón al equipo que indique la flecha de posesión alterna ubicada en la mesa de control (la cual rota de dirección tras cada uso)."
                        )
                        13 -> listOf(
                            "13.1 Pases y manejo: El baloncesto se juega exclusivamente con las manos. Se permite pasar, lanzar, palear, rodar o botar el balón en cualquier dirección dentro de los límites reglamentarios.",
                            "13.2 Contacto ilegal con el pie: Golpear o bloquear el balón de manera intencionada con el pie o la pierna constituye una violación. Sin embargo, si el balón toca el pie o la pierna de forma involuntaria, el juego continúa sin interrupción."
                        )
                        14 -> listOf(
                            "14.1 Control individual y de equipo: Un jugador toma el control cuando sostiene o bota un balón vivo, o cuando tiene el balón a su disposición para un saque o tiro libre. El control de equipo se mantiene mientras un integrante de dicho conjunto posea o se pase el balón.",
                            "14.2 Fin del control: El control de equipo concluye cuando un contrincante obtiene el balón, cuando el balón pasa a estar muerto o cuando se realiza un tiro a canasta y el balón abandona la mano del atacante."
                        )
                        15 -> listOf(
                            "15.1 Inicio de la acción: La mecánica de tiro empieza cuando el jugador inicia el movimiento continuo que precede al lanzamiento a canasta (habitualmente al elevar los brazos o impulsar el cuerpo).",
                            "15.2 Finalización: La acción de tiro concluye cuando el balón ha abandonado la mano del lanzador y, si el jugador estaba en el aire, cuando vuelve a tocar el suelo con ambos pies de forma estable."
                        )
                        16 -> listOf(
                            "16.1 Definición: Se considera canasta anotada cuando un balón vivo entra por la parte superior del aro y pasa a través de la red o queda dentro de ella.",
                            "16.2 Escala de puntuación: 1 punto: Canasta convertida mediante un lanzamiento de tiro libre. 2 puntos: Canasta lograda desde la zona de tiro de campo de 2 puntos. 3 puntos: Canasta conseguida tras un lanzamiento efectuado por detrás del arco delimitador de 6,75 metros.",
                            "16.3 Canasta propia: Si un jugador encesta por error en su propia canasta, los 2 puntos se le adjudican en la planilla al capitán del equipo contrario. Encestar intencionadamente en el propio aro se sanciona como violación y el tiro no suma puntos."
                        )
                        17 -> listOf(
                            "17.1 Ejecución: El jugador encargado del saque debe posicionarse fuera de los límites de la pista en el punto indicado por el árbitro.",
                            "17.2 Restricciones del sacador: Tiene un plazo máximo de 5 segundos para soltar el balón desde que está a su disposición. No puede pisar la cancha mientras sostiene el balón ni hacer que este bote fuera antes de ser lanzado a un compañero.",
                            "17.3 Restricciones de la defensa: Ningún defensor puede cruzar con ninguna parte de su cuerpo la línea divisoria antes de que el balón haya sido soltado por el sacador."
                        )
                        18 -> listOf(
                            "18.1 Definición y duración: Es una pausa en el partido otorgada al primer entrenador para dar instrucciones tácticas. Dura exactamente 1 minuto.",
                            "18.2 Distribución permitida: 2 tiempos muertos durante la primera mitad (primer y segundo cuarto). 3 tiempos muertos durante la segunda mitad (tercer y cuarto cuarto). Nota: Máximo 2 de estos pueden solicitarse en los últimos 2 minutos del partido. 1 tiempo muerto por cada periodo de prórroga disputado."
                        )
                        19 -> listOf(
                            "19.1 Procedimiento: Los cambios de jugadores deben ser solicitados por el sustituto en persona ante la mesa de control durante las pausas reglamentarias (oportunidades de sustitución).",
                            "19.2 Ingreso: El relevo solo puede entrar al campo a través de la zona de sustitución una vez que el árbitro realice el gesto oficial de autorización y el jugador sustituido haya abandonado la pista."
                        )
                        20 -> listOf(
                            "20.1 Causas: Un equipo pierde el partido por incomparecencia si: Se niega a jugar tras ser requerido por el árbitro. No se presenta en la cancha con 5 jugadores listos para competir 15 minutos después de la hora oficial programada. Sus acciones deliberadas impiden la continuidad del juego.",
                            "20.2 Sanción: El equipo rival gana el partido con un resultado anotado de 20 a 0. El equipo infractor recibe 0 puntos en la tabla de clasificación."
                        )
                        21 -> listOf(
                            "21.1 Causa: Ocurre si durante el transcurso del encuentro el número de jugadores activos de un equipo sobre la pista queda reducido a menos de 2 (debido a faltas o expulsiones).",
                            "21.2 Sanción: Si el equipo afectado iba perdiendo, se mantiene el marcador del momento. Si el equipo afectado iba ganando, el resultado se modifica automáticamente a 2 a 0 a favor del oponente. El equipo derrotado por inferioridad suma 1 punto en la tabla de clasificación."
                        )
                        22 -> listOf(
                            "22.1 Definición: Una violación es una infracción del reglamento de juego que no constituye una falta personal o técnica.",
                            "22.2 Penalización: El balón se concede al equipo contrario para un saque de banda/fondo desde el punto más cercano al lugar donde se cometió la infracción (salvo directamente detrás del tablero), a menos que las reglas estipulen un procedimiento distinto."
                        )
                        23 -> listOf(
                            "23.1 Jugador fuera del terreno: Un jugador está fuera de la pista cuando cualquier parte de su cuerpo toca el suelo o cualquier objeto (que no sea un jugador) que se encuentre sobre o fuera de las líneas delimitadoras.",
                            "23.2 Balón fuera del terreno: El balón se considera fuera de la pista cuando toca: A un jugador, persona, superficie u objeto situado fuera de los límites del campo. Los soportes del tablero, la parte posterior del tablero o cualquier objeto situado encima del terreno de juego."
                        )
                        24 -> listOf(
                            "24.1 Definición: Un regate inicia cuando un jugador toma el control de un balón vivo, lo lanza, palmea, rueda o bota en el terreno de juego y lo vuelve a tocar antes de que haga contacto con otro jugador.",
                            "24.2 Infracción por dobles: Un jugador no puede realizar un segundo regate continuo después de haber concluido el primero, a menos que haya perdido el control del balón debido a un tiro a canasta, un toque de un adversario o un pase desviado."
                        )
                        25 -> listOf(
                            "25.1 Definición: Es el desplazamiento no permitido de uno o ambos pies en cualquier dirección mientras se sostiene un balón vivo dentro del terreno de juego.",
                            "25.2 Determinación del pie de pivote: Al recibir el balón con ambos pies sobre el terreno, el jugador puede elegir cualquiera de los dos como pie de pivote. Para iniciar un regate, el balón debe abandonar la mano del jugador antes de levantarse el pie de pivote. Para pasar o tirar a canasta, el pie de pivote puede levantarse, pero no puede volver a tocar el suelo antes de que el balón haya salido de las manos."
                        )
                        26 -> listOf(
                            "26.1 Regla general: Un jugador atacante no puede permanecer de manera continua durante más de 3 segundos dentro de la zona restringida (zona pintada o \"llave\") de los oponentes mientras su equipo mantenga el control de un balón vivo en su pista delantera y el reloj de juego esté en marcha.",
                            "26.2 Excepciones: Se concede un margen si el jugador intenta salir activamente de la zona restringida, si se encuentra en acción de tiro o si un compañero está driblando/tirando hacia la canasta."
                        )
                        27 -> listOf(
                            "27.1 Definición: Ocurre cuando un jugador en pista sostiene un balón vivo y un defensor oponente establece una posición legal de defensa a una distancia máxima de 1 metro.",
                            "27.2 Límite de tiempo: El jugador estrechamente marcado dispone de un tiempo límite de 5 segundos para pasar, lanzar a canasta o iniciar un regate."
                        )
                        28 -> listOf(
                            "28.1 Regla general: Cuando un equipo obtiene la posesión de un balón vivo en su pista trasera, debe lograr que el balón pase a la pista delantera en un plazo máximo de 8 segundos continuos.",
                            "28.2 Criterio de llegada: El balón se considera en la pista delantera cuando no toca a ningún jugador ni superficie en la pista trasera y hace contacto con la pista delantera o con un atacante/árbitro que toque la pista delantera."
                        )
                        29 -> listOf(
                            "29.1 Límite de posesión: Todo equipo que tome el control de un balón vivo en la pista dispone de 24 segundos para realizar un lanzamiento al aro contrario.",
                            "29.2 Requisitos del tiro: El balón debe abandonar la mano del lanzador antes de que suene la señal del reloj de tiro y, posteriormente, debe tocar el aro o ingresar en la canasta.",
                            "29.3 Cuenta reducida a 14 segundos: El reloj de posesión se reinicia a 14 segundos si el juego se detiene por una infracción/falta cometida por la defensa en pista delantera estando el reloj en 13 segundos o menos, o tras un rebote ofensivo después de un tiro de campo o tiro libre no convertido que toque el aro."
                        )
                        30 -> listOf(
                            "30.1 Definición: Ocurre cuando un equipo en control del balón en su pista delantera hace de forma ilegal que el balón regrese a su pista trasera y un jugador de ese mismo equipo es el primero en tocarlo en la zona posterior."
                        )
                        31 -> listOf(
                            "31.1 Intercepción (Goaltending): Se sanciona cuando un jugador toca el balón durante un tiro a canasta mientras este se encuentra completamente por encima del nivel del aro y en trayectoria descendente, o bien después de que el balón haya tocado el tablero.",
                            "31.2 Interferencia: Se comete cuando un jugador toca el aro, el tablero o la red mientras el balón está sobre el aro o dentro de la canasta, o cuando introduce la mano a través de la red para tocar el balón.",
                            "31.3 Penalizaciones: Si la comete la defensa: Se da la canasta por válida automáticamente (otorgando 2 o 3 puntos según el tiro). Si la comete el ataque: Se anula la canasta (si entró) y se otorga saque de banda al rival."
                        )
                        32 -> listOf(
                            "32.1 Definición: Una falta es una infracción del reglamento que implica un contacto personal ilegal con un oponente o una conducta antideportiva.",
                            "32.2 Registro: Toda falta se anota en el acta de partido al infractor correspondiente y se sanciona conforme a lo estipulado en la normativa."
                        )
                        33 -> listOf(
                            "33.1 Principio de cilindro y verticalidad: Cada jugador tiene derecho al espacio cilíndrico que ocupa dentro de la pista (delimitado por las palmas de las manos por delante, las nalgas por detrás y el exterior de las piernas y brazos a los lados). El jugador tiene derecho a saltar verticalmente dentro de su cilindro sin que un oponente invada dicho espacio ilegalmente.",
                            "33.2 Posición legal de defensa: Un defensor la establece cuando está encarando a su oponente y mantiene ambos pies en contacto con el suelo.",
                            "33.3 Defensa a un jugador con balón: No se aplican elementos de tiempo ni distancia para establecer la posición legal de defensa; la responsabilidad del contacto recae en el atacante si el defensor ya ocupaba la posición.",
                            "33.4 Defensa a un jugador sin balón: Deben respetarse los elementos de tiempo y distancia (el defensor debe dar espacio suficiente para que el rival cambie de dirección o se detenga, máximo 2 pasos normales).",
                            "33.5 Jugador en el aire: Tiene derecho a aterrizar en el mismo lugar o en un punto predecible, siempre que esa zona no estuviera ocupada legalmente por un rival antes de iniciarse el salto.",
                            "33.6 Pantalla (Screening) legal e ilegal: Legal: Cuando el bloqueador está inmóvil dentro de su cilindro al producirse el contacto. Ilegal: Si el bloqueador está en movimiento o no respeta la distancia/tiempo al colocar la pantalla fuera del campo de visión del rival.",
                            "33.7 Semicírculo de no-carga (No-charge area): En la zona del semicírculo bajo el aro, no se sanciona falta de ataque por carga al atacante que penetra, siempre que el defensor esté pisando o dentro de la línea del semicírculo (salvo uso ilegal de manos/brazos o falta antideportiva).",
                            "33.8 Simulación de falta (Flopping): Acción deliberada de fingir o exagerar un contacto sin que exista infracción real. Se penaliza con una advertencia inicial y, si se repite, con falta técnica."
                        )
                        34 -> listOf(
                            "34.1 Definición: Contacto ilegal de un jugador sobre un oponente, independientemente de si el balón está vivo o muerto.",
                            "34.2 Penalización: Sin acción de tiro: Saque de banda/fondo para el equipo rival (salvo que el equipo infractor esté en penalización por acumulación de faltas de equipo, en cuyo caso se concederán 2 tiros libres). En acción de tiro convertida: La canasta vale y se concede 1 tiro libre adicional. En acción de tiro fallada: Se conceden 2 o 3 tiros libres, según la zona desde donde se realizó el lanzamiento."
                        )
                        35 -> listOf(
                            "35.1 Definición: Situación en la que dos jugadores contrarios se cometen faltas personales o antideportivas/descalificantes de forma mutua y casi simultánea.",
                            "35.2 Reanudación: Se anota una falta a cada jugador. El juego se reanuda concediendo el balón al equipo que tenía el control previo o mediante la flecha de posesión alterna si no había control claro."
                        )
                        36 -> listOf(
                            "36.1 Definición: Infracción de carácter conductual (sin contacto físico directo) cometida por un jugador, entrenador o integrante del banquillo. Incluye faltas de respeto a los árbitros, retrasar el juego o gestos antideportivos.",
                            "36.2 Penalización: Se concede 1 tiro libre al equipo contrario. Tras el tiro libre, el partido se reanuda en el mismo punto donde se detuvo el juego antes de la señal."
                        )
                        37 -> listOf(
                            "37.1 Criterios de evaluación: Contacto excesivo o duro sobre un rival en un intento de jugar el balón. Contacto causado por un defensor por la espalda o de lado sobre un atacante en progresión hacia la canasta sin defensores entre él y el aro (campo abierto / clear path). Contacto ilegal del defensor sobre el sacador antes de que el balón abandone sus manos en los últimos 2 minutos del cuarto periodo o de cada prórroga.",
                            "37.2 Penalización: Otorga tiros libres (1, 2 o 3 según la situación de tiro) más la posesión del balón con un saque de banda en la línea de saque de la pista delantera (con 14 segundos en el reloj de tiro)."
                        )
                        38 -> listOf(
                            "38.1 Definición: Cualquier infracción por acción antideportiva flagrante o conducta grave cometida por jugadores, sustitutos o cuerpo técnico.",
                            "38.2 Sanción disciplinaria: El infractor es expulsado inmediatamente y debe dirigiéndose al vestuario o abandonar el pabellón.",
                            "38.3 Penalización en pista: Se conceden tiros libres más la posesión del balón para el equipo contrincante (saque en pista delantera con 14 segundos)."
                        )
                        39 -> listOf(
                            "39.1 Definición: Altercado con violencia física que involucra a dos o más personas dentro o fuera de la pista.",
                            "39.2 Abandono de banquillo: Cualquier sustituto o miembro del cuerpo técnico que abandone la zona del banquillo durante una trifulca será descalificado de forma automática.",
                            "39.3 Excepción del entrenador: El primer entrenador (o su asistente) únicamente puede salir del banquillo para colaborar de manera activa con los árbitros en la pacificación o detención de la trifulca."
                        )
                        40 -> listOf(
                            "40.1 Salida obligatoria: Un jugador que acumule 5 faltas (ya sean personales, técnicas o antideportivas) debe ser informado inmediatamente por el árbitro y abandonar el partido.",
                            "40.2 Plazo de sustitución: El jugador sancionado dispone de un tiempo máximo de 30 segundos para completar su cambio y sentarse en el banquillo.",
                            "40.3 Infracción posterior: Si un jugador que ya cometió sus 5 faltas vuelve a ingresar o comete una falta posterior en cancha, dicha falta se registrará en el acta como una falta técnica de banquillo atribuida al entrenador."
                        )
                        41 -> listOf(
                            "41.1 Umbral de penalización: Un equipo entra en situación de penalización por faltas colectivas cuando acumula 4 faltas dentro de un mismo cuarto.",
                            "41.2 Cómputo en descansos y prórrogas: Las faltas cometidas durante los intervalos de descanso entre periodos se consideran acumuladas para el cuarto siguiente. Todas las faltas cometidas durante los tiempos suplementarios (prórrogas) se suman al cómputo de faltas del cuarto periodo.",
                            "41.3 Sanción por penalización: Una vez alcanzado el límite de 4 faltas en el periodo, cualquier falta personal subsiguiente cometida sobre un rival que no esté en acción de tiro será penalizada con 2 tiros libres, en lugar de otorgar un saque de banda/fondo."
                        )
                        42 -> listOf(
                            "42.1 Definición: Suceden cuando se sancionan múltiples faltas o infracciones adicionales durante la misma interrupción del reloj de juego antes de que el balón vuelva a estar vivo.",
                            "42.2 Procedimiento de resolución y compensación: Se anotan todas las faltas y se identifican las sanciones en el orden cronológico exacto en que fueron pita das. Las sanciones de idéntico valor señaladas contra ambos equipos (o las faltas dobles) se cancelan y anulan mutuamente en el orden en que ocurrieron. Una vez anuladas, se consideran como si no hubiesen sucedido. La concesión de la posesión del balón como parte de la última sanción aplicable anula cualquier derecho anterior a la posesión. Una vez que el balón queda vivo para administrar el primer tiro libre o saque, las sanciones ya iniciadas no pueden ser objeto de compensaciones posteriores. Si tras cancelar sanciones equivalentes no restan más penas por ejecutar, el juego se reanuda entregando el balón al equipo que tenía el control en el momento de la primera infracción (o mediante la flecha de posesión alterna si no había control claro)."
                        )
                        43 -> listOf(
                            "43.1 Definición: Oportunidad sin oposición concedida a un jugador para sumar 1 punto lanzando desde detrás de la línea de tiro libre y dentro del semicírculo.",
                            "43.2 Normas para el tirador: Debe efectuar el lanzamiento en un plazo máximo de 5 segundos a partir del momento en que el árbitro ponga el balón a su disposición. No puede realizar fintas o amagos deliberados ni pisar o rebasar la línea de tiro libre hasta que el balón haya entrado en la canasta o tocado el aro.",
                            "43.3 Normas para los jugadores en los pasillos de rebote: Se permite la ocupación de un máximo de 5 espacios (3 defensores y 2 atacantes) situados de forma alterna a los lados de la botella. Los jugadores no pueden ingresar en la zona restringida (llave) ni tocar el balón hasta que este haya abandonado las manos del lanzador.",
                            "43.4 Jugadores fuera de los pasillos: Deben permanecer por detrás del arco de 3 puntos y por encima de la prolongación de la línea de tiro libre hasta que el lanzamiento impacte en el aro o concluya la acción.",
                            "43.5 Penalización por infracción en tiro libre: Si la infracción la comete el propio tirador, el punto no es válido. Si la infracción la comete un defensor y el tiro libre no se convierte, se le concede un intento de repetición al tirador. Si ambos equipos cometen violación simultánea en el último tiro, el punto se anula y el juego se reanuda con un salto/posesión alterna."
                        )
                        44 -> listOf(
                            "44.1 Casos de corrección permitidos: Los árbitros únicamente están facultados para corregir un fallo reglamentario si incurren de forma involuntaria en alguno de los siguientes supuestos: Conceder uno o varios tiros libres no merecidos. Omitir la concesión de uno o varios tiros libres merecidos. Adjudicar o anular puntos por equivocación en el marcador. Permitir que el jugador equivocado ejecute uno o varios tiros libres.",
                            "44.2 Límite de tiempo para la corrección: El error solo es subsanable si se descubre e identifica antes de que el balón vuelva a estar vivo tras la primera interrupción del reloj de juego posterior a la puesta en marcha del reloj que siguió a la comisión del error.",
                            "44.3 Efecto tras la expiración: Si transcurre dicho intervalo de tiempo sin que se advierta la falla, la situación queda consolidada y ya no podrá ser modificada bajo ningún concepto."
                        )
                        45 -> listOf(
                            "45.1 Composición de la tripulación: El cuerpo arbitral estará conformado por un árbitro principal (Crew Chief) y uno o dos árbitros auxiliares. Estarán respaldados por la mesa de control, compuesta por: anotador, ayudante de anotador, cronometrador y operador del reloj de tiro, además de un comisionado (si estuviera designado).",
                            "45.2 Neutralidad e imparcialidad: Ninguno de los colegiados u oficiales de mesa puede pertenecer ni estar vinculado de forma alguna a los equipos contendientes.",
                            "45.3 Uniformidad: Los árbitros deben vestir la indumentaria homologada por la FIBA (camiseta oficial, pantalón negro, calcetines negros y zapatillas de baloncesto negras) e ir provistos de un silbato."
                        )
                        46 -> listOf(
                            "46.1 Inspección previa: Debe revisar y aprobar todo el equipamiento de juego, la cancha, los relojes, la mesa de control y los dispositivos electrónicos antes del partido.",
                            "46.2 Elección del balón de juego: Seleccionará el balón oficial de entre los proporcionados por el equipo local o la organización.",
                            "46.3 Gestión del juego y decisiones de autoridad: Administra el salto entre dos para iniciar el primer cuarto. Posee la facultad de interrumpir o suspender el encuentro si las condiciones de la cancha o el público comprometen la seguridad. Decide sobre cualquier aspecto o eventualidad que no esté explícitamente contemplada en el reglamento (casos imprevisibles).",
                            "46.4 Revisión en pantalla (IRS - Instant Replay System): Es el único autorizado para revisar el sistema de repetición en vídeo en las situaciones permitidas por el reglamento (como tiros de último segundo, faltas descalificantes o errores de reloj).",
                            "46.5 Cierre del encuentro: Revisa y firma el acta oficial al finalizar el partido, concluyendo de esta manera la jurisdicción arbitral sobre el juego."
                        )
                        47 -> listOf(
                            "47.1 Competencia en pista: Tienen plena potestad para señalar e imponer sanciones por faltas y violaciones cometidas tanto dentro como fuera de las líneas de demarcación, desde 20 minutos antes de la hora fijada para el partido hasta que suena la señal final y se aprueba el acta.",
                            "47.2 Uso del silbato: Hacer sonar el silbato detiene inmediatamente el reloj de juego y deja el balón muerto. Los árbitros no deben pitar tras un tiro libre o canasta convertida válida a menos que exista una falta o violación adicional.",
                            "47.3 Criterio y coherencia: Deben aplicar el reglamento con consistencia y equilibrio, aplicando el principio de \"ventaja/desventaja\" para no interrumpir el juego de manera innecesaria por contactos marginales o sin impacto directo en la acción."
                        )
                        48 -> listOf(
                            "48.1 Registro del acta oficial (Anotador): Cronología de puntos anotados (tiros libres, canastas de 2 y 3 puntos). Registro nominativo de jugadores, quintetos iniciales y sustituciones. Contabilidad de las faltas personales, técnicas y antideportivas de cada jugador (notificando de inmediato al árbitro cuando un jugador alcanza su 5.ª falta). Control del número de tiempos muertos consumidos por cada equipo. Operación de la flecha de posesión alterna.",
                            "48.2 Manejo del marcador público (Ayudante de anotador): Controla la pantalla del marcador para que refleje fielmente los puntos y faltas registrados por el anotador en el acta en papel o electrónica. Asiste al anotador en la verificación de cualquier discrepancia de puntos o faltas."
                        )
                        49 -> listOf(
                            "49.1 Control del tiempo de juego: Pone en marcha el reloj principal en los saltos (cuando es palmeado), en saques de banda (cuando el balón toca a un jugador en cancha) o en rebotes de tiros libres fallados. Detiene el reloj cuando un árbitro pita falta/violación, cuando se concede un tiempo muerto o cuando se convierte una canasta en los últimos 2 minutos del cuarto periodo o de las prórrogas.",
                            "49.2 Medición de pausas: Mide la duración exacta de 1 minuto para los tiempos muertos. Controla los intervalos de descanso entre periodos (2 minutos o 15 minutos en el descanso). Emite una señal acústica potente y automática al vencer el tiempo de cada cuarto o prórroga."
                        )
                        50 -> listOf(
                            "50.1 Control del reloj de 24/14 segundos: Inicia o reanuda la cuenta cuando un equipo toma el control de un balón vivo en pista. Restablece el reloj a 24 segundos cuando la posesión cambia de equipo tras una canasta, rebote defensivo o falta cometida en pista trasera por la defensa. Restablece el reloj a 14 segundos tras un rebote ofensivo propio o tras una falta/infracción defensiva en pista delantera cuando restaban menos de 14 segundos en el dispositivo.",
                            "50.2 Detención e interrupción: Detiene la cuenta (sin reiniciar) cuando el balón sale fuera por toque defensivo sin cambio de control. Apaga o detiene el dispositivo cuando resta menos tiempo en el reloj principal de juego que el tiempo restante en el reloj de tiro (menos de 24 o 14 segundos). Emite una alarma sonora independiente para indicar el agotamiento del tiempo de posesión del equipo atacante."
                        )
                        else -> listOf("Información detallada del artículo $i en proceso...")
                    },
                    progress = existing?.progress ?: 0.0f,
                    isCompleted = existing?.isCompleted ?: false
                ))
            }

            repository.syncRules(rules)
            repository.syncArticles(articlesList)
            
            val quizQuestions = listOf(
                // ARTÍCULO 1
                QuizQuestion("q1", "¿Cuál es el objetivo central del basketball?", listOf("Posesión", "Encestar y defender", "Faltas", "Jugar mitad pista"), 1, "Encestar en aro contrario y defender el propio.", "a1"),
                QuizQuestion("q2", "¿Cuántos jugadores por equipo en pista?", listOf("3", "4", "5", "6"), 2, "En pista juegan 5 por equipo.", "a1"),
                QuizQuestion("q3", "¿A quién notificar descuadres?", listOf("Capitán", "Espectadores", "Árbitros", "Comisionado"), 2, "De inmediato a los árbitros.", "a1"),
                QuizQuestion("q4", "¿Cómo se determina al ganador?", listOf("Primero en prórroga", "Menos faltas", "Más puntos al final", "Oficiales de mesa"), 2, "El conjunto con más puntos al finalizar el tiempo.", "a1"),

                // ARTÍCULO 2
                QuizQuestion("q5", "¿Cuáles son las dimensiones oficiales del terreno de juego?", listOf("26 x 14 metros", "28 x 15 metros", "30 x 16 metros", "32 x 19 metros"), 1, "El campo debe medir 28 metros de longitud por 15 metros de ancho.", "a2"),
                QuizQuestion("q6", "¿Cuál es el ancho de todas las líneas de demarcación?", listOf("3 cm", "5 cm", "7 cm", "10 cm"), 1, "Todas las líneas deben medir 5 cm de ancho.", "a2"),
                QuizQuestion("q7", "¿A qué distancia se fija el arco de 3 puntos?", listOf("6,25 metros", "6,50 metros", "6,75 metros", "7,24 metros"), 2, "El arco de 3 puntos está fijado a 6,75 metros.", "a2"),
                QuizQuestion("q8", "¿Cuántos asientos deben tener las zonas de banquillo?", listOf("12 asientos", "14 asientos", "16 asientos", "20 asientos"), 2, "Se deben acondicionar 16 asientos para el personal del equipo.", "a2"),

                // ARTÍCULO 3
                QuizQuestion("q9", "¿Qué dispositivo controla la cuenta regresiva de posesión?", listOf("Reloj de juego", "Cronómetro", "Reloj de tiro (24/14s)", "Acta del partido"), 2, "El dispositivo del reloj de tiro indica los 24 y 14 segundos.", "a3"),
                QuizQuestion("q10", "¿Qué elemento resuelve situaciones de balón retenido?", listOf("Señal acústica", "Flecha de posesión alterna", "Tablero electrónico", "Marcador de faltas"), 1, "La flecha de posesión alterna resuelve estas situaciones.", "a3"),
                QuizQuestion("q11", "¿Cuántas señales acústicas independientes se requieren?", listOf("Una señal", "Dos señales", "Tres señales", "No son obligatorias"), 1, "Se requieren dos: una para el reloj de juego y otra para el de tiro.", "a3"),

                // ARTÍCULO 4
                QuizQuestion("q12", "¿Cuántos jugadores puede registrar cada equipo como máximo?", listOf("10 jugadores", "12 jugadores", "14 jugadores", "15 jugadores"), 1, "Cada equipo puede registrar hasta 12 jugadores elegibles.", "a4"),
                QuizQuestion("q13", "¿Cuál es la altura mínima de los números en la espalda?", listOf("10 cm", "15 cm", "20 cm", "25 cm"), 2, "Los números de la espalda deben medir al menos 20 cm de alto.", "a4"),
                QuizQuestion("q14", "¿Se permiten joyas durante el partido?", listOf("Sí, si están cubiertas", "Solo anillos de boda", "No, nada que corte", "Solo relojes deportivos"), 2, "No se permite ningún objeto que pueda causar lesiones (joyas, anillos).", "a4"),
                QuizQuestion("q20", "¿Cuántos acompañantes adicionales puede tener la delegación?", listOf("5 acompañantes", "8 acompañantes", "10 acompañantes", "Ilimitados"), 1, "La delegación se complementa con hasta 8 acompañantes técnicos/médicos.", "a4"),
                QuizQuestion("q21", "¿Cuál es la norma para la camiseta en relación al pantalón?", listOf("Puede ir fuera", "Debe ir por dentro", "Depende del equipo", "Solo en canasta"), 1, "La camiseta debe ir por dentro del pantalón según el reglamento.", "a4"),

                // ARTÍCULO 5
                QuizQuestion("q15", "¿Cuándo se detiene el juego si hay un lesionado?", listOf("Inmediatamente siempre", "Al tirar o perder posesión", "Cuando el coach lo pida", "Solo si el balón sale"), 1, "Se interrumpe cuando el equipo tira o pierde posesión, salvo emergencia.", "a5"),
                QuizQuestion("q16", "¿Cuánto tiempo tiene un jugador para recuperarse antes de ser sustituido?", listOf("15 segundos", "30 segundos", "1 minuto", "2 minutos"), 0, "Debe ser sustituido si no se recupera en aprox. 15 segundos.", "a5"),
                QuizQuestion("q22", "¿Qué excepción permite mantener a un lesionado sin sustituirlo?", listOf("Si anota un triple", "Si el equipo pide tiempo muerto", "Si el capitán lo decide", "Ninguna excepción"), 1, "Puede quedarse si el equipo pide tiempo muerto y se recupera antes de finalizar.", "a5"),
                QuizQuestion("q23", "¿Cuándo puede regresar un jugador que tenía una herida abierta?", listOf("En cuanto deje de doler", "Tras el primer cuarto", "Cuando el sangrado pare y esté cubierta", "No puede regresar"), 2, "Regresa cuando el sangrado se detenga y la zona esté totalmente cubierta.", "a5"),

                // ARTÍCULO 6
                QuizQuestion("q17", "¿Quién es el único autorizado para solicitar aclaraciones respetuosas?", listOf("El Entrenador", "Cualquier jugador", "El Capitán", "El Delegado"), 2, "El capitán es el único autorizado para dirigirse a los árbitros.", "a6"),
                QuizQuestion("q24", "¿Qué debe hacer el capitán si el equipo decide protestar formalmente?", listOf("Gritar al anotador", "Firmar el acta tras el juego", "Abandonar la pista", "Llamar a la federación"), 1, "Debe firmar la casilla de protesta en el acta oficial inmediatamente al finalizar.", "a6"),

                // ARTÍCULO 7
                QuizQuestion("q18", "¿Con cuánto tiempo de antelación se confirma la lista de nombres?", listOf("20 minutos", "30 minutos", "40 minutos", "60 minutos"), 2, "El entrenador debe confirmar la lista al menos 40 minutos antes.", "a7"),
                QuizQuestion("q19", "¿Quién es el único autorizado para permanecer de pie dando instrucciones?", listOf("Entrenador principal", "Primer asistente", "Capitán", "Ambos entrenadores"), 0, "Únicamente el primer entrenador puede estar de pie en el banquillo.", "a7"),
                QuizQuestion("q25", "¿Con cuánto tiempo de antelación se debe firmar el acta ratificando al quinteto inicial?", listOf("5 minutos", "10 minutos", "15 minutos", "20 minutos"), 1, "Debe firmar el acta al menos 10 minutos antes del partido.", "a7"),
                QuizQuestion("q26", "¿Quién tiene la autoridad para solicitar tiempos muertos y sustituciones?", listOf("El Capitán", "Cualquier jugador", "El Entrenador Principal", "El Médico"), 2, "Corresponde al primer entrenador solicitar tiempos muertos y sustituciones.", "a7"),

                // ARTÍCULO 8
                QuizQuestion("q27", "¿En cuántos periodos se divide un encuentro oficial?", listOf("2 periodos", "3 periodos", "4 periodos", "5 periodos"), 2, "El partido se divide en 4 periodos de 10 minutos cada uno.", "a8"),
                QuizQuestion("q28", "¿Cuánto dura cada periodo de prórroga?", listOf("2 minutos", "3 minutos", "5 minutos", "10 minutos"), 2, "Cada periodo suplementario tiene una duración de 5 minutos.", "a8"),
                QuizQuestion("q29", "¿Cuánto dura el descanso de mitad de partido?", listOf("5 minutos", "10 minutos", "15 minutos", "20 minutos"), 2, "El descanso entre el segundo y tercer cuarto es de 15 minutos.", "a8"),
                QuizQuestion("q30", "¿Cuántos minutos de intervalo hay entre el 1º y 2º cuarto?", listOf("1 minuto", "2 minutos", "5 minutos", "No hay descanso"), 1, "Se concede un intervalo de descanso de 2 minutos entre periodos.", "a8"),

                // ARTÍCULO 9
                QuizQuestion("q31", "¿Cuándo arranca formalmente el primer cuarto?", listOf("Al pitar el árbitro", "Al palmeo del balón", "Al tocar un jugador", "A los 10 segundos"), 1, "El primer cuarto arranca cuando el balón es palmeado en el salto entre dos.", "a9"),
                QuizQuestion("q32", "¿Con cuántos jugadores debe contar un equipo para iniciar el partido?", listOf("3 jugadores", "4 jugadores", "5 jugadores", "12 jugadores"), 2, "Ningún partido puede dar comienzo si un equipo no cuenta con 5 jugadores.", "a9"),
                QuizQuestion("q33", "¿Qué marca el final de un periodo o del partido?", listOf("El silbato final", "La señal del cronometrador", "La alarma del reloj de juego", "Gesto del árbitro"), 2, "Finaliza en el instante en que suena la alarma acústica del reloj principal.", "a9"),
                QuizQuestion("q70", "¿Cuándo inician los demás periodos tras el primero?", listOf("Al palmeo", "A disposición para saque", "Al pitar ref", "Tras 2 min"), 1, "Los demás periodos inician cuando el balón queda a disposición para el saque.", "a9"),

                // ARTÍCULO 10
                QuizQuestion("q34", "¿Cuándo se considera que el balón está vivo en un saque?", listOf("Al botar fuera", "Al estar a disposición", "Al entrar en pista", "Al tocar el aro"), 1, "Está vivo cuando el árbitro lo pone a disposición del jugador para el saque.", "a10"),
                QuizQuestion("q35", "¿Cuándo pasa el balón a estar muerto?", listOf("Al pasar media pista", "Tras un triple fallido", "Al sonar el silbato", "Al botar muy alto"), 2, "El balón queda muerto cuando un árbitro hace sonar su silbato.", "a10"),
                QuizQuestion("q71", "¿Cuándo queda vivo en un salto entre dos?", listOf("Al tocar suelo", "Al palmeo", "Al salir de mano del ref", "A los 5 seg"), 2, "Queda vivo al salir de la mano del árbitro en el lanzamiento del salto.", "a10"),
                QuizQuestion("q72", "¿Qué señal acústica hace que el balón quede muerto?", listOf("Alarma fin periodo", "Silbato público", "Grito coach", "Bote fuerte"), 0, "Suena la señal de finalización del reloj de juego o de tiro.", "a10"),

                // ARTÍCULO 11
                QuizQuestion("q36", "¿Qué determina la ubicación espacial de un jugador?", listOf("Su sombra", "Donde tiene el balón", "Donde sus pies tocan suelo", "Su mirada"), 2, "Está determinada por el punto donde sus pies hacen contacto con el suelo.", "a11"),
                QuizQuestion("q37", "¿Qué ocurre si el balón toca a un árbitro en pista?", listOf("Se repite jugada", "Es balón muerto", "Es como tocar el suelo", "Es falta técnica"), 2, "Se considera que ha impactado en la superficie donde el árbitro está de pie.", "a11"),
                QuizQuestion("q73", "¿Qué condición mantiene un jugador que está en el aire?", listOf("Ninguna", "La del último lugar pisado", "La del punto de caída", "La de pista delantera siempre"), 1, "Mantiene exactamente la misma condición del lugar donde pisó por última vez.", "a11"),
                QuizQuestion("q74", "¿Cómo se consideran los árbitros respecto al terreno?", listOf("Obstáculos", "Fuera de límites", "Parte del terreno", "Espectadores"), 2, "Los árbitros forman parte del terreno de juego.", "a11"),

                // ARTÍCULO 12
                QuizQuestion("q38", "¿En qué momento del partido se realiza el salto entre dos?", listOf("Cada cuarto", "Solo al inicio del 1º", "Al inicio y tras prórroga", "En cada lucha"), 1, "Solo se realiza al arranque del primer cuarto en el círculo central.", "a12"),
                QuizQuestion("q39", "¿Cómo se resuelven situaciones de balón retenido tras el inicio?", listOf("Repitiendo el salto", "Flecha de posesión", "Tiro libre", "Saque de fondo"), 1, "Se resuelven entregando el balón según indique la flecha de posesión alterna.", "a12"),
                QuizQuestion("q75", "¿Cuándo rota la dirección de la flecha de posesión?", listOf("Cada minuto", "Al final de mitad", "Tras cada uso reglamentario", "Por orden del ref"), 2, "La flecha rota de dirección inmediatamente tras su uso reglamentario.", "a12"),
                QuizQuestion("q76", "¿Cómo inicia el segundo periodo de juego?", listOf("Salto entre dos", "Saque según flecha", "Sorteo moneda", "Posesión del local"), 1, "Inicia con un saque entregado al equipo que indique la flecha.", "a12"),

                // ARTÍCULO 13
                QuizQuestion("q40", "¿Con qué parte del cuerpo se juega exclusivamente?", listOf("Pies y manos", "Cualquier parte", "Solo con las manos", "Manos y cabeza"), 2, "El baloncesto se juega exclusivamente con las manos.", "a13"),
                QuizQuestion("q41", "¿Qué constituye una violación en el manejo del balón?", listOf("Tocarlo sin querer", "Bloqueo intencionado con pie", "Rodarlo por el suelo", "Pasarlo con una mano"), 1, "Golrep o bloquear el balón intencionadamente con el pie es violación.", "a13"),
                QuizQuestion("q77", "¿Qué ocurre si el balón toca el pie de forma involuntaria?", listOf("Violación", "Balón muerto", "El juego continúa", "Falta técnica"), 2, "Si el contacto es involuntario, el juego continúa sin interrupción.", "a13"),
                QuizQuestion("q78", "¿Qué acciones están permitidas con el balón?", listOf("Solo pasar", "Solo botar", "Pasar, lanzar, rodar, botar", "Patear"), 2, "Se permite pasar, lanzar, palear, rodar o botar en cualquier dirección.", "a13"),

                // ARTÍCULO 14
                QuizQuestion("q42", "¿Cuándo toma un jugador el control individual del balón?", listOf("Al mirarlo", "Al sostenerlo o botarlo", "Al estar cerca", "Tras un rebote"), 1, "Se toma el control cuando se sostiene o bota un balón vivo.", "a14"),
                QuizQuestion("q43", "¿Cuándo concluye el control de equipo?", listOf("Al pasar media pista", "Al tirar a canasta", "Al pedir tiempo muerto", "Tras 10 segundos"), 1, "Concluye cuando se realiza un tiro y el balón abandona la mano.", "a14"),
                QuizQuestion("q79", "¿Cuánto tiempo se mantiene el control de equipo?", listOf("10 segundos", "Hasta perder posesión", "Mientras se posea o pase", "Solo al botar"), 2, "Se mantiene mientras un integrante del conjunto posea o se pase el balón.", "a14"),
                QuizQuestion("q80", "¿Qué evento pone fin al control de equipo?", listOf("Pisar fuera", "Contrincante obtiene balón", "Gritar", "Salto entre dos"), 1, "Concluye cuando un contrincante obtiene el balón o este queda muerto.", "a14"),

                // ARTÍCULO 15
                QuizQuestion("q44", "¿Cuándo se inicia formalmente la mecánica de tiro?", listOf("Al cruzar media pista", "Al iniciar el movimiento continuo", "Al saltar solamente", "Al tocar el aro"), 1, "Empieza cuando el jugador inicia el movimiento continuo que precede al lanzamiento.", "a15"),
                QuizQuestion("q45", "¿Cuándo concluye la acción de tiro de un jugador en el aire?", listOf("Al soltar el balón", "Al tocar el aro", "Al caer de forma estable", "Al pitar el árbitro"), 2, "Concluye cuando el jugador vuelve a tocar el suelo con ambos pies de forma estable.", "a15"),
                QuizQuestion("q81", "¿Qué movimiento es habitual al iniciar un tiro?", listOf("Bajar brazos", "Elevar brazos o impulso", "Correr veloz", "Mirar al coach"), 1, "Habitualmente al elevar los brazos o impulsar el cuerpo.", "a15"),
                QuizQuestion("q82", "¿Cuándo termina la acción de tiro en suelo?", listOf("Al anotar", "Al soltar el balón", "Al pitar ref", "Al tocar red"), 1, "Concluye cuando el balón ha abandonado la mano del lanzador.", "a15"),

                // ARTÍCULO 16
                QuizQuestion("q46", "¿Cuántos puntos vale un tiro libre convertido?", listOf("1 punto", "2 puntos", "3 puntos", "Depende del cuarto"), 0, "Una canasta de tiro libre vale exactamente 1 punto.", "a16"),
                QuizQuestion("q47", "¿A qué distancia se marca el arco de 3 puntos?", listOf("6,25 metros", "6,50 metros", "6,75 metros", "7,24 metros"), 2, "Los lanzamientos por detrás de los 6,75 metros valen 3 puntos.", "a16"),
                QuizQuestion("q48", "¿Qué ocurre si un jugador encesta por error en su propio aro?", listOf("No vale nada", "Vale 2 ptos para el rival", "Se repite", "Es falta técnica"), 1, "Los 2 puntos se adjudican al capitán del equipo contrario.", "a16"),
                QuizQuestion("q83", "¿Cuál es la definición de canasta anotada?", listOf("Tocar el aro", "Entrar por arriba del aro", "Tocar el tablero", "Pasar cerca"), 1, "Cuando un balón vivo entra por la parte superior del aro y pasa la red.", "a16"),
                QuizQuestion("q84", "¿Qué ocurre si encestas intencionadamente en propio aro?", listOf("Suma 2 rival", "Se repite", "Es violación (0 pts)", "Falta técnica"), 2, "Es una violación y el tiro no suma puntos.", "a16"),

                // ARTÍCULO 17
                QuizQuestion("q49", "¿Cuánto tiempo tiene el sacador para soltar el balón?", listOf("3 segundos", "5 segundos", "8 segundos", "24 segundos"), 1, "Tiene un plazo máximo de 5 segundos para soltar el balón.", "a17"),
                QuizQuestion("q50", "¿Qué restricción tiene el defensor ante un saque?", listOf("No puede saltar", "No puede gritar", "No puede cruzar la línea", "No puede mirar"), 2, "Ningún defensor puede cruzar la línea divisoria antes de soltar el balón.", "a17"),
                QuizQuestion("q60", "¿Qué ocurre si el balón bota fuera de la pista antes de ser lanzado por el sacador?", listOf("Es violación del sacador", "Se repite el saque", "Es falta técnica", "Sigue el juego"), 0, "El sacador no puede hacer que el balón bote fuera antes de ser lanzado.", "a17"),
                QuizQuestion("q61", "¿Dónde debe posicionarse el sacador?", listOf("En cualquier parte", "Donde indique el árbitro", "En la esquina", "Tras su canasta"), 1, "Debe posicionarse fuera de los límites en el punto indicado por el árbitro.", "a17"),

                // ARTÍCULO 18
                QuizQuestion("q51", "¿Cuánto dura exactamente un tiempo muerto?", listOf("30 segundos", "45 segundos", "1 minuto", "2 minutos"), 2, "El tiempo muerto dura exactamente 1 minuto.", "a18"),
                QuizQuestion("q52", "¿Cuántos tiempos muertos tiene un equipo en la 2ª mitad?", listOf("1 tiempo", "2 tiempos", "3 tiempos", "5 tiempos"), 2, "Se conceden 3 tiempos muertos durante la segunda mitad.", "a18"),
                QuizQuestion("q62", "¿Cuántos tiempos muertos máximo se permiten en los últimos 2 minutos?", listOf("1 tiempo", "2 tiempos", "3 tiempos", "Ilimitados"), 1, "Máximo 2 de los 3 tiempos muertos de la 2ª mitad se pueden pedir en los últimos 2 min.", "a18"),
                QuizQuestion("q63", "¿Cuántos tiempos muertos se dan por cada prórroga?", listOf("0", "1", "2", "3"), 1, "Se concede 1 tiempo muerto por cada periodo de prórroga.", "a18"),

                // ARTÍCULO 20
                QuizQuestion("q53", "¿Cuál es el resultado anotado en un Forfeit?", listOf("10-0", "15-0", "20-0", "2-0"), 2, "El equipo rival gana con un resultado de 20 a 0.", "a20"),
                QuizQuestion("q54", "¿Cuánto tiempo se espera a un equipo antes de declarar incomparecencia?", listOf("5 minutos", "10 minutos", "15 minutos", "30 minutos"), 2, "Se declaran 15 minutos después de la hora oficial programada.", "a20"),
                QuizQuestion("q64", "¿Cuántos puntos recibe el equipo infractor en la tabla por Forfeit?", listOf("0 puntos", "1 punto", "2 puntos", "Sanción económica"), 0, "El equipo infractor recibe 0 puntos en la tabla de clasificación.", "a20"),
                QuizQuestion("q65", "¿Qué ocurre si un equipo se niega a jugar?", listOf("Se espera 1 hora", "Es un Forfeit (20-0)", "Es un Default (2-0)", "Se cancela el torneo"), 1, "Negarse a jugar tras ser requerido es causa de Forfeit.", "a20"),

                // ARTÍCULO 21
                QuizQuestion("q55", "¿Cuándo se declara la pérdida por inferioridad (Default)?", listOf("Sin entrenador", "Menos de 2 jugadores", "Menos de 5 jugadores", "Por conducta"), 1, "Ocurre si el número de jugadores activos queda reducido a menos de 2.", "a21"),
                QuizQuestion("q56", "¿Cuántos puntos suma el equipo derrotado por Default en la tabla?", listOf("0 puntos", "1 punto", "2 puntos", "3 puntos"), 1, "El equipo derrotado por inferioridad suma 1 punto en la clasificación.", "a21"),
                QuizQuestion("q66", "¿Qué resultado se anota en un Default si el equipo infractor iba ganando?", listOf("Mismo marcador", "20-0", "2-0", "Empate"), 2, "Si iban ganando, el resultado se modifica automáticamente a 2-0 rival.", "a21"),
                QuizQuestion("q67", "¿Qué ocurre con el marcador si el equipo infractor ya iba perdiendo en un Default?", listOf("Se pone 2-0", "Se mantiene el marcador", "Se pone 0-0", "Se anula"), 1, "Si el equipo infractor ya iba perdiendo, se mantiene el marcador del momento.", "a21")
            )
            quizQuestions.forEach { repository.addQuizQuestion(it) }
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

    fun completeArticle(article: Article) {
        viewModelScope.launch {
            if (!article.isCompleted) {
                repository.updateArticle(article.copy(isCompleted = true, progress = 1.0f))
                val user = uiState.value.user
                user?.let {
                    val updatedUser = it.copy(xp = it.xp + 100)
                    repository.login(updatedUser)
                }
            }
        }
    }

    fun updateQuizStats(newQuestions: Int, newCorrect: Int) {
        viewModelScope.launch {
            val user = uiState.value.user ?: return@launch
            val currentTime = System.currentTimeMillis()
            val oneDayMillis = 24 * 60 * 60 * 1000L
            val isNextDay = currentTime - user.lastActivityDate > oneDayMillis && currentTime - user.lastActivityDate < 2 * oneDayMillis
            val isSameDay = currentTime - user.lastActivityDate < oneDayMillis

            val newStreak = when {
                isNextDay -> user.streak + 1
                isSameDay -> user.streak
                else -> 1
            }

            val updatedUser = user.copy(
                streak = if (newStreak == 0) 1 else newStreak,
                lastActivityDate = currentTime,
                totalQuestions = user.totalQuestions + newQuestions,
                correctAnswers = user.correctAnswers + newCorrect
            )
            repository.login(updatedUser)
        }
    }

    companion object {
        fun provideFactory(repository: RuleRepository): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return DashboardViewModel(repository) as T
            }
        }
    }
}
