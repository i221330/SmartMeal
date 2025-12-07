package com.example.smartmeal.data.repository

import android.util.Log
import com.example.smartmeal.data.api.AddPantryItemRequest
import com.example.smartmeal.data.api.ApiResponse
import com.example.smartmeal.data.api.IngredientDetail
import com.example.smartmeal.data.api.ShoppingItem
import com.example.smartmeal.data.api.SmartMealApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for Shopping List operations
 */
class ShoppingListRepository(private val api: SmartMealApiService) {

    companion object {
        private const val TAG = "ShoppingListRepository"
    }

    /**
     * Get user's shopping list
     */
    suspend fun getShoppingList(userId: String): Result<List<ShoppingItem>> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Fetching shopping list for user: $userId")
                val response = api.getShoppingList(userId)

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (body.success && body.data != null) {
                        Log.d(TAG, "Shopping list fetched: ${body.data.size} items")
                        Result.success(body.data)
                    } else {
                        Log.e(TAG, "API returned success=false")
                        Result.failure(Exception(body.message ?: "Failed to fetch shopping list"))
                    }
                } else {
                    Log.e(TAG, "API call failed: ${response.code()}")
                    Result.failure(Exception("Error: ${response.message()}"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception fetching shopping list", e)
                Result.failure(e)
            }
        }
    }

    /**
     * Add ingredients to shopping list
     */
    suspend fun addIngredients(
        userId: String,
        ingredients: List<Pair<IngredientDetail, String>>
    ): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Adding ${ingredients.size} ingredients to shopping list")

                // Add each ingredient that needs to be bought
                val itemsToAdd = ingredients.filter { (ingredient, quantity) ->
                    quantity.isNotEmpty() && quantity != "0"
                }

                if (itemsToAdd.isEmpty()) {
                    return@withContext Result.success("No items to add")
                }

                // Add items one by one (or implement batch API)
                itemsToAdd.forEach { (ingredient, quantity) ->
                    val request = mapOf(
                        "user_id" to userId,
                        "name" to ingredient.name,
                        "quantity" to quantity,
                        "category" to "ingredient"
                    )

                    val response = api.addShoppingItem(request)
                    if (!response.isSuccessful) {
                        Log.e(TAG, "Failed to add ${ingredient.name}")
                    }
                }

                Log.d(TAG, "All ingredients added successfully")
                Result.success("${itemsToAdd.size} items added to shopping list")
            } catch (e: Exception) {
                Log.e(TAG, "Exception adding ingredients", e)
                Result.failure(e)
            }
        }
    }

    /**
     * Mark item as purchased and move to pantry
     */
    suspend fun markAsPurchased(
        itemId: String,
        itemName: String,
        quantityBought: String,
        userId: String
    ): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Marking item $itemId as purchased")

                // Add to pantry
                val pantryRequest = AddPantryItemRequest(
                    user_id = userId,
                    name = itemName,
                    quantity = quantityBought
                )

                val pantryResponse = api.addPantryItem(pantryRequest)
                if (!pantryResponse.isSuccessful) {
                    return@withContext Result.failure(Exception("Failed to add to pantry"))
                }

                // Delete from shopping list
                val deleteResponse = api.deleteShoppingItem(itemId)
                if (!deleteResponse.isSuccessful) {
                    Log.e(TAG, "Failed to delete from shopping list")
                }

                Log.d(TAG, "Item marked as purchased successfully")
                Result.success("Added to pantry")
            } catch (e: Exception) {
                Log.e(TAG, "Exception marking as purchased", e)
                Result.failure(e)
            }
        }
    }

    /**
     * Delete item from shopping list
     */
    suspend fun deleteItem(itemId: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Deleting item: $itemId")
                val response = api.deleteShoppingItem(itemId)

                if (response.isSuccessful) {
                    Log.d(TAG, "Item deleted successfully")
                    Result.success("Item removed")
                } else {
                    Result.failure(Exception("Failed to delete item"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception deleting item", e)
                Result.failure(e)
            }
        }
    }

    /**
     * Add custom item to shopping list
     */
    suspend fun addCustomItem(
        userId: String,
        name: String,
        quantity: String
    ): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Adding custom item: $name")

                val request = mapOf(
                    "user_id" to userId,
                    "name" to name,
                    "quantity" to quantity,
                    "category" to "custom"
                )

                val response = api.addShoppingItem(request)
                if (response.isSuccessful) {
                    Result.success("Item added")
                } else {
                    Result.failure(Exception("Failed to add item"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception adding custom item", e)
                Result.failure(e)
            }
        }
    }
}

