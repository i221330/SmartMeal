package com.example.smartmeal.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meal_plans")
data class MealPlan(
    @PrimaryKey
    val id: String = java.util.UUID.randomUUID().toString(),
    val recipeId: String,
    val recipeName: String,
    val recipeImageUrl: String? = null,
    val date: Long, // timestamp for the date
    val mealType: String, // "breakfast", "lunch", "dinner"
    val notes: String? = null,
    val lastUpdated: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false
)

