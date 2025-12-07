package com.example.smartmeal

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.example.smartmeal.data.api.MealPlan
import com.example.smartmeal.data.api.RecipeSuggestion
import com.example.smartmeal.data.repository.HomeRepository
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ActivityHome : AppCompatActivity() {

    private val TAG = "ActivityHome"
    private val homeRepository = HomeRepository()
    private var userId: String = "1" // TODO: Get from auth session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        Log.d(TAG, "ActivityHome created")

        // Get user ID from auth (using hardcoded for now)
        // TODO: Get actual user ID from PhpAuthViewModel or SharedPreferences

        setupQuickActions()
        setupBottomNavigation()

        // Load data from backend
        loadTodaysMeals()
        loadRecipeSuggestions()
    }

    private fun setupQuickActions() {
        try {
            // Recipe List button
            findViewById<MaterialButton>(R.id.recipeListButton)?.setOnClickListener {
                Log.d(TAG, "Recipe List button clicked")
                try {
                    startActivity(Intent(this, ActivityRecipeList::class.java))
                } catch (e: Exception) {
                    Log.e(TAG, "Error navigating to Recipe List", e)
                    Toast.makeText(this, "Recipe List coming soon!", Toast.LENGTH_SHORT).show()
                }
            }

            // Learn More button
            findViewById<MaterialButton>(R.id.learnMoreButton)?.setOnClickListener {
                Log.d(TAG, "Learn More button clicked")
                try {
                    startActivity(Intent(this, ActivityLearnMore::class.java))
                } catch (e: Exception) {
                    Log.e(TAG, "Error navigating to Learn More", e)
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            // AI Assistant button
            findViewById<MaterialButton>(R.id.aiAssistantButton)?.setOnClickListener {
                Log.d(TAG, "AI Assistant button clicked")
                try {
                    startActivity(Intent(this, ActivityAiAssistant::class.java))
                } catch (e: Exception) {
                    Log.e(TAG, "Error navigating to AI Assistant", e)
                    Toast.makeText(this, "AI Assistant coming soon!", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in setupQuickActions", e)
        }
    }

    private fun setupBottomNavigation() {
        try {
            // Home (current screen - do nothing)
            findViewById<View>(R.id.navHome)?.setOnClickListener {
                Log.d(TAG, "Home nav clicked (already on home)")
            }

            // Pantry
            findViewById<View>(R.id.navPantry)?.setOnClickListener {
                Log.d(TAG, "Pantry nav clicked")
                try {
                    startActivity(Intent(this, ActivityPantry::class.java))
                } catch (e: Exception) {
                    Log.e(TAG, "Error navigating to Pantry", e)
                    Toast.makeText(this, "Pantry coming soon!", Toast.LENGTH_SHORT).show()
                }
            }

            // Meal Planner
            findViewById<View>(R.id.navPlanner)?.setOnClickListener {
                Log.d(TAG, "Planner nav clicked")
                try {
                    startActivity(Intent(this, ActivityMealPlanner::class.java))
                } catch (e: Exception) {
                    Log.e(TAG, "Error navigating to Meal Planner", e)
                    Toast.makeText(this, "Meal Planner coming soon!", Toast.LENGTH_SHORT).show()
                }
            }

            // Shopping List
            findViewById<View>(R.id.navShopping)?.setOnClickListener {
                Log.d(TAG, "Shopping nav clicked")
                try {
                    startActivity(Intent(this, ActivityShoppingList::class.java))
                } catch (e: Exception) {
                    Log.e(TAG, "Error navigating to Shopping List", e)
                    Toast.makeText(this, "Shopping List coming soon!", Toast.LENGTH_SHORT).show()
                }
            }

            // Profile
            findViewById<View>(R.id.navProfile)?.setOnClickListener {
                Log.d(TAG, "Profile nav clicked")
                try {
                    startActivity(Intent(this, ActivityProfile::class.java))
                } catch (e: Exception) {
                    Log.e(TAG, "Error navigating to Profile", e)
                    Toast.makeText(this, "Profile coming soon!", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in setupBottomNavigation", e)
        }
    }

    private fun loadTodaysMeals() {
        lifecycleScope.launch {
            try {
                Log.d(TAG, "Loading today's meals...")

                // Get today's date in YYYY-MM-DD format
                val today = SimpleDateFormat("yyyy-MM-DD", Locale.getDefault()).format(Date())

                val result = homeRepository.getTodaysMeals(userId, today)

                result.onSuccess { response ->
                    Log.d(TAG, "Today's meals loaded: ${response.meals_count} meals")
                    updateTodaysMealsUI(response.meals.breakfast, response.meals.lunch, response.meals.dinner)
                }

                result.onFailure { error ->
                    Log.e(TAG, "Failed to load today's meals", error)
                    showEmptyMealsUI()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception loading today's meals", e)
                showEmptyMealsUI()
            }
        }
    }

    private fun updateTodaysMealsUI(breakfast: MealPlan?, lunch: MealPlan?, dinner: MealPlan?) {
        // Update breakfast
        findViewById<TextView>(R.id.breakfastTitle)?.text =
            breakfast?.recipe_name ?: "No meal planned"

        // Update lunch
        findViewById<TextView>(R.id.lunchTitle)?.text =
            lunch?.recipe_name ?: "No meal planned"

        // Update dinner
        findViewById<TextView>(R.id.dinnerTitle)?.text =
            dinner?.recipe_name ?: "No meal planned"

        Log.d(TAG, "UI updated - Breakfast: ${breakfast?.recipe_name}, Lunch: ${lunch?.recipe_name}, Dinner: ${dinner?.recipe_name}")
    }

    private fun showEmptyMealsUI() {
        findViewById<TextView>(R.id.breakfastTitle)?.text = "Add a meal"
        findViewById<TextView>(R.id.lunchTitle)?.text = "Add a meal"
        findViewById<TextView>(R.id.dinnerTitle)?.text = "Add a meal"
    }

    private fun loadRecipeSuggestions() {
        lifecycleScope.launch {
            try {
                Log.d(TAG, "Loading recipe suggestions...")

                val result = homeRepository.getRecipeSuggestions(userId)

                result.onSuccess { response ->
                    Log.d(TAG, "Recipe suggestions loaded: ${response.suggestions_count} recipes")
                    Log.d(TAG, "User has ${response.pantry_items_count} pantry items")

                    if (response.data.isNotEmpty()) {
                        updateRecipeSuggestionsUI(response.data)
                    } else {
                        showEmptySuggestionsUI()
                    }
                }

                result.onFailure { error ->
                    Log.e(TAG, "Failed to load recipe suggestions", error)
                    showEmptySuggestionsUI()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception loading recipe suggestions", e)
                showEmptySuggestionsUI()
            }
        }
    }

    private fun updateRecipeSuggestionsUI(suggestions: List<RecipeSuggestion>) {
        // Update all 5 suggestion cards (backend returns 5)
        for (i in 0 until minOf(5, suggestions.size)) {
            updateSuggestionCard(i + 1, suggestions[i])
        }

        Log.d(TAG, "Updated ${minOf(5, suggestions.size)} suggestion cards")
    }

    private fun updateSuggestionCard(cardNumber: Int, suggestion: RecipeSuggestion) {
        val titleId = when(cardNumber) {
            1 -> R.id.suggestedRecipeTitle1
            2 -> R.id.suggestedRecipeTitle2
            3 -> R.id.suggestedRecipeTitle3
            4 -> R.id.suggestedRecipeTitle4
            5 -> R.id.suggestedRecipeTitle5
            else -> return
        }

        val statusId = when(cardNumber) {
            1 -> R.id.suggestedRecipeStatus1
            2 -> R.id.suggestedRecipeStatus2
            3 -> R.id.suggestedRecipeStatus3
            4 -> R.id.suggestedRecipeStatus4
            5 -> R.id.suggestedRecipeStatus5
            else -> return
        }

        val cardId = when(cardNumber) {
            1 -> R.id.suggestedRecipe1
            2 -> R.id.suggestedRecipe2
            3 -> R.id.suggestedRecipe3
            4 -> R.id.suggestedRecipe4
            5 -> R.id.suggestedRecipe5
            else -> return
        }

        // Update title
        findViewById<TextView>(titleId)?.text = suggestion.title

        // Update status with ingredient count
        findViewById<TextView>(statusId)?.apply {
            if (suggestion.can_make_now) {
                text = "You have all ${suggestion.total_ingredients} ingredients!"
                setTextColor(getColor(R.color.primary))
            } else if (suggestion.matched_ingredients > 0) {
                text = "Need ${suggestion.missing_ingredients_count} more (${suggestion.total_ingredients} total)"
                setTextColor(getColor(android.R.color.holo_orange_dark))
            } else {
                // Empty pantry - show total ingredients needed
                text = "Needs ${suggestion.total_ingredients} ingredients"
                setTextColor(getColor(android.R.color.holo_red_light))
            }
        }

        // Set click listener to view recipe details
        findViewById<CardView>(cardId)?.setOnClickListener {
            Log.d(TAG, "Recipe card clicked: ${suggestion.title}")
            Toast.makeText(this, "Recipe details coming soon!", Toast.LENGTH_SHORT).show()
            // TODO: Navigate to recipe details with recipe_id
        }
    }

    private fun showEmptySuggestionsUI() {
        findViewById<TextView>(R.id.suggestedRecipeTitle1)?.text = "No suggestions"
        findViewById<TextView>(R.id.suggestedRecipeTitle2)?.text = "Add items to pantry"
        findViewById<TextView>(R.id.suggestedRecipeTitle3)?.text = "to see recipes"
    }
}

