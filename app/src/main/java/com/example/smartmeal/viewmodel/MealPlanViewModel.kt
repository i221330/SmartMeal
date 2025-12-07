package com.example.smartmeal.viewmodel

import androidx.lifecycle.*
import com.example.smartmeal.data.model.MealPlan
import com.example.smartmeal.data.repository.MealPlanRepository
import kotlinx.coroutines.launch

class MealPlanViewModel(private val repository: MealPlanRepository) : ViewModel() {

    val allMealPlans: LiveData<List<MealPlan>> = repository.allMealPlans

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _success = MutableLiveData<String?>()
    val success: LiveData<String?> = _success

    private val _weekMealPlans = MutableLiveData<List<MealPlan>>()
    val weekMealPlans: LiveData<List<MealPlan>> = _weekMealPlans

    fun loadWeekMealPlans(startDate: Long, endDate: Long) = viewModelScope.launch {
        try {
            _isLoading.value = true
            val plans = repository.getMealPlansByDate(startDate, endDate)
            _weekMealPlans.value = plans
        } catch (e: Exception) {
            _error.value = e.message ?: "Failed to load meal plans"
        } finally {
            _isLoading.value = false
        }
    }

    fun getMealPlanForSlot(date: Long, mealType: String): LiveData<MealPlan?> = liveData {
        try {
            val plan = repository.getMealPlanByDateAndType(date, mealType)
            emit(plan)
        } catch (e: Exception) {
            _error.postValue(e.message ?: "Failed to load meal plan")
            emit(null)
        }
    }

    fun insertMealPlan(mealPlan: MealPlan) = viewModelScope.launch {
        try {
            _isLoading.value = true
            repository.insertMealPlan(mealPlan)
            _success.value = "Meal planned successfully"
        } catch (e: Exception) {
            _error.value = e.message ?: "Failed to add meal plan"
        } finally {
            _isLoading.value = false
        }
    }

    fun updateMealPlan(mealPlan: MealPlan) = viewModelScope.launch {
        try {
            _isLoading.value = true
            repository.updateMealPlan(mealPlan)
            _success.value = "Meal plan updated successfully"
        } catch (e: Exception) {
            _error.value = e.message ?: "Failed to update meal plan"
        } finally {
            _isLoading.value = false
        }
    }

    fun deleteMealPlan(id: String) = viewModelScope.launch {
        try {
            _isLoading.value = true
            repository.deleteMealPlan(id)
            _success.value = "Meal plan deleted successfully"
        } catch (e: Exception) {
            _error.value = e.message ?: "Failed to delete meal plan"
        } finally {
            _isLoading.value = false
        }
    }

    fun clearOldMealPlans(beforeDate: Long) = viewModelScope.launch {
        try {
            repository.clearOldMealPlans(beforeDate)
            _success.value = "Old meal plans cleared"
        } catch (e: Exception) {
            _error.value = e.message ?: "Failed to clear old plans"
        }
    }

    fun syncData(userId: String) = viewModelScope.launch {
        try {
            _isLoading.value = true
            repository.syncToCloud(userId)
            repository.syncFromCloud(userId)
            _success.value = "Meal plans synced successfully"
        } catch (e: Exception) {
            _error.value = e.message ?: "Failed to sync meal plans"
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

class MealPlanViewModelFactory(
    private val repository: MealPlanRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MealPlanViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MealPlanViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

