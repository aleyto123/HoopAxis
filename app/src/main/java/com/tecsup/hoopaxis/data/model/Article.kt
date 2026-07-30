package com.tecsup.hoopaxis.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articulos")
data class Article(
    @PrimaryKey val id: String = "",
    val ruleId: String = "",
    val chapterId: String = "",
    val title: String = "",
    val emoji: String = "📄",
    val articleNumber: String = "",
    val color: String = "#C96BFF",
    val sortOrder: Int = 0,
    val paraphrase: String = "",
    val sourceText: String = "",
    val keyPoints: List<String> = emptyList(),
    val progress: Float = 0f,
    val isCompleted: Boolean = false
)
