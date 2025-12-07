package com.example.smartmeal.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.smartmeal.data.model.Recipe

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipes ORDER BY title ASC")
    fun getAllRecipes(): LiveData<List<Recipe>>

    @Query("SELECT * FROM recipes ORDER BY title ASC")
    suspend fun getAllRecipesList(): List<Recipe>

    @Query("SELECT * FROM recipes WHERE id = :id")
    suspend fun getRecipeById(id: String): Recipe?

    @Query("SELECT * FROM recipes WHERE isFavorite = 1 ORDER BY title ASC")
    fun getFavoriteRecipes(): LiveData<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    suspend fun searchRecipes(query: String): List<Recipe>

    @Query("SELECT * FROM recipes WHERE isSynced = 0")
    suspend fun getUnsyncedRecipes(): List<Recipe>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: Recipe)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(recipes: List<Recipe>)

    @Update
    suspend fun updateRecipe(recipe: Recipe)

    @Query("UPDATE recipes SET isFavorite = :isFavorite, lastUpdated = :timestamp, isSynced = 0 WHERE id = :id")
    suspend fun updateFavoriteStatus(id: String, isFavorite: Boolean, timestamp: Long = System.currentTimeMillis())

    @Delete
    suspend fun deleteRecipe(recipe: Recipe)

    @Query("DELETE FROM recipes")
    suspend fun deleteAll()

    @Query("UPDATE recipes SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: String)
}

