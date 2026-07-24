package com.tecsup.hoopaxis.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "capitulos")
data class Chapter(
    @PrimaryKey val id: String = "",
    val ruleId: String = "",
    val number: Int = 0,
    val title: String = "",
    val emoji: String = "",
    val articlesCount: Int = 0,
    val progress: Float = 0f
)
