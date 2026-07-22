package com.tecsup.hoopaxis.data.repository

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object UserRepository {
    var isProUser by mutableStateOf(false)
    
    fun setPro(isPro: Boolean) {
        isProUser = isPro
    }
}
