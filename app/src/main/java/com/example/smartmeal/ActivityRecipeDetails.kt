package com.example.smartmeal

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.smartmeal.data.api.RecipeDetail
import com.example.smartmeal.data.api.RecipeIngredient
import com.example.smartmeal.data.api.PantryItem
import com.example.smartmeal.data.api.RetrofitClient
import com.example.smartmeal.data.repository.PantryRepository
import com.example.smartmeal.data.repository.ShoppingListRepository
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class ActivityRecipeDetails : AppCompatActivity() {

    private val TAG = "ActivityRecipeDetails"
    private var recipeId: String? = null
    private var currentRecipe: RecipeDetail? = null
    private var userPantryItems: List<PantryItem> = emptyList()

    // Repositories
    private val pantryRepository = PantryRepository()
    private val shoppingListRepository = ShoppingListRepository(RetrofitClient.api)
    private val userId = "1" // TODO: Get from auth session

    // UI Components
    private lateinit var recipeImage: ImageView
    private lateinit var recipeTitle: TextView
    private lateinit var recipeDescription: TextView
    private lateinit var prepTimeText: TextView
    private lateinit var cookTimeText: TextView
    private lateinit var servingsText: TextView
    private lateinit var difficultyText: TextView
    private lateinit var ingredientsContainer: LinearLayout
    private lateinit var instructionsTextView: TextView
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var errorTextView: TextView
    private lateinit var addToShoppingListButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_details)

        // Get recipe ID from intent
        recipeId = intent.getStringExtra("RECIPE_ID")
        val recipeName = intent.getStringExtra("RECIPE_NAME")

        Log.d(TAG, "Recipe ID: $recipeId, Recipe Name: $recipeName")

        if (recipeId == null) {
            Toast.makeText(this, "Error: No recipe ID provided", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        initializeViews()
        setupBackButton()
        setupBottomNavigation()
        loadRecipeDetails()
    }

    private fun initializeViews() {
        recipeImage = findViewById(R.id.recipeImage)
        recipeTitle = findViewById(R.id.recipeTitle)
        recipeDescription = findViewById(R.id.recipeDescription)
        prepTimeText = findViewById(R.id.prepTimeText)
        cookTimeText = findViewById(R.id.cookTimeText)
        servingsText = findViewById(R.id.servingsText)
        difficultyText = findViewById(R.id.difficultyText)
        ingredientsContainer = findViewById(R.id.ingredientsContainer)
        instructionsTextView = findViewById(R.id.instructionsTextView)
        loadingProgressBar = findViewById(R.id.loadingProgressBar)
        errorTextView = findViewById(R.id.errorTextView)
        addToShoppingListButton = findViewById(R.id.addToShoppingListButton)

        // Setup button click listener
        addToShoppingListButton.setOnClickListener {
            addMissingIngredientsToShoppingList()
        }
    }

    private fun setupBackButton() {
        findViewById<View>(R.id.backButton)?.setOnClickListener {
            finish()
        }
    }

    private fun setupBottomNavigation() {
        try {
            // Home
            findViewById<View>(R.id.homeNav)?.setOnClickListener {
                Log.d(TAG, "Home nav clicked")
                startActivity(Intent(this, ActivityHome::class.java))
                finish() // Close recipe details
            }

            // Pantry
            findViewById<View>(R.id.pantryNav)?.setOnClickListener {
                Log.d(TAG, "Pantry nav clicked")
                startActivity(Intent(this, ActivityPantry::class.java))
                finish()
            }

            // Meal Planner
            findViewById<View>(R.id.plannerNav)?.setOnClickListener {
                Log.d(TAG, "Planner nav clicked")
                startActivity(Intent(this, ActivityMealPlanner::class.java))
                finish()
            }

            // Shopping List
            findViewById<View>(R.id.shoppingNav)?.setOnClickListener {
                Log.d(TAG, "Shopping nav clicked")
                startActivity(Intent(this, ActivityShoppingList::class.java))
                finish()
            }

            // Profile
            findViewById<View>(R.id.profileNav)?.setOnClickListener {
                Log.d(TAG, "Profile nav clicked")
                startActivity(Intent(this, ActivityProfile::class.java))
                finish()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up bottom navigation", e)
        }
    }


    private fun loadRecipeDetails() {
        lifecycleScope.launch {
            try {
                showLoading(true)

                Log.d(TAG, "Fetching recipe details for: $recipeId")

                // Load recipe and pantry in parallel
                val recipeResponse = RetrofitClient.api.getRecipeDetail(recipeId = recipeId!!)
                val pantryResult = pantryRepository.getUserPantry(userId)

                // Handle pantry result
                pantryResult.onSuccess { pantryItems ->
                    Log.d(TAG, "Loaded ${pantryItems.size} pantry items")
                    userPantryItems = pantryItems
                }.onFailure { error ->
                    Log.e(TAG, "Failed to load pantry, continuing without it", error)
                    userPantryItems = emptyList()
                }

                if (recipeResponse.isSuccessful && recipeResponse.body() != null) {
                    val apiResponse = recipeResponse.body()!!

                    if (apiResponse.success && apiResponse.data != null) {
                        Log.d(TAG, "Recipe loaded successfully: ${apiResponse.data.title}")
                        currentRecipe = apiResponse.data
                        displayRecipeDetails(apiResponse.data)
                        showLoading(false)
                    } else {
                        Log.e(TAG, "API returned success=false or null data")
                        showError("Recipe not found")
                    }
                } else {
                    Log.e(TAG, "API call failed: ${recipeResponse.code()} - ${recipeResponse.message()}")
                    showError("Failed to load recipe: ${recipeResponse.message()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception loading recipe details", e)
                showError("Error: ${e.message}")
            }
        }
    }

    private fun displayRecipeDetails(recipe: RecipeDetail) {
        // Set title and description
        recipeTitle.text = recipe.title
        recipeDescription.text = recipe.description ?: "No description available"
        recipeDescription.visibility = if (recipe.description.isNullOrEmpty()) View.GONE else View.VISIBLE

        // Set cooking info
        prepTimeText.text = "${recipe.prep_time} min"
        cookTimeText.text = "${recipe.cook_time} min"
        servingsText.text = recipe.servings.toString()
        difficultyText.text = recipe.difficulty

        // Load recipe image
        if (!recipe.image_url.isNullOrEmpty()) {
            Glide.with(this)
                .load(recipe.image_url)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .into(recipeImage)
        } else {
            recipeImage.setImageResource(android.R.drawable.ic_menu_gallery)
        }

        // Display ingredients
        displayIngredients(recipe.ingredients)

        // Display instructions
        instructionsTextView.text = formatInstructions(recipe.instructions)
    }

    private fun displayIngredients(ingredients: List<RecipeIngredient>) {
        ingredientsContainer.removeAllViews()

        if (ingredients.isEmpty()) {
            val noIngredientsText = TextView(this).apply {
                text = "No ingredients listed"
                textSize = 14f
                setTextColor(getColor(android.R.color.darker_gray))
            }
            ingredientsContainer.addView(noIngredientsText)
            return
        }

        ingredients.forEach { ingredient ->
            val hasInPantry = checkIfInPantry(ingredient.name)
            val ingredientCard = createIngredientCard(ingredient, hasInPantry)
            ingredientsContainer.addView(ingredientCard)
        }
    }

    private fun checkIfInPantry(ingredientName: String): Boolean {
        val normalizedName = ingredientName.trim().lowercase()
        return userPantryItems.any { pantryItem ->
            val pantryName = pantryItem.name.trim().lowercase()
            // Check for exact match or if one contains the other
            pantryName == normalizedName ||
            pantryName.contains(normalizedName) ||
            normalizedName.contains(pantryName)
        }
    }

    private fun createIngredientCard(ingredient: RecipeIngredient, hasInPantry: Boolean): View {
        val cardView = CardView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = (8 * resources.displayMetrics.density).toInt()
            }
            radius = 8 * resources.displayMetrics.density
            // Different background color based on pantry status
            setCardBackgroundColor(getColor(
                if (hasInPantry) R.color.success_light else R.color.subtle_light
            ))
            cardElevation = 0f
        }

        val linearLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            val padding = (12 * resources.displayMetrics.density).toInt()
            setPadding(padding, padding, padding, padding)
            gravity = android.view.Gravity.CENTER_VERTICAL
        }

        val checkBox = CheckBox(this).apply {
            val size = (20 * resources.displayMetrics.density).toInt()
            layoutParams = LinearLayout.LayoutParams(size, size)
            isChecked = hasInPantry
            isEnabled = false // Make it non-interactive, just visual indicator
            buttonTintList = getColorStateList(
                if (hasInPantry) R.color.success else R.color.primary
            )
        }

        val textLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            ).apply {
                marginStart = (12 * resources.displayMetrics.density).toInt()
            }
        }

        val ingredientTextView = TextView(this).apply {
            text = "${ingredient.quantity} ${ingredient.name}"
            textSize = 16f
            setTextColor(getColor(R.color.text_light))
        }

        textLayout.addView(ingredientTextView)

        // Add status indicator if in pantry
        if (hasInPantry) {
            val statusTextView = TextView(this).apply {
                text = "✓ In your pantry"
                textSize = 12f
                setTextColor(getColor(R.color.success))
                val topMargin = (2 * resources.displayMetrics.density).toInt()
                setPadding(0, topMargin, 0, 0)
            }
            textLayout.addView(statusTextView)
        }

        linearLayout.addView(checkBox)
        linearLayout.addView(textLayout)
        cardView.addView(linearLayout)

        return cardView
    }

    private fun formatInstructions(instructions: String): String {
        // Check if instructions are already formatted with numbers
        return if (instructions.contains(Regex("^\\d+\\.", RegexOption.MULTILINE))) {
            // Already formatted, just ensure proper spacing
            instructions.replace(Regex("(\\d+\\.)"), "\n$1")
                .trim()
        } else {
            // Split by newlines or periods and number them
            val steps = instructions.split(Regex("[.\\n]"))
                .map { it.trim() }
                .filter { it.isNotEmpty() }

            steps.mapIndexed { index, step ->
                "${index + 1}. $step"
            }.joinToString("\n\n")
        }
    }

    private fun addMissingIngredientsToShoppingList() {
        val recipe = currentRecipe
        if (recipe == null) {
            Toast.makeText(this, "Recipe not loaded", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                // Disable button during operation
                addToShoppingListButton.isEnabled = false
                addToShoppingListButton.text = "Adding..."

                Log.d(TAG, "Adding missing ingredients to shopping list")

                // Filter out ingredients that are already in pantry
                val missingIngredients = recipe.ingredients.filter { ingredient ->
                    !checkIfInPantry(ingredient.name)
                }

                if (missingIngredients.isEmpty()) {
                    Toast.makeText(
                        this@ActivityRecipeDetails,
                        "You already have all ingredients!",
                        Toast.LENGTH_LONG
                    ).show()
                    addToShoppingListButton.isEnabled = true
                    addToShoppingListButton.text = "Add All Ingredients to Shopping List"
                    return@launch
                }

                // Convert to format expected by repository
                val ingredientsToAdd = missingIngredients.map { ingredient ->
                    com.example.smartmeal.data.api.IngredientDetail(
                        name = ingredient.name,
                        quantity = ingredient.quantity,
                        has_in_pantry = false,
                        pantry_quantity = null,
                        need_to_buy = true
                    ) to ingredient.quantity
                }

                // Add to shopping list using existing repository method
                val result = shoppingListRepository.addIngredients(userId, ingredientsToAdd)

                result.onSuccess { message ->
                    Log.d(TAG, "Successfully added missing ingredients: $message")
                    Toast.makeText(
                        this@ActivityRecipeDetails,
                        "Added ${missingIngredients.size} missing ingredients to shopping list!",
                        Toast.LENGTH_LONG
                    ).show()

                    // Update button text
                    addToShoppingListButton.text = "✓ Added to Shopping List"
                    addToShoppingListButton.isEnabled = true
                }

                result.onFailure { error ->
                    Log.e(TAG, "Failed to add ingredients", error)
                    Toast.makeText(
                        this@ActivityRecipeDetails,
                        "Error: ${error.message}",
                        Toast.LENGTH_LONG
                    ).show()

                    // Reset button
                    addToShoppingListButton.text = "Add All Ingredients to Shopping List"
                    addToShoppingListButton.isEnabled = true
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception adding ingredients to shopping list", e)
                Toast.makeText(
                    this@ActivityRecipeDetails,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()

                // Reset button
                addToShoppingListButton.text = "Add All Ingredients to Shopping List"
                addToShoppingListButton.isEnabled = true
            }
        }
    }

    private fun showLoading(show: Boolean) {
        loadingProgressBar.visibility = if (show) View.VISIBLE else View.GONE
        recipeTitle.visibility = if (show) View.GONE else View.VISIBLE
        recipeDescription.visibility = if (show) View.GONE else View.VISIBLE
        ingredientsContainer.visibility = if (show) View.GONE else View.VISIBLE
        instructionsTextView.visibility = if (show) View.GONE else View.VISIBLE
        addToShoppingListButton.visibility = if (show) View.GONE else View.VISIBLE
        errorTextView.visibility = View.GONE
    }

    private fun showError(message: String) {
        loadingProgressBar.visibility = View.GONE
        errorTextView.visibility = View.VISIBLE
        errorTextView.text = message
        recipeTitle.visibility = View.GONE
        recipeDescription.visibility = View.GONE
        ingredientsContainer.visibility = View.GONE
        instructionsTextView.visibility = View.GONE
        addToShoppingListButton.visibility = View.GONE

        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
