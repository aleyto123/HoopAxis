package com.tecsup.hoopaxis.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.tecsup.hoopaxis.data.model.User
import com.tecsup.hoopaxis.data.repository.RuleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel(private val repository: RuleRepository) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun signInWithGoogle(idToken: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val result = auth.signInWithCredential(credential).await()
                val firebaseUser = result.user
                if (firebaseUser != null) {
                    val user = User(
                        id = firebaseUser.uid,
                        name = firebaseUser.displayName ?: "Árbitro",
                        email = firebaseUser.email ?: "",
                        isLoggedIn = true
                    )
                    repository.login(user)
                    onSuccess()
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al iniciar sesión con Google"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signIn(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                val firebaseUser = result.user
                if (firebaseUser != null) {
                    val user = User(
                        id = firebaseUser.uid,
                        name = firebaseUser.displayName ?: "Árbitro",
                        email = firebaseUser.email ?: "",
                        isLoggedIn = true
                    )
                    repository.login(user)
                    onSuccess()
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al iniciar sesión"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signUp(email: String, password: String, name: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val firebaseUser = result.user
                if (firebaseUser != null) {
                    val user = User(
                        id = firebaseUser.uid,
                        name = name,
                        email = email,
                        isLoggedIn = true
                    )
                    repository.login(user)
                    onSuccess()
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al registrarse"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
