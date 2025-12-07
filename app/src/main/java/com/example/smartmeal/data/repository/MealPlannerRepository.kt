package com.example.smartmeal.data.repository

import android.util.Log
import com.example.smartmeal.data.api.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for Meal Planner operations
 */
class MealPlannerRepository {

    private val TAG = "MealPlannerRepository"
    private val api = RetrofitClient.api

    suspend fun getMealsForDate(userId: String, date: String): Result<MealPlanResponse> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Fetching meals for user: $userId, date: $date")
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

    suspend fun addMealToPlan(
        userId: String,
        recipeId: String,
        mealDate: String,
        mealType: String
    ): Result<AddMealResult> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Adding meal: recipe=$recipeId, date=$mealDate, type=$mealType")
                val request = AddMealRequest(
                    user_id = userId,
                    recipe_id = recipeId,
                    meal_date = mealDate,
                    meal_type = mealType
                )
                val response = api.addMealToPlan(request)

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (body.success && body.data != null) {
                        Log.d(TAG, "Successfully added meal: ${body.data.plan_id}")
                        Result.success(body.data)
                    } else {
                        Log.e(TAG, "API returned success=false")
                        Result.failure(Exception(body.message ?: "Failed to add meal"))
                    }
                } else {
                    Log.e(TAG, "API call failed: ${response.code()} - ${response.message()}")
                    Result.failure(Exception("Error: ${response.message()}"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception adding meal", e)
                Result.failure(e)
            }
        }
    }

    suspend fun getAllRecipes(): Result<List<RecipeDetail>> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Fetching all recipes")
                val response = api.getAllRecipes()

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (body.success && body.data != null) {
                        Log.d(TAG, "Successfully fetched ${body.data.size} recipes")
                        Result.success(body.data)
                    } else {
                        Result.failure(Exception("Failed to fetch recipes"))
                    }
                } else {
                    Result.failure(Exception("Error: ${response.message()}"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception fetching recipes", e)
                Result.failure(e)
            }
        }
    }
}

