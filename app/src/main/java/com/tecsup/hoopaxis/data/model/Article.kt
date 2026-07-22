package com.tecsup.hoopaxis.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articulos")
data class Article(
    @PrimaryKey val id: String,
    val chapterId: String,
    val title: String,
    val articleNumber: String,
    val paraphrase: String,
    val officialText: String,
    val keyPoints: List<String>,
    val isProOnly: Boolean,
    val isCompleted: Boolean = false
)
