package com.tecsup.hoopaxis.data.model

data class Chapter(
    val id: Int,
    val ruleId: Int, // R1, R2...
    val chapterNumber: Int, // C1, C2...
    val title: String,
    val categoryName: String,
    val lessonsCount: Int,
    val progress: Float,
    val isLocked: Boolean = false,
    val tagColorHex: String
)
