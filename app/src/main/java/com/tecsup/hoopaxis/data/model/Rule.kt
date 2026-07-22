package com.tecsup.hoopaxis.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reglas")
data class Rule(
    @PrimaryKey val id: String,
    val number: Int,
    val title: String,
    val subtitle: String,
    val emoji: String,
    val color: String,
    val glow: String,
    val chaptersCount: Int = 2,
    val progress: Float = 0f
)
