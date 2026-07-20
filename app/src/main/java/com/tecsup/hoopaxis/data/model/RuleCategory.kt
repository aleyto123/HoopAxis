package com.tecsup.hoopaxis.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rule_categories")
data class RuleCategory(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String, // Nueva descripción
    val chaptersCount: Int,
    val lessonsCount: Int, // Nuevas lecciones
    val progress: Float,
    val iconEmoji: String,
    val tagColorHex: String // Color para el R1, R2, etc.
)
