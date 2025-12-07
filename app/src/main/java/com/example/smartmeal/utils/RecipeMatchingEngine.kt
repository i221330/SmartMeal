package com.example.smartmeal.utils

import com.example.smartmeal.data.model.Ingredient
import com.example.smartmeal.data.model.PantryItem
import com.example.smartmeal.data.model.Recipe
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class RecipeMatch(
    val recipe: Recipe,
    val matchPercentage: Float,
    val availableIngredients: List<String>,
    val missingIngredients: List<String>,
    val canMake: Boolean
) {
    val missingCount: Int get() = missingIngredients.size
}

class RecipeMatchingEngine {

    private val gson = Gson()

    /**
     * Matches recipes with available pantry items
     * Returns a sorted list of recipes based on ingredient availability
     */
    fun matchRecipesWithPantry(
        recipes: List<Recipe>,
        pantryItems: List<PantryItem>
    ): List<RecipeMatch> {
        if (pantryItems.isEmpty()) {
            // Return all recipes with 0% match if pantry is empty
            return recipes.map { recipe ->
                val ingredients = parseIngredients(recipe.ingredients)
                RecipeMatch(
                    recipe = recipe,
                    matchPercentage = 0f,
                    availableIngredients = emptyList(),
                    missingIngredients = ingredients.map { it.name },
                    canMake = false
                )
            }
        }

        val pantryItemNames = pantryItems.map { it.name.lowercase().trim() }.toSet()

        val matches = recipes.mapNotNull { recipe ->
            val ingredients = parseIngredients(recipe.ingredients)
            if (ingredients.isEmpty()) return@mapNotNull null

            val available = mutableListOf<String>()
            val missing = mutableListOf<String>()

            ingredients.forEach { ingredient ->
                val ingredientName = ingredient.name.lowercase().trim()

                // Check for exact or partial match
                val isAvailable = pantryItemNames.any { pantryItem ->
                    pantryItem.contains(ingredientName) ||
                    ingredientName.contains(pantryItem) ||
                    // Check for common word matches
                    hasCommonKeyword(pantryItem, ingredientName)
                }

                if (isAvailable) {
                    available.add(ingredient.name)
                } else {
                    missing.add(ingredient.name)
                }
            }

            val matchPercentage = (available.size.toFloat() / ingredients.size.toFloat()) * 100
            val canMake = missing.isEmpty()

            RecipeMatch(
                recipe = recipe,
                matchPercentage = matchPercentage,
                availableIngredients = available,
                missingIngredients = missing,
                canMake = canMake
            )
        }

        // Sort by match percentage (descending) and then by missing count (ascending)
        return matches.sortedWith(
            compareByDescending<RecipeMatch> { it.matchPercentage }
                .thenBy { it.missingCount }
        )
    }

    /**
     * Get top N matching recipes
     */
    fun getTopMatches(matches: List<RecipeMatch>, count: Int = 3): List<RecipeMatch> {
        return matches.take(count)
    }

    /**
     * Get recipes that can be made with available ingredients (100% match)
     */
    fun getRecipesCanMake(matches: List<RecipeMatch>): List<RecipeMatch> {
        return matches.filter { it.canMake }
    }

    /**
     * Get recipes with high match (80% or more)
     */
    fun getHighMatchRecipes(matches: List<RecipeMatch>): List<RecipeMatch> {
        return matches.filter { it.matchPercentage >= 80f }
    }

    /**
     * Parse ingredients from JSON string
     */
    private fun parseIngredients(ingredientsJson: String): List<Ingredient> {
        return try {
            val type = object : TypeToken<List<Ingredient>>() {}.type
            gson.fromJson(ingredientsJson, type) ?: emptyList()
        } catch (e: Exception) {
            // Fallback: try to parse as simple comma-separated string
            try {
                ingredientsJson.split(",").map { item ->
                    Ingredient(name = item.trim(), quantity = "")
                }
            } catch (e2: Exception) {
                emptyList()
            }
        }
    }

    /**
     * Check if two ingredient names have common keywords
     * Helps match "chicken breast" with "chicken" or "tomato paste" with "tomatoes"
     */
    private fun hasCommonKeyword(text1: String, text2: String): Boolean {
        val keywords1 = text1.split(" ", "-", "_").filter { it.length > 3 }
        val keywords2 = text2.split(" ", "-", "_").filter { it.length > 3 }

        return keywords1.any { keyword1 ->
            keywords2.any { keyword2 ->
                keyword1.contains(keyword2) || keyword2.contains(keyword1)
            }
        }
    }

    /**
     * Calculate shopping list for missing ingredients
     */
    fun getMissingIngredientsForRecipe(
        recipe: Recipe,
        pantryItems: List<PantryItem>
    ): List<Ingredient> {
        val matches = matchRecipesWithPantry(listOf(recipe), pantryItems)
        if (matches.isEmpty()) return emptyList()

        val match = matches.first()
        val ingredients = parseIngredients(recipe.ingredients)

        return ingredients.filter { ingredient ->
            match.missingIngredients.contains(ingredient.name)
        }
    }
}

