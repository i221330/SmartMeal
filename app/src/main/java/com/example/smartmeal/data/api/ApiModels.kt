package com.example.smartmeal.data.api

/**
 * API Response Models for SmartMeal Backend
 */

// Recipe Suggestion Response
data class RecipeSuggestionResponse(
    val success: Boolean,
    val user_id: String?,
    val pantry_items_count: Int,
    val suggestions_count: Int,
    val data: List<RecipeSuggestion>
)

data class RecipeSuggestion(
    val recipe_id: String,
    val title: String,
    val description: String,
    val image_url: String?,
    val prep_time: Int,
    val cook_time: Int,
    val servings: Int,
    val difficulty: String,
    val cuisine: String,
    val total_ingredients: Int,
    val matched_ingredients: Int,
    val missing_ingredients_count: Int,
    val missing_ingredients: List<MissingIngredient>,
    val match_percentage: Int,
    val can_make_now: Boolean
)

data class MissingIngredient(
    val name: String,
    val quantity: String
)

// Meal Plan Response
data class MealPlanResponse(
    val success: Boolean,
    val user_id: String?,
    val date: String,
    val meals_count: Int,
    val meals: MealsForDate,
    val all_meals: List<MealPlan>?
)

data class MealsForDate(
    val breakfast: MealPlan?,
    val lunch: MealPlan?,
    val dinner: MealPlan?
)

data class MealPlan(
    val id: Int?,
    val user_id: Int,
    val plan_id: String,
    val recipe_id: String,
    val recipe_name: String,
    val recipe_image_url: String?,
    val meal_date: Long,
    val meal_type: String,
    val notes: String?,
    val last_updated: Long,
    val is_synced: Int,  // Backend returns 0 or 1
    val is_deleted: Int  // Backend returns 0 or 1
) {
    val isSynced: Boolean
        get() = is_synced == 1
    val isDeleted: Boolean
        get() = is_deleted == 1
}

// Generic API Response
data class ApiResponse<T>(
    val success: Boolean,
    val message: String?,
    val data: T?
)

// Recipe Detail Response
data class RecipeDetailResponse(
    val success: Boolean,
    val data: RecipeDetail?
)

data class RecipeDetail(
    val id: Int?,
    val recipe_id: String,
    val title: String,
    val description: String?,
    val image_url: String?,
    val prep_time: Int,
    val cook_time: Int,
    val servings: Int,
    val difficulty: String,
    val cuisine: String?,
    val diet_type: String?,
    val ingredients: List<RecipeIngredient>,
    val instructions: String,
    val is_global: Int  // Backend returns 0 or 1, not boolean
) {
    val isGlobal: Boolean
        get() = is_global == 1
}

data class RecipeIngredient(
    val name: String,
    val quantity: String
)

