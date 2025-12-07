package com.example.smartmeal

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
import com.example.smartmeal.data.api.RetrofitClient
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class ActivityRecipeDetails : AppCompatActivity() {

    private val TAG = "ActivityRecipeDetails"
    private var recipeId: String? = null
    private var currentRecipe: RecipeDetail? = null

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
    }

    private fun setupBackButton() {
        findViewById<View>(R.id.backButton)?.setOnClickListener {
            finish()
        }
    }


    private fun loadRecipeDetails() {
        lifecycleScope.launch {
            try {
                showLoading(true)

                Log.d(TAG, "Fetching recipe details for: $recipeId")
                val response = RetrofitClient.api.getRecipeDetail(recipeId = recipeId!!)

                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!

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
                    Log.e(TAG, "API call failed: ${response.code()} - ${response.message()}")
                    showError("Failed to load recipe: ${response.message()}")
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

    private fun displayIngredients(ingredients: List<com.example.smartmeal.data.api.RecipeIngredient>) {
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
            val ingredientCard = createIngredientCard(ingredient)
            ingredientsContainer.addView(ingredientCard)
        }
    }

    private fun createIngredientCard(ingredient: com.example.smartmeal.data.api.RecipeIngredient): View {
        val cardView = CardView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = (8 * resources.displayMetrics.density).toInt()
            }
            radius = 8 * resources.displayMetrics.density
            setCardBackgroundColor(getColor(R.color.subtle_light))
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
            buttonTintList = getColorStateList(R.color.primary)
        }

        val textView = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            ).apply {
                marginStart = (12 * resources.displayMetrics.density).toInt()
            }
            text = "${ingredient.quantity} ${ingredient.name}"
            textSize = 16f
            setTextColor(getColor(R.color.text_light))
        }

        linearLayout.addView(checkBox)
        linearLayout.addView(textView)
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
