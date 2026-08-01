package com.tecsup.hoopaxis.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String = "",
    val name: String = "",
    val email: String = "",
    val profileImageUrl: String? = null,
    val isLoggedIn: Boolean = false,
    val isAdmin: Boolean = false,
    val streak: Int = 0,
    val xp: Int = 0,
    val lastActivityDate: Long = 0,
    val totalQuestions: Int = 0,
    val correctAnswers: Int = 0
)
