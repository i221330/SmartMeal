package com.example.smartmeal.data.repository

import android.util.Log
import com.example.smartmeal.data.api.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for Home Screen Data
 */
class HomeRepository {

    private val TAG = "HomeRepository"
    private val api = RetrofitClient.api

    suspend fun getTodaysMeals(userId: String, date: String): Result<MealPlanResponse> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Fetching today's meals for user: $userId, date: $date")
                val response = api.getMealsForDate(userId, date)

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (body.success) {
                        Log.d(TAG, "Successfully fetched ${body.meals_count} meals")
                        Result.success(body)
                    } else {
                        Log.e(TAG, "API returned success=false")
                        Result.failure(Exception("Failed to fetch meals"))
                    }
                } else {
                    Log.e(TAG, "API call failed: ${response.code()} - ${response.message()}")
                    Result.failure(Exception("Error: ${response.message()}"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception fetching meals", e)
                Result.failure(e)
            }
        }
    }

    suspend fun getRecipeSuggestions(userId: String): Result<RecipeSuggestionResponse> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Fetching recipe suggestions for user: $userId")
                val response = api.getRecipeSuggestions(userId = userId)

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (body.success) {
                        Log.d(TAG, "Successfully fetched ${body.suggestions_count} suggestions")
                        Log.d(TAG, "User has ${body.pantry_items_count} pantry items")
                        Result.success(body)
                    } else {
                        Log.e(TAG, "API returned success=false")
                        Result.failure(Exception("Failed to fetch suggestions"))
                    }
                } else {
                    Log.e(TAG, "API call failed: ${response.code()} - ${response.message()}")
                    Result.failure(Exception("Error: ${response.message()}"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception fetching suggestions", e)
                Result.failure(e)
            }
        }
    }

    suspend fun getRecipeDetail(recipeId: String): Result<RecipeDetail> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Fetching recipe detail: $recipeId")
                val response = api.getRecipeDetail(recipeId = recipeId)

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (body.success && body.data != null) {
                        Log.d(TAG, "Successfully fetched recipe: ${body.data.title}")
                        Result.success(body.data)
                    } else {
                        Log.e(TAG, "API returned success=false or null data")
                        Result.failure(Exception("Recipe not found"))
                    }
                } else {
                    Log.e(TAG, "API call failed: ${response.code()} - ${response.message()}")
                    Result.failure(Exception("Error: ${response.message()}"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception fetching recipe detail", e)
                Result.failure(e)
            }
        }
    }
}

