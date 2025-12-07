package com.example.smartmeal.data.api

import retrofit2.Response
import retrofit2.http.*

/**
 * SmartMeal Backend API Service
 */
interface SmartMealApiService {

    // Recipes API
    @GET("recipes.php")
    suspend fun getAllRecipes(): Response<ApiResponse<List<RecipeDetail>>>

    @GET("recipes.php")
    suspend fun getRecipeSuggestions(
        @Query("action") action: String = "suggestions",
        @Query("user_id") userId: String
    ): Response<RecipeSuggestionResponse>

    @GET("recipes.php")
    suspend fun getRecipeDetail(
        @Query("action") action: String = "detail",
        @Query("recipe_id") recipeId: String
    ): Response<RecipeDetailResponse>

    @GET("recipes.php")
    suspend fun searchRecipes(
        @Query("action") action: String = "search",
        @Query("query") query: String
    ): Response<ApiResponse<List<RecipeDetail>>>

    @POST("recipes.php?action=mark_made")
    @Headers("Content-Type: application/json")
    suspend fun markRecipeAsMade(
        @Body request: MarkRecipeMadeRequest
    ): Response<ApiResponse<MarkRecipeMadeResult>>

    // Meal Planner API
    @GET("meal_planner.php")
    suspend fun getMealsForDate(
        @Query("user_id") userId: String,
        @Query("date") date: String  // Format: YYYY-MM-DD
    ): Response<MealPlanResponse>

    @GET("meal_planner.php")
    suspend fun getMealsForWeek(
        @Query("user_id") userId: String,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): Response<ApiResponse<Map<String, MealsForDate>>>

    @POST("meal_planner.php")
    @Headers("Content-Type: application/json")
    suspend fun addMealToPlan(
        @Body request: AddMealRequest
    ): Response<ApiResponse<AddMealResult>>

    // Pantry API
    @GET("pantry.php")
    suspend fun getUserPantry(
        @Query("user_id") userId: String
    ): Response<ApiResponse<List<PantryItem>>>

    @POST("pantry.php")
    @Headers("Content-Type: application/json")
    suspend fun addPantryItem(
        @Body request: AddPantryItemRequest
    ): Response<ApiResponse<String>>

    @PUT("pantry.php")
    @Headers("Content-Type: application/json")
    suspend fun updatePantryItem(
        @Body request: UpdatePantryItemRequest
    ): Response<ApiResponse<String>>

    @DELETE("pantry.php")
    suspend fun deletePantryItem(
        @Query("item_id") itemId: String
    ): Response<ApiResponse<String>>

    // Shopping List API
    @GET("shopping.php")
    suspend fun getShoppingList(
        @Query("user_id") userId: String
    ): Response<ApiResponse<List<ShoppingItem>>>

    @POST("shopping.php")
    @Headers("Content-Type: application/json")
    suspend fun addShoppingItem(
        @Body request: Map<String, String>
    ): Response<ApiResponse<String>>

    @DELETE("shopping.php")
    suspend fun deleteShoppingItem(
        @Query("item_id") itemId: String
    ): Response<ApiResponse<String>>

    // Ingredients API
    @GET("ingredients.php")
    suspend fun getAllIngredients(): Response<ApiResponse<List<com.example.smartmeal.data.repository.MasterIngredient>>>

    @GET("ingredients.php")
    suspend fun searchIngredients(
        @Query("search") query: String
    ): Response<ApiResponse<List<com.example.smartmeal.data.repository.MasterIngredient>>>
}

// Request/Response models
data class MarkRecipeMadeRequest(
    val user_id: String,
    val recipe_id: String,
    val servings_made: Int = 1
)

data class MarkRecipeMadeResult(
    val message: String,
    val ingredients_used: List<String>
)

data class AddMealRequest(
    val user_id: String,
    val recipe_id: String,
    val meal_date: String,  // YYYY-MM-DD
    val meal_type: String,  // breakfast, lunch, dinner
    val notes: String? = null
)

data class AddMealResult(
    val plan_id: String,
    val ingredients_info: IngredientsInfo
)

data class IngredientsInfo(
    val total_ingredients: Int,
    val already_have: Int,
    val need_to_buy: Int,
    val details: List<IngredientDetail>
)

data class IngredientDetail(
    val name: String,
    val quantity: String,
    val has_in_pantry: Boolean,
    val pantry_quantity: String?,
    val need_to_buy: Boolean
)

data class PantryItem(
    val id: Int,
    val item_id: String,
    val name: String,
    val quantity: String,
    val category: String?,
    val expiry_date: Long?,
    val image_url: String?
)

data class AddPantryItemRequest(
    val user_id: String,
    val name: String,
    val quantity: String,
    val category: String? = null,
    val expiry_date: Long? = null
)

data class UpdatePantryItemRequest(
    val item_id: String,
    val quantity: String
)

data class ShoppingItem(
    val id: Int,
    val item_id: String,
    val name: String,
    val quantity: String,
    val is_completed: Int,  // Backend returns 0 or 1
    val category: String?,
    val added_from_recipe_id: String?
) {
    val isCompleted: Boolean
        get() = is_completed == 1
}

