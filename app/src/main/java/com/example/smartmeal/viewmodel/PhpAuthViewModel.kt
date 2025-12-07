package com.example.smartmeal.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartmeal.data.model.User
import com.example.smartmeal.data.repository.PhpAuthRepository
import kotlinx.coroutines.launch

class PhpAuthViewModel(private val repository: PhpAuthRepository) : ViewModel() {

    private val TAG = "PhpAuthViewModel"

    private val _authState = MutableLiveData<PhpAuthState>(PhpAuthState.Initial)
    val authState: LiveData<PhpAuthState> = _authState

    fun signUp(email: String, password: String, displayName: String) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Starting signup for: $email")
                _authState.value = PhpAuthState.Loading

                val result = repository.signUpWithEmail(email, password, displayName)

                if (result.isSuccess) {
                    val user = result.getOrNull()
                    Log.d(TAG, "Signup successful: ${user?.email}")
                    _authState.value = PhpAuthState.Authenticated(user)
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Signup failed"
                    Log.e(TAG, "Signup failed: $error")
                    _authState.value = PhpAuthState.Error(error)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Signup exception", e)
                _authState.value = PhpAuthState.Error("Signup error: ${e.message}")
            }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Starting login for: $email")
                _authState.value = PhpAuthState.Loading

                val result = repository.signInWithEmail(email, password)

                if (result.isSuccess) {
                    val user = result.getOrNull()
                    Log.d(TAG, "Login successful: ${user?.email}")
                    _authState.value = PhpAuthState.Authenticated(user)
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Login failed"
                    Log.e(TAG, "Login failed: $error")
                    _authState.value = PhpAuthState.Error(error)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Login exception", e)
                _authState.value = PhpAuthState.Error("Login error: ${e.message}")
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                repository.signOut()
                _authState.value = PhpAuthState.SignedOut
            } catch (e: Exception) {
                Log.e(TAG, "Signout error", e)
            }
        }
    }

    fun clearError() {
        _authState.value = PhpAuthState.Initial
    }
}

sealed class PhpAuthState {
    object Initial : PhpAuthState()
    object Loading : PhpAuthState()
    data class Authenticated(val user: User?) : PhpAuthState()
    data class Error(val message: String) : PhpAuthState()
    object SignedOut : PhpAuthState()
}

