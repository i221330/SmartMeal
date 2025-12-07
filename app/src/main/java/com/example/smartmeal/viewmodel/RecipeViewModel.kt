package com.example.smartmeal.viewmodel

import androidx.lifecycle.*
import com.example.smartmeal.data.model.Recipe
import com.example.smartmeal.data.repository.RecipeRepository
import kotlinx.coroutines.launch

class RecipeViewModel(private val repository: RecipeRepository) : ViewModel() {

    val allRecipes: LiveData<List<Recipe>> = repository.allRecipes
    val favoriteRecipes: LiveData<List<Recipe>> = repository.favoriteRecipes

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _success = MutableLiveData<String?>()
    val success: LiveData<String?> = _success

    private val _searchResults = MutableLiveData<List<Recipe>>()
    val searchResults: LiveData<List<Recipe>> = _searchResults

    private val _currentRecipe = MutableLiveData<Recipe?>()
    val currentRecipe: LiveData<Recipe?> = _currentRecipe

    fun loadRecipeById(id: String) = viewModelScope.launch {
        try {
            _isLoading.value = true
            val recipe = repository.getRecipeById(id)
            _currentRecipe.value = recipe
        } catch (e: Exception) {
            _error.value = e.message ?: "Failed to load recipe"
        } finally {
            _isLoading.value = false
        }
    }

    fun searchRecipes(query: String) = viewModelScope.launch {
        try {
            _isLoading.value = true
            val results = if (query.isBlank()) {
                repository.getAllRecipesList()
            } else {
                repository.searchRecipes(query)
            }
            _searchResults.value = results
        } catch (e: Exception) {
            _error.value = e.message ?: "Failed to search recipes"
        } finally {
            _isLoading.value = false
        }
    }

    fun insertRecipe(recipe: Recipe) = viewModelScope.launch {
        try {
            _isLoading.value = true
            repository.insertRecipe(recipe)
            _success.value = "Recipe added successfully"
        } catch (e: Exception) {
            _error.value = e.message ?: "Failed to add recipe"
        } finally {
            _isLoading.value = false
        }
    }

    fun updateRecipe(recipe: Recipe) = viewModelScope.launch {
        try {
            _isLoading.value = true
            repository.updateRecipe(recipe)
            _success.value = "Recipe updated successfully"
        } catch (e: Exception) {
            _error.value = e.message ?: "Failed to update recipe"
        } finally {
            _isLoading.value = false
        }
    }

    fun toggleFavorite(recipeId: String, isFavorite: Boolean) = viewModelScope.launch {
        try {
            repository.updateFavoriteStatus(recipeId, isFavorite)
            _success.value = if (isFavorite) "Added to favorites" else "Removed from favorites"
        } catch (e: Exception) {
            _error.value = e.message ?: "Failed to update favorite status"
        }
    }

    fun deleteRecipe(recipe: Recipe) = viewModelScope.launch {
        try {
            _isLoading.value = true
            repository.deleteRecipe(recipe)
            _success.value = "Recipe deleted successfully"
        } catch (e: Exception) {
            _error.value = e.message ?: "Failed to delete recipe"
        } finally {
            _isLoading.value = false
        }
    }

    fun syncData(userId: String) = viewModelScope.launch {
        try {
            _isLoading.value = true
            repository.syncToCloud(userId)
            repository.syncFromCloud(userId)
            _success.value = "Recipes synced successfully"
        } catch (e: Exception) {
            _error.value = e.message ?: "Failed to sync recipes"
        } finally {
            _isLoading.value = false
        }
    }

    fun loadGlobalRecipes() = viewModelScope.launch {
        try {
            _isLoading.value = true
            repository.loadGlobalRecipes()
            _success.value = "Recipes loaded successfully"
        } catch (e: Exception) {
            _error.value = e.message ?: "Failed to load recipes"
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

class RecipeViewModelFactory(
    private val repository: RecipeRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecipeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

