package com.tecsup.hoopaxis.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
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

    private fun handleAuthError(e: Exception): String {
        return when (e) {
            is FirebaseNetworkException -> "Sin conexión a Internet. Verifica tu red y vuelve a intentarlo."
            is FirebaseAuthWeakPasswordException -> "La contraseña es muy débil. Debe tener al menos 6 caracteres."
            is FirebaseAuthInvalidCredentialsException -> "Credenciales incorrectas o formato de correo inválido."
            is FirebaseAuthUserCollisionException -> "Este correo electrónico ya está registrado."
            is FirebaseAuthInvalidUserException -> "No existe una cuenta con este correo."
            else -> e.localizedMessage ?: "Ocurrió un error inesperado. Inténtalo de nuevo."
        }
    }

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
                _error.value = handleAuthError(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signIn(email: String, password: String, onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            _error.value = "Por favor, completa todos los campos."
            return
        }
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
                _error.value = handleAuthError(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signUp(email: String, password: String, name: String, onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank() || name.isBlank()) {
            _error.value = "Por favor, completa todos los campos."
            return
        }
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
                _error.value = handleAuthError(e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun clearError() {
        _error.value = null
    }

    fun updateError(message: String) {
        _error.value = message
    }
}
