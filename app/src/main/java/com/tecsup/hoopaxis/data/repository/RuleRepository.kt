package com.tecsup.hoopaxis.data.repository

import com.tecsup.hoopaxis.data.local.HoopAxisDao
import com.tecsup.hoopaxis.data.model.RuleCategory
import com.tecsup.hoopaxis.data.model.User
import kotlinx.coroutines.flow.Flow

class RuleRepository(private val dao: HoopAxisDao) {
    val currentUser: Flow<User?> = dao.getCurrentUser()
    val allCategories: Flow<List<RuleCategory>> = dao.getAllCategories()

    suspend fun login(user: User) {
        dao.insertUser(user)
    }

    suspend fun logout() {
        dao.logout()
    }

    suspend fun syncCategories(categories: List<RuleCategory>) {
        dao.insertCategories(categories)
    }
}
