package com.example.smartmeal.network

import com.example.smartmeal.network.models.*
import retrofit2.Response
import retrofit2.http.*

interface UserApiService {
    @POST("users.php?action=register")
    suspend fun registerUser(@Body request: UserRegisterRequest): Response<UserRegisterResponse>

    @POST("users.php?action=login")
    suspend fun loginUser(@Body request: UserLoginRequest): Response<UserLoginResponse>

    @GET("users.php?action=profile")
    suspend fun getUserProfile(@Query("firebase_uid") firebaseUid: String): Response<UserData>

    @PUT("users.php?action=profile")
    suspend fun updateUserProfile(@Body request: UserProfileUpdateRequest): Response<UserUpdateResponse>
}

interface RecipeApiService {
    @GET("recipes.php?action=all")
    suspend fun getAllRecipes(): Response<RecipesResponse>

    @GET("recipes.php?action=global")
    suspend fun getGlobalRecipes(): Response<RecipesResponse>

    @GET("recipes.php?action=search")
    suspend fun searchRecipes(@Query("query") query: String): Response<RecipesResponse>

    @GET("recipes.php?action=get")
    suspend fun getRecipeById(@Query("recipe_id") recipeId: String): Response<RecipeResponse>

    @POST("recipes.php?action=create")
    suspend fun createRecipe(@Body request: CreateRecipeRequest): Response<ApiResponse>

    @POST("recipes.php?action=favorite")
    suspend fun toggleFavorite(@Body request: FavoriteRequest): Response<ApiResponse>

    @GET("recipes.php?action=favorites")
    suspend fun getUserFavorites(@Query("firebase_uid") firebaseUid: String): Response<RecipesResponse>
}

interface PantryApiService {
    @GET("pantry.php?action=all")
    suspend fun getAllPantryItems(@Query("firebase_uid") firebaseUid: String): Response<PantryItemsResponse>

    @POST("pantry.php?action=add")
    suspend fun addPantryItem(@Body request: PantryItemRequest): Response<ApiResponse>

    @PUT("pantry.php?action=update")
    suspend fun updatePantryItem(@Body request: PantryItemRequest): Response<ApiResponse>

    @DELETE("pantry.php?action=delete")
    suspend fun deletePantryItem(@Query("item_id") itemId: String): Response<ApiResponse>

    @POST("pantry.php?action=sync")
    suspend fun syncPantryItems(@Body request: SyncPantryRequest): Response<SyncResponse>
}

interface ShoppingApiService {
    @GET("shopping.php?action=all")
    suspend fun getAllShoppingItems(@Query("firebase_uid") firebaseUid: String): Response<ShoppingItemsResponse>

    @POST("shopping.php?action=add")
    suspend fun addShoppingItem(@Body request: ShoppingItemRequest): Response<ApiResponse>

    @PUT("shopping.php?action=update")
    suspend fun updateShoppingItem(@Body request: ShoppingItemRequest): Response<ApiResponse>

    @PUT("shopping.php?action=complete")
    suspend fun updateCompletion(@Body request: UpdateCompletionRequest): Response<ApiResponse>

    @DELETE("shopping.php?action=delete")
    suspend fun deleteShoppingItem(@Query("item_id") itemId: String): Response<ApiResponse>

    @DELETE("shopping.php?action=clear_completed")
    suspend fun clearCompleted(@Query("firebase_uid") firebaseUid: String): Response<ApiResponse>

    @POST("shopping.php?action=sync")
    suspend fun syncShoppingItems(@Body request: SyncShoppingRequest): Response<SyncResponse>
}

interface MealPlanApiService {
    @GET("mealplans.php?action=all")
    suspend fun getAllMealPlans(@Query("firebase_uid") firebaseUid: String): Response<MealPlansResponse>

    @GET("mealplans.php?action=week")
    suspend fun getWeekMealPlans(
        @Query("firebase_uid") firebaseUid: String,
        @Query("start_date") startDate: Long,
        @Query("end_date") endDate: Long
    ): Response<MealPlansResponse>

    @POST("mealplans.php?action=add")
    suspend fun addMealPlan(@Body request: MealPlanRequest): Response<ApiResponse>

    @PUT("mealplans.php?action=update")
    suspend fun updateMealPlan(@Body request: MealPlanRequest): Response<ApiResponse>

    @DELETE("mealplans.php?action=delete")
    suspend fun deleteMealPlan(@Query("plan_id") planId: String): Response<ApiResponse>

    @POST("mealplans.php?action=sync")
    suspend fun syncMealPlans(@Body request: SyncMealPlansRequest): Response<SyncResponse>
}

