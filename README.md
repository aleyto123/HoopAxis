# HoopAxis 🏀

HoopAxis es una aplicación móvil moderna diseñada para el estudio, consulta y dominio del **Reglamento FIBA 2026**. Enfocada en árbitros, entrenadores y jugadores profesionales, la app combina una experiencia visual de vanguardia con herramientas de seguimiento de aprendizaje personalizadas.

## 🚀 Tecnologías y Stack

El proyecto está construido bajo las mejores prácticas de desarrollo moderno en Android:

- **Lenguaje:** [Kotlin](https://kotlinlang.org/) (2.1.0+)
- **Interfaz de Usuario:** [Jetpack Compose](https://developer.android.com/jetpack/compose) con **Material 3**.
- **Arquitectura:** MVVM (Model-View-ViewModel) con principios de Clean Architecture.
- **Base de Datos Local:** [Room Database](https://developer.android.com/training/data-storage/room) con sistema de ordenamiento personalizado y limpieza de datos automática.
- **Servicios Cloud:** [Firebase](https://firebase.google.com/) (Authentication, Firestore).
- **Diseño Visual:** Estética *Glassmorphic* con gradientes dinámicos, progreso circular integrado y diversidad de iconos/colores por cada regla.

## 📂 Estructura del Proyecto (`app/src/main/java/...`)

- **`data/`**: Capa de datos.
    - `local/`: Configuración de Room, DAOs (`HoopAxisDao`) y la base de datos con migración destructiva para desarrollo.
    - `model/`: Entidades principales: `User`, `Rule` (8 reglas), `Article` (50 artículos) y `QuizQuestion`.
    - `repository/`: `RuleRepository` con lógica de **sincronización única por sesión** para optimizar el rendimiento.
- **`ui/`**: Capa de presentación.
    - `components/`: Componentes premium (`GlassCard`, `CircularProgress`, `BottomNavBar` actualizado).
    - `navigation/`: Gestión de rutas con `NavHost` optimizado para navegación secuencial entre lecciones.
    - `screens/`: Pantallas principales (`Dashboard`, `Rules`, `ArticlesScreen`, `LessonScreen` con diseño pedagógico).
    - `theme/`: Definición de la paleta de colores vibrante y tipografías.
- **`viewmodel/`**: Lógica de negocio y estados de UI mediante `StateFlow`.

## ✨ Características Principales

1.  **Dashboard de Progreso:** Visualización del avance real sobre los 50 artículos de la FIBA mediante anillos dinámicos.
2.  **Organización FIBA:** Contenido estructurado en las **8 Reglas Principales** (El Juego, Terreno, Equipos, Faltas, etc.).
3.  **50 Artículos Detallados:** Cada artículo cuenta con su propio emoji único, color temático y numeración entera correlativa (Art. 1 al Art. 50).
4.  **Lectura Secuencial:** Sistema de "Siguiente lección" que permite navegar fluidamente por todo el reglamento sin volver al menú.
5.  **Seguridad Legal:** Terminología cuidadosamente seleccionada (evitando la palabra "oficial" en la interfaz pública) para cumplir con normativas de propiedad intelectual.
6.  **Rendimiento Optimizado:** Los datos se actualizan solo al iniciar la aplicación, evitando recargas molestas y bugs visuales durante el uso.

## 🛠 Requisitos de Ejecución

- **Android Studio:** Ladybug (2024.2.1) o superior.
- **JDK:** 17+.
- **Min SDK:** 26 (Android 8.0).
- **Target SDK:** 35.

## 📝 Notas de Desarrollo

La aplicación utiliza un sistema de **"Initial Data Load"** que garantiza que siempre existan los 50 artículos con el orden correcto. El diseño de las tarjetas implementa `GlassmorphicCard`, utilizando transparencias y desenfoques para lograr una estética moderna y profesional acorde al nivel del arbitraje internacional.

---
*Desarrollado con ❤️ para la comunidad del baloncesto.*
