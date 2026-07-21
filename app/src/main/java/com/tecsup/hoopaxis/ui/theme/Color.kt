package com.tecsup.hoopaxis.ui.theme

import androidx.compose.ui.graphics.Color

object AppColors {
    val Background    = Color(0xFF0D0520)
    val Purple        = Color(0xFFC96BFF)
    val Pink          = Color(0xFFFF6B9D)
    val Blue          = Color(0xFF5BC8FF)
    val Gold          = Color(0xFFFFD166)
    val Green         = Color(0xFF50DC78)
    val Red           = Color(0xFFFF5064)
    val GlassSurface  = Color.White.copy(alpha = 0.09f)
    val GlassBorder   = Color.White.copy(alpha = 0.18f)
    val GlassDeep     = Color(0xFF0D0520).copy(alpha = 0.72f)
    val TextPrimary   = Color.White
    val TextSecondary = Color.White.copy(alpha = 0.55f)
    val TextMuted     = Color.White.copy(alpha = 0.32f)
}

// Keep legacy for Material theme compatibility if needed, but AppColors is preferred
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)
val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)
