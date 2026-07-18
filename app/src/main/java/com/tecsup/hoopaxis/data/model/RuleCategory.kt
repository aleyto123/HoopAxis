package com.tecsup.hoopaxis.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rule_categories")
data class RuleCategory(
    @PrimaryKey val id: Int,
    val title: String,
    val chaptersCount: Int,
    val progress: Float,
    val iconEmoji: String
)
