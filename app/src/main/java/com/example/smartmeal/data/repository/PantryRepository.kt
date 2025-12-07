package com.example.smartmeal.data.repository

import android.util.Log
import com.example.smartmeal.data.api.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for Pantry operations
 */
class PantryRepository {

    private val TAG = "PantryRepository"
    private val api = RetrofitClient.api

    suspend fun getUserPantry(userId: String): Result<List<PantryItem>> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Fetching pantry for user: $userId")
                val response = api.getUserPantry(userId)

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (body.success && body.data != null) {
                        Log.d(TAG, "Successfully fetched ${body.data.size} items")
                        Result.success(body.data)
                    } else {
                        Log.e(TAG, "API returned success=false")
                        Result.failure(Exception("Failed to fetch pantry"))
                    }
                } else {
                    Log.e(TAG, "API call failed: ${response.code()} - ${response.message()}")
                    Result.failure(Exception("Error: ${response.message()}"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception fetching pantry", e)
                Result.failure(e)
            }
        }
    }

    suspend fun addPantryItem(userId: String, name: String, quantity: String, category: String?): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Adding item: $name, quantity: $quantity")
                val request = AddPantryItemRequest(
                    user_id = userId,
                    name = name,
                    quantity = quantity,
                    category = category
                )
                val response = api.addPantryItem(request)

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (body.success) {
                        Log.d(TAG, "Successfully added item")
                        Result.success(body.message ?: "Item added")
                    } else {
                        Log.e(TAG, "API returned success=false")
                        Result.failure(Exception(body.message ?: "Failed to add item"))
                    }
                } else {
                    Log.e(TAG, "API call failed: ${response.code()} - ${response.message()}")
                    Result.failure(Exception("Error: ${response.message()}"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception adding item", e)
                Result.failure(e)
            }
        }
    }

    suspend fun updatePantryItem(itemId: String, quantity: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Updating item: $itemId, quantity: $quantity")
                val request = UpdatePantryItemRequest(
                    item_id = itemId,
                    quantity = quantity
                )
                val response = api.updatePantryItem(request)

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (body.success) {
                        Log.d(TAG, "Successfully updated item")
                        Result.success(body.message ?: "Item updated")
                    } else {
                        Log.e(TAG, "API returned success=false")
                        Result.failure(Exception(body.message ?: "Failed to update item"))
                    }
                } else {
                    Log.e(TAG, "API call failed: ${response.code()} - ${response.message()}")
                    Result.failure(Exception("Error: ${response.message()}"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception updating item", e)
                Result.failure(e)
            }
        }
    }

    suspend fun deletePantryItem(itemId: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Deleting item: $itemId")
                val response = api.deletePantryItem(itemId)

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (body.success) {
                        Log.d(TAG, "Successfully deleted item")
                        Result.success(body.message ?: "Item deleted")
                    } else {
                        Log.e(TAG, "API returned success=false")
                        Result.failure(Exception(body.message ?: "Failed to delete item"))
                    }
                } else {
                    Log.e(TAG, "API call failed: ${response.code()} - ${response.message()}")
                    Result.failure(Exception("Error: ${response.message()}"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception deleting item", e)
                Result.failure(e)
            }
        }
    }

    suspend fun searchIngredients(query: String): Result<List<MasterIngredient>> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Searching ingredients: $query")
                val response = api.searchIngredients(query)

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (body.success && body.data != null) {
                        Log.d(TAG, "Found ${body.data.size} ingredients")
                        Result.success(body.data)
                    } else {
                        Log.e(TAG, "API returned success=false")
                        Result.failure(Exception("Search failed"))
                    }
                } else {
                    Log.e(TAG, "API call failed: ${response.code()} - ${response.message()}")
                    Result.failure(Exception("Error: ${response.message()}"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception searching ingredients", e)
                Result.failure(e)
            }
        }
    }

    suspend fun getAllIngredients(): Result<List<MasterIngredient>> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Fetching all master ingredients")
                val response = api.getAllIngredients()

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (body.success && body.data != null) {
                        Log.d(TAG, "Fetched ${body.data.size} ingredients")
                        Result.success(body.data)
                    } else {
                        Result.failure(Exception("Failed to fetch ingredients"))
                    }
                } else {
                    Result.failure(Exception("Error: ${response.message()}"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception fetching ingredients", e)
                Result.failure(e)
            }
        }
    }
}

// Master ingredient model
data class MasterIngredient(
    val id: Int,
    val ingredient_id: String,
    val name: String,
    val category: String,
    val common_unit: String,
    val searchable_name: String
)

