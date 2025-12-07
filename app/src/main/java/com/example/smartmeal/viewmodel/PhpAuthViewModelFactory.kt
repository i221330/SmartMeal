package com.example.smartmeal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.smartmeal.data.repository.PhpAuthRepository

class PhpAuthViewModelFactory(
    private val repository: PhpAuthRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PhpAuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PhpAuthViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

