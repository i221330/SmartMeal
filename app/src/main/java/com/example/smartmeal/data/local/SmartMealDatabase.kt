package com.example.smartmeal.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.smartmeal.data.model.*

@Database(
    entities = [
        User::class,
        PantryItem::class,
        ShoppingItem::class,
        Recipe::class,
        MealPlan::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SmartMealDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun pantryDao(): PantryDao
    abstract fun shoppingDao(): ShoppingDao
    abstract fun recipeDao(): RecipeDao
    abstract fun mealPlanDao(): MealPlanDao

    companion object {
        @Volatile
        private var INSTANCE: SmartMealDatabase? = null

        fun getDatabase(context: Context): SmartMealDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SmartMealDatabase::class.java,
                    "smartmeal_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

