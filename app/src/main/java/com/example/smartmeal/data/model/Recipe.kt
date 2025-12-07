package com.example.smartmeal.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val description: String? = null,
    val imageUrl: String? = null,
    val prepTime: Int = 0, // in minutes
    val cookTime: Int = 0, // in minutes
    val servings: Int = 1,
    val difficulty: String = "Medium",
    val cuisine: String? = null,
    val dietType: String? = null,
    val ingredients: String, // JSON string of list
    val instructions: String,
    val isFavorite: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)

data class Ingredient(
    val name: String,
    val quantity: String,
    val unit: String? = null,
    val isAvailable: Boolean = false
)

