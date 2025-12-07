package com.example.smartmeal.network.models

import com.google.gson.annotations.SerializedName

// Base Response
data class ApiResponse(
    val message: String,
    val success: Boolean = true
)

// User Related
data class UserRegisterRequest(
    @SerializedName("firebase_uid") val firebaseUid: String = "",
    val email: String,
    val password: String,
    @SerializedName("phone_number") val phoneNumber: String? = null,
    @SerializedName("display_name") val displayName: String? = null,
    @SerializedName("profile_image_url") val profileImageUrl: String? = null
)

data class UserLoginRequest(
    val email: String,
    val password: String,
    @SerializedName("firebase_uid") val firebaseUid: String = ""
)

data class UserRegisterResponse(
    val message: String,
    @SerializedName("user_id") val user_id: String? = null,
    val email: String? = null,
    @SerializedName("display_name") val display_name: String? = null
)

data class UserLoginResponse(
    val message: String,
    val user: UserData? = null
)

data class UserData(
    val id: Int? = null,
    @SerializedName("user_id") val user_id: String? = null,
    @SerializedName("firebase_uid") val firebaseUid: String? = null,
    val email: String? = null,
    @SerializedName("phone_number") val phone_number: String? = null,
    @SerializedName("display_name") val display_name: String? = null,
    @SerializedName("profile_image_url") val profile_image_url: String? = null,
    @SerializedName("is_premium") val is_premium: Boolean? = null,
    @SerializedName("joined_date") val joined_date: String? = null
)

// Recipe Related
data class RecipeData(
    val id: Int,
    @SerializedName("recipe_id") val recipeId: String,
    val title: String,
    val description: String?,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("prep_time") val prepTime: Int,
    @SerializedName("cook_time") val cookTime: Int,
    val servings: Int,
    val difficulty: String,
    val cuisine: String?,
    @SerializedName("diet_type") val dietType: String?,
    val ingredients: String,
    val instructions: String,
    @SerializedName("is_favorite") val isFavorite: Boolean = false
)

data class RecipesResponse(
    val recipes: List<RecipeData>
)

data class RecipeResponse(
    val recipe: RecipeData
)

data class CreateRecipeRequest(
    @SerializedName("firebase_uid") val firebaseUid: String,
    val title: String,
    val description: String?,
    val ingredients: String,
    val instructions: String,
    @SerializedName("prep_time") val prepTime: Int = 0,
    @SerializedName("cook_time") val cookTime: Int = 0,
    val servings: Int = 1,
    val difficulty: String = "Medium",
    val cuisine: String? = null,
    @SerializedName("diet_type") val dietType: String? = null
)

data class FavoriteRequest(
    @SerializedName("firebase_uid") val firebaseUid: String,
    @SerializedName("recipe_id") val recipeId: String,
    @SerializedName("is_favorite") val isFavorite: Boolean
)

// Pantry Related
data class PantryItemData(
    @SerializedName("item_id") val itemId: String,
    val name: String,
    val quantity: String,
    @SerializedName("image_url") val imageUrl: String?,
    val category: String?,
    @SerializedName("expiry_date") val expiryDate: Long?,
    @SerializedName("last_updated") val lastUpdated: Long
)

data class PantryItemsResponse(
    val items: List<PantryItemData>
)

data class PantryItemRequest(
    @SerializedName("firebase_uid") val firebaseUid: String,
    @SerializedName("item_id") val itemId: String,
    val name: String,
    val quantity: String,
    @SerializedName("image_url") val imageUrl: String? = null,
    val category: String? = null,
    @SerializedName("expiry_date") val expiryDate: Long? = null
)

data class SyncPantryRequest(
    @SerializedName("firebase_uid") val firebaseUid: String,
    val items: List<PantryItemData>
)

// Shopping Related
data class ShoppingItemData(
    @SerializedName("item_id") val itemId: String,
    val name: String,
    val quantity: String,
    @SerializedName("is_completed") val isCompletedInt: Int,  // Backend returns 0 or 1
    val category: String?,
    @SerializedName("last_updated") val lastUpdated: Long
) {
    val isCompleted: Boolean
        get() = isCompletedInt == 1
}

data class ShoppingItemsResponse(
    val items: List<ShoppingItemData>
)

data class ShoppingItemRequest(
    @SerializedName("firebase_uid") val firebaseUid: String,
    @SerializedName("item_id") val itemId: String,
    val name: String,
    val quantity: String,
    @SerializedName("is_completed") val isCompleted: Boolean = false,
    val category: String? = null
)

data class UpdateCompletionRequest(
    @SerializedName("item_id") val itemId: String,
    @SerializedName("is_completed") val isCompleted: Boolean
)

data class SyncShoppingRequest(
    @SerializedName("firebase_uid") val firebaseUid: String,
    val items: List<ShoppingItemData>
)

// Meal Plan Related
data class MealPlanData(
    @SerializedName("plan_id") val planId: String,
    @SerializedName("recipe_id") val recipeId: String,
    @SerializedName("recipe_name") val recipeName: String,
    @SerializedName("recipe_image_url") val recipeImageUrl: String?,
    @SerializedName("meal_date") val mealDate: Long,
    @SerializedName("meal_type") val mealType: String,
    val notes: String?,
    @SerializedName("last_updated") val lastUpdated: Long
)

data class MealPlansResponse(
    val plans: List<MealPlanData>
)

data class MealPlanRequest(
    @SerializedName("firebase_uid") val firebaseUid: String,
    @SerializedName("plan_id") val planId: String,
    @SerializedName("recipe_id") val recipeId: String,
    @SerializedName("recipe_name") val recipeName: String,
    @SerializedName("recipe_image_url") val recipeImageUrl: String? = null,
    @SerializedName("meal_date") val mealDate: Long,
    @SerializedName("meal_type") val mealType: String,
    val notes: String? = null
)

data class SyncMealPlansRequest(
    @SerializedName("firebase_uid") val firebaseUid: String,
    val plans: List<MealPlanData>
)

// Sync Response
data class SyncResponse(
    val message: String,
    @SerializedName("synced_count") val syncedCount: Int
)

