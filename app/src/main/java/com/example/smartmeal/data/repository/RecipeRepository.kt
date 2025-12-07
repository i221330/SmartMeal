package com.example.smartmeal.data.repository

import androidx.lifecycle.LiveData
import com.example.smartmeal.data.local.RecipeDao
import com.example.smartmeal.data.model.Recipe
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class RecipeRepository(
    private val recipeDao: RecipeDao,
    private val firestore: FirebaseFirestore
) {

    val allRecipes: LiveData<List<Recipe>> = recipeDao.getAllRecipes()
    val favoriteRecipes: LiveData<List<Recipe>> = recipeDao.getFavoriteRecipes()

    suspend fun getAllRecipesList(): List<Recipe> {
        return recipeDao.getAllRecipesList()
    }

    suspend fun getRecipeById(id: String): Recipe? {
        return recipeDao.getRecipeById(id)
    }

    suspend fun searchRecipes(query: String): List<Recipe> {
        return recipeDao.searchRecipes(query)
    }

    suspend fun insertRecipe(recipe: Recipe) {
        recipeDao.insertRecipe(recipe.copy(isSynced = false))
    }

    suspend fun insertAll(recipes: List<Recipe>) {
        recipeDao.insertAll(recipes)
    }

    suspend fun updateRecipe(recipe: Recipe) {
        recipeDao.updateRecipe(recipe.copy(
            lastUpdated = System.currentTimeMillis(),
            isSynced = false
        ))
    }

    suspend fun updateFavoriteStatus(id: String, isFavorite: Boolean) {
        recipeDao.updateFavoriteStatus(id, isFavorite)
    }

    suspend fun deleteRecipe(recipe: Recipe) {
        recipeDao.deleteRecipe(recipe)
    }

    suspend fun syncToCloud(userId: String) {
        val unsyncedRecipes = recipeDao.getUnsyncedRecipes()

        unsyncedRecipes.forEach { recipe ->
            try {
                firestore.collection("users")
                    .document(userId)
                    .collection("recipes")
                    .document(recipe.id)
                    .set(recipe)
                    .await()

                recipeDao.markAsSynced(recipe.id)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun syncFromCloud(userId: String) {
        try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("recipes")
                .get()
                .await()

            val cloudRecipes = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Recipe::class.java)?.copy(isSynced = true)
            }

            recipeDao.insertAll(cloudRecipes)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun loadGlobalRecipes() {
        try {
            // Load recipes from a global collection
            val snapshot = firestore.collection("global_recipes")
                .limit(50)
                .get()
                .await()

            val recipes = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Recipe::class.java)?.copy(isSynced = true)
            }

            if (recipes.isNotEmpty()) {
                recipeDao.insertAll(recipes)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

