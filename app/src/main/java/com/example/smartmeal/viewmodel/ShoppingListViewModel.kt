package com.example.smartmeal.viewmodel

import androidx.lifecycle.*
import com.example.smartmeal.data.model.ShoppingItem
import com.example.smartmeal.data.repository.ShoppingRepository
import kotlinx.coroutines.launch

class ShoppingListViewModel(private val repository: ShoppingRepository) : ViewModel() {

    val allShoppingItems: LiveData<List<ShoppingItem>> = repository.allShoppingItems

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _success = MutableLiveData<String?>()
    val success: LiveData<String?> = _success

    fun insertShoppingItem(item: ShoppingItem) = viewModelScope.launch {
        try {
            _isLoading.value = true
            repository.insertShoppingItem(item)
            _success.value = "Item added to shopping list"
        } catch (e: Exception) {
            _error.value = e.message ?: "Failed to add item"
        } finally {
            _isLoading.value = false
        }
    }

    fun updateShoppingItem(item: ShoppingItem) = viewModelScope.launch {
        try {
            repository.updateShoppingItem(item)
        } catch (e: Exception) {
            _error.value = e.message ?: "Failed to update item"
        }
    }

    fun toggleCompletion(id: String, isCompleted: Boolean) = viewModelScope.launch {
        try {
            repository.updateCompletionStatus(id, isCompleted)
        } catch (e: Exception) {
            _error.value = e.message ?: "Failed to update status"
        }
    }

    fun deleteShoppingItem(id: String) = viewModelScope.launch {
        try {
            repository.deleteShoppingItem(id)
            _success.value = "Item removed"
        } catch (e: Exception) {
            _error.value = e.message ?: "Failed to delete item"
        }
    }

    fun clearCompletedItems() = viewModelScope.launch {
        try {
            _isLoading.value = true
            repository.clearCompletedItems()
            _success.value = "Completed items cleared"
        } catch (e: Exception) {
            _error.value = e.message ?: "Failed to clear items"
        } finally {
            _isLoading.value = false
        }
    }

    fun syncData(userId: String) = viewModelScope.launch {
        try {
            _isLoading.value = true
            repository.syncToCloud(userId)
            repository.syncFromCloud(userId)
            _success.value = "Shopping list synced"
        } catch (e: Exception) {
            _error.value = e.message ?: "Failed to sync"
        } finally {
            _isLoading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun clearSuccess() {
        _success.value = null
    }
}

class ShoppingListViewModelFactory(
    private val repository: ShoppingRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShoppingListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ShoppingListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

