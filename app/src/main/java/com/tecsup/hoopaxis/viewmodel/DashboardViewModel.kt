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
            val currentArticles = repository.allArticles.first()
            if (currentArticles.size < 50) {
                _isLoading.value = true
                loadInitialData()
                _isLoading.value = false
            } else {
                _isLoading.value = false
            }
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
                    paraphrase = when(i) {
                        1 -> "• Enfrentamiento 5 vs 5 en pista.\n• Objetivo: Encestar e impedir anotaciones.\n• Cumplir normas y conducta deportiva.\n• Gana el equipo con más puntos."
                        2 -> "• Superficie lisa y dura de 28x15 metros.\n• División en pista trasera y delantera.\n• Líneas de 5 cm de ancho.\n• Áreas técnicas fuera del terreno."
                        3 -> "• Elementos esenciales homologados.\n• Control de tiempos (juego y tiro).\n• Señalización de faltas y posesión.\n• Iluminación y actas digitales/físicas."
                        4 -> "• Registro de 12 jugadores y 8 acompañantes.\n• Uniformes del mismo tono dominante.\n• Números visibles (10cm pecho, 20cm espalda).\n• Prohibición de joyas u objetos peligrosos."
                        5 -> "• Interrupción por lesión en balón muerto.\n• Sustitución si recibe atención (aprox 15s).\n• Heridas abiertas deben cubrirse totalmente.\n• Excepción por tiempo muerto solicitado."
                        6 -> "• Único autorizado para hablar con árbitros.\n• Aclaraciones respetuosas en balón muerto.\n• Firma del acta en caso de protesta oficial.\n• Representación legal del equipo en pista."
                        7 -> "• Trámites de lista (40m) y quinteto (10m).\n• Único autorizado a estar de pie en banquillo.\n• Gestión de tiempos muertos y sustituciones.\n• Responsable del Coach Challenge (HCC)."
                        8 -> "• Duración: 4 periodos de 10 minutos de tiempo real.\n• Pausas: 2 min entre periodos 1-2 y 3-4.\n• Descanso de 15 min al medio tiempo.\n• Prórrogas de 5 min en caso de empate al final."
                        9 -> "• Inicio: palmeo en salto entre dos (1º cuarto).\n• Otros periodos: balón a disposición para saque.\n• Condición: mínimo 5 jugadores equipados por equipo.\n• Conclusión: alarma acústica del reloj principal."
                        10 -> "• Vivo: mano del árbitro en salto o disposición para tiro/saque.\n• Muerto: canasta válida, silbato arbitral o alarma final.\n• Detención del reloj en balón muerto.\n• Sincronización con el operador de mesa."
                        11 -> "• Ubicación: determinada por punto de contacto en suelo.\n• Aire: mantiene condición del último lugar pisado.\n• Árbitros: se consideran parte del terreno de juego.\n• Balón al tocar árbitro: regla de superficie igual."
                        12 -> "• Salto entre dos: solo al inicio del 1er cuarto.\n• Flecha: resuelve luchas y otros inicios de periodo.\n• Rotación: la flecha cambia tras cada entrega reglamentaria.\n• Mesa: encargada de gestionar la dirección de flecha."
                        13 -> "• Manejo: exclusivo con manos en cualquier dirección.\n• Violación: golpe intencionado con pie o pierna.\n• Accidental: contacto con pie no detiene el juego.\n• Prohibición: golpear el balón con el puño."
                        14 -> "• Control individual: al sostener o botar balón vivo.\n• Control equipo: mientras se posea o pase el balón.\n• Fin: robo rival, canasta, balón muerto o tiro.\n• Transición: tras abandono de mano en lanzamiento."
                        15 -> "• Inicio: movimiento continuo previo al lanzamiento.\n• Incluye elevación de brazos o impulso del cuerpo.\n• Finalización: balón fuera de mano y apoyo estable.\n• Protección del lanzador durante toda la acción técnica."
                        16 -> "• Canasta: balón vivo entra por parte superior del aro.\n• Valor: 1 (tiro libre), 2 (campo), 3 (tras 6.75m).\n• Propia accidental: suma 2 al capitán rival.\n• Propia intencionada: violación y tiro no suma."
                        17 -> "• Ejecución: posicionado fuera de límites según árbitro.\n• Tiempo: 5 segundos máximo para soltar el balón.\n• Restricción: no pisar cancha ni botar fuera antes.\n• Defensa: no cruzar línea divisoria antes del tiro."
                        18 -> "• Duración: pausa estratégica de exactamente 1 minuto.\n• Distribución: 2 en 1ª mitad, 3 en 2ª mitad (máx 2 ult 2m).\n• Prórroga: se concede 1 tiempo muerto adicional.\n• Solicitud: exclusiva por parte del primer entrenador."
                        19 -> "• Pedido: sustituto en persona ante la mesa de control.\n• Oportunidad: durante pausas reglamentarias autorizadas.\n• Ingreso: tras autorización y salida del jugador anterior.\n• Zona: el cambio se realiza por el área de sustitución."
                        20 -> "• Causas: Negarse a jugar, no presentarse 15 min después de la hora oficial programada o acciones deliberadas que impidan la continuidad del juego.\n• Sanción: El equipo rival gana 20 a 0 y el infractor recibe 0 puntos en la tabla."
                        21 -> "• Causa: Ocurre si durante el transcurso del encuentro el número de jugadores activos de un equipo sobre la pista queda reducido a menos de 2 (debido a faltas o expulsiones).\n• Sanción: Si el equipo afectado iba ganando, el resultado se modifica automáticamente a 2 a 0 rival. Si iba perdiendo, se mantiene el marcador. El derrotado suma 1 punto en la tabla."
                        else -> "Contenido pedagógico del artículo $i en desarrollo..."
                    },
                    keyPoints = when(i) {
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
                            "4.1 Integración y miembros elegibles: Cada equipo puede registrar hasta 12 jugadores con derecho a jugar (incluido un capitán). La delegación se complementa con 1 primer entrenador, un máximo de 2 entrenadores ayudantes y hasta 5 miembros adicionales del cuerpo técnico o médico (máximo 8 acompañantes en total). Todos deben figurar en el acta oficial antes del inicio del partido.",
                            "4.2 Indumentaria y uniformes: Camisetas del mismo tono dominante en parte delantera y trasera (por dentro del pantalón). Pantalones del mismo color dominante. Calcetines del mismo color visible. Números legibles y contrastantes en pecho (mín. 10 cm) y espalda (mín. 20 cm). Se permiten 0, 00 y del 1 al 99.",
                            "4.3 Equipamiento accesorio y protecciones: Prendas de compresión y accesorios deben ser del mismo tono uniforme para todos los jugadores del equipo que los utilicen. No se permite ningún objeto que pueda cortar o causar lesiones (joyas, anillos, accesorios metáicos sin acolchar)."
                        )
                        5 -> listOf(
                            "5.1 Interrupción por lesión: Si un jugador se lesiona, los árbitros pueden detener el partido. Si el balón está vivo, el juego solo se interrumpirá cuando el equipo con el balón tire, pierda la posesión o el balón quede muerto, salvo que sea imprescindible actuar de inmediato por la seguridad.",
                            "5.2 Sustitución obligatoria por atención médica: Si el jugador lesionado no puede recuperarse de inmediato (aproximadamente 15 segundos) o recibe atención médica, debe ser sustituido. Excepción: si el equipo solicita un tiempo muerto y el jugador se recupera antes de que finalice.",
                            "5.3 Sangrado y heridas abiertas: Cualquier jugador con sangrado o herida abierta debe abandonar el juego. Solo regresa si el sangrado se detuvo y la zona afectada está totalmente cubierta."
                        )
                        6 -> listOf(
                            "6.1 Representación en cancha: Es el único jugador autorizado para dirigirse a los árbitros durante las pausas para solicitar aclaraciones respetuosas solo cuando el balón esté muerto.",
                            "6.2 Firma de protesta: Si el equipo decide protestar formalmente el resultado, el capitán debe firmar la casilla correspondiente en el acta oficial inmediatamente después de concluido el juego."
                        )
                        7 -> listOf(
                            "7.1 Trámites antes del partido: Al menos 40 minutos antes, el primer entrenador debe confirmar la lista de jugadores. Al menos 10 minutos antes del partido, debe firmar el acta ratificando el quinteto inicial y banquillo.",
                            "7.2 Permanencia en pista: Únicamente el primer entrenador está autorizado a permanecer de pie durante el partido dentro de la zona de banquillo para dar instrucciones. El ayudante asume si el principal abandona por expulsión o enfermedad.",
                            "7.3 Solicitudes técnicas: Corresponde al primer entrenador solicitar tiempos muertos, pedidos de sustitución y el desafío de entrenador (HCC) cuando esté permitido."
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
                            "10.2 Balón muerto: Se presenta cuando se encesta un tiro de campo o tiro libre válido, un árbitro hace sonar su silbato con el balón en juego, o suena la señal acústica de finalización de tiempo del reloj de juego o del reloj de tiro de 24/14 segundos."
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
                            "20.1 Causas: Un equipo pierde el partido por incomparecencia si: se niega a jugar tras ser requerido por el árbitro; no se presenta en la cancha con 5 jugadores listos para competir 15 minutos después de la hora oficial programada; sus acciones deliberadas impiden la continuidad del juego.",
                            "20.2 Sanción: El equipo rival gana el partido con un resultado anotado de 20 a 0. El equipo infractor recibe 0 puntos en la tabla de clasificación."
                        )
                        21 -> listOf(
                            "21.1 Causa: Ocurre si durante el transcurso del encuentro el número de jugadores activos de un equipo sobre la pista queda reducido a menos de 2 (debido a faltas o expulsiones).",
                            "21.2 Sanción: Si el equipo afectado iba perdiendo, se mantiene el marcador del momento. Si el equipo afectado iba ganando, el resultado se modifica automáticamente a 2 a 0 a favor del oponente. El equipo derrotado por inferioridad suma 1 punto en la tabla de clasificación."
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
