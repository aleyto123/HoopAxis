# HoopAxis 🏀

HoopAxis es una aplicación móvil moderna diseñada para el estudio, consulta y dominio del **Reglamento Oficial de Baloncesto FIBA 2026**. Enfocada en árbitros, entrenadores y jugadores profesionales, la app combina una experiencia visual de vanguardia con herramientas de seguimiento de aprendizaje.

## 🚀 Tecnologías y Stack

El proyecto está construido bajo las mejores prácticas de desarrollo moderno en Android:

- **Lenguaje:** [Kotlin](https://kotlinlang.org/) (2.1.0+)
- **Interfaz de Usuario:** [Jetpack Compose](https://developer.android.com/jetpack/compose) con **Material 3**.
- **Arquitectura:** MVVM (Model-View-ViewModel) con Clean Architecture principles.
- **Base de Datos Local:** [Room Database](https://developer.android.com/training/data-storage/room) para persistencia de progreso y datos del reglamento.
- **Networking:** [Retrofit](https://square.github.io/retrofit/) & Gson para integración con APIs externas.
- **Servicios Cloud:** [Firebase](https://firebase.google.com/) (Authentication, Analytics, Google Services).
- **Procesamiento de Anotaciones:** KSP (Kotlin Symbol Processing).
- **Diseño Visual:** Estética *Glassmorphic* con gradientes dinámicos y componentes personalizados mediante `Canvas`.

## 📂 Estructura del Proyecto (`app/src/main/java/...`)

- **`data/`**: Capa de datos.
    - `local/`: Configuración de Room, DAOs y la base de datos `HoopAxisDatabase`.
    - `model/`: Clases de datos (POJOs/Entities) como `User`, `Chapter`, `RuleCategory`.
    - `repository/`: `RuleRepository` que actúa como fuente única de verdad (SSOT).
- **`ui/`**: Capa de presentación.
    - `components/`: Componentes UI reutilizables (Barras de navegación, tarjetas, badges).
    - `navigation/`: Gestión de rutas y grafos de navegación con `NavHost`.
    - `screens/`: Pantallas completas (Dashboard, Reglas, Detalle de Regla, Perfil).
    - `theme/`: Definición de colores, tipografías y estilos personalizados.
- **`viewmodel/`**: Lógica de UI y gestión de estados mediante `StateFlow`.

## ✨ Características Principales

1.  **Dashboard de Progreso:** Visualización en tiempo real del avance en el reglamento mediante anillos de progreso dinámicos.
2.  **8 Reglas Principales:** Organización del contenido basada en la estructura oficial de la FIBA (El Juego, Terreno, Equipos, Violaciones, Faltas, etc.).
3.  **Seguimiento de Capítulos:** Capacidad para continuar lecciones pendientes desde la pantalla principal.
4.  **Sistema de Insignias:** Reconocimiento visual de capítulos completados y en curso.
5.  **Modo Árbitro Pro:** Preparado para funcionalidades exclusivas, exámenes de certificación y eliminación de anuncios.

## 🛠 Requisitos de Ejecución

- **Android Studio:** Ladybug (2024.2.1) o superior.
- **JDK:** 17+.
- **Min SDK:** 26 (Android 8.0).
- **Target SDK:** 35.

## 📝 Notas de Desarrollo

La aplicación utiliza un sistema de **"Destructive Migration"** en Room durante la fase de desarrollo para facilitar cambios rápidos en el esquema de datos. La interfaz implementa `GlassmorphicCard`, un componente personalizado que utiliza transparencias y desenfoques para lograr una estética moderna.

## 🛡 Manejo de Errores y Seguridad

La aplicación implementa un sistema robusto de retroalimentación al usuario mediante `AuthViewModel`, capturando excepciones específicas de Firebase y transformándolas en mensajes amigables:

- **Conectividad:** Detección de falta de internet (`FirebaseNetworkException`) con aviso de reintentar conexión.
- **Validación de Acceso:** Mensajes claros para credenciales incorrectas, correos no registrados o formatos de email inválidos.
- **Seguridad de Registro:** Control de contraseñas débiles (mínimo 6 caracteres) y detección de cuentas duplicadas.
- **Validación Local:** Verificación de campos obligatorios antes de realizar peticiones al servidor para optimizar el tráfico de datos.

---
*Desarrollado con ❤️ para la comunidad del baloncesto.*
