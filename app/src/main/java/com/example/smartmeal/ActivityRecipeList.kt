package com.example.smartmeal

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartmeal.adapter.RecipeListAdapter
import com.example.smartmeal.data.api.RecipeDetail
import com.example.smartmeal.data.repository.MealPlannerRepository
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class ActivityRecipeList : AppCompatActivity() {

    private val TAG = "ActivityRecipeList"
    private val mealPlannerRepository = MealPlannerRepository()
    private val shoppingListRepository = com.example.smartmeal.data.repository.ShoppingListRepository(
        com.example.smartmeal.data.api.RetrofitClient.api
    )
    private var userId: String = "1" // TODO: Get from auth session

    // UI Elements
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecipeListAdapter
    private lateinit var searchEditText: TextInputEditText
    private lateinit var emptyStateLayout: LinearLayout
    private lateinit var progressBar: ProgressBar

    // Filters
    private lateinit var chipAll: Chip
    private lateinit var chipBreakfast: Chip
    private lateinit var chipLunch: Chip
    private lateinit var chipDinner: Chip
    private lateinit var chipQuick: Chip

    // Data
    private var allRecipes = listOf<RecipeDetail>()
    private var filteredRecipes = listOf<RecipeDetail>()

    // Intent extras
    private var selectForMealType: String? = null // breakfast, lunch, dinner
    private var selectForDate: String? = null // YYYY-MM-DD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_list)

        Log.d(TAG, "ActivityRecipeList created")

        // Check if opened for meal selection
        selectForMealType = intent.getStringExtra("MEAL_TYPE")
        selectForDate = intent.getStringExtra("MEAL_DATE")

        initializeViews()
        setupRecyclerView()
        setupSearchBar()
        setupFilters()
        setupBackButton()

        loadRecipes()
    }

    private fun initializeViews() {
        recyclerView = findViewById(R.id.recipesRecyclerView)
        searchEditText = findViewById(R.id.searchEditText)
        emptyStateLayout = findViewById(R.id.emptyStateLayout)
        progressBar = findViewById(R.id.progressBar)

        chipAll = findViewById(R.id.chipAll)
        chipBreakfast = findViewById(R.id.chipBreakfast)
        chipLunch = findViewById(R.id.chipLunch)
        chipDinner = findViewById(R.id.chipDinner)
        chipQuick = findViewById(R.id.chipQuick)
    }

    private fun setupRecyclerView() {
        adapter = RecipeListAdapter { recipe ->
            onRecipeClicked(recipe)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupSearchBar() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterRecipes()
            }
        })
    }

    private fun setupFilters() {
        chipAll.setOnCheckedChangeListener { _, _ -> filterRecipes() }
        chipBreakfast.setOnCheckedChangeListener { _, _ -> filterRecipes() }
        chipLunch.setOnCheckedChangeListener { _, _ -> filterRecipes() }
        chipDinner.setOnCheckedChangeListener { _, _ -> filterRecipes() }
        chipQuick.setOnCheckedChangeListener { _, _ -> filterRecipes() }
    }

    private fun setupBackButton() {
        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            finish()
        }
    }

    private fun loadRecipes() {
        showLoading(true)

        lifecycleScope.launch {
            try {
                val result = mealPlannerRepository.getAllRecipes()

                result.onSuccess { recipes ->
                    Log.d(TAG, "Loaded ${recipes.size} recipes")
                    allRecipes = recipes
                    filterRecipes()
                }

                result.onFailure { error ->
                    Log.e(TAG, "Failed to load recipes", error)
                    Toast.makeText(this@ActivityRecipeList,
                        "Error loading recipes: ${error.message}", Toast.LENGTH_SHORT).show()
                    updateUI(emptyList())
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception loading recipes", e)
                updateUI(emptyList())
            } finally {
                showLoading(false)
            }
        }
    }

    private fun filterRecipes() {
        val query = searchEditText.text.toString().trim().lowercase()

        // Start with all recipes
        var filtered = allRecipes

        // Apply search filter
        if (query.isNotBlank()) {
            filtered = filtered.filter { recipe ->
                recipe.title.lowercase().contains(query) ||
                recipe.description?.lowercase()?.contains(query) == true ||
                recipe.cuisine?.lowercase()?.contains(query) == true
            }
        }

        // Apply chip filters
        if (!chipAll.isChecked) {
            // Filter by meal type (this is a simple implementation)
            // In a real app, recipes would have meal_type field
            if (chipBreakfast.isChecked) {
                filtered = filtered.filter { recipe ->
                    recipe.title.lowercase().contains("breakfast") ||
                    recipe.title.lowercase().contains("egg") ||
                    recipe.title.lowercase().contains("toast") ||
                    recipe.title.lowercase().contains("pancake") ||
                    recipe.title.lowercase().contains("oatmeal")
                }
            }

            if (chipLunch.isChecked) {
                filtered = filtered.filter { recipe ->
                    recipe.title.lowercase().contains("lunch") ||
                    recipe.title.lowercase().contains("salad") ||
                    recipe.title.lowercase().contains("sandwich") ||
                    recipe.title.lowercase().contains("soup")
                }
            }

            if (chipDinner.isChecked) {
                filtered = filtered.filter { recipe ->
                    recipe.title.lowercase().contains("dinner") ||
                    recipe.title.lowercase().contains("pasta") ||
                    recipe.title.lowercase().contains("chicken") ||
                    recipe.title.lowercase().contains("beef") ||
                    recipe.title.lowercase().contains("fish")
                }
            }
        }

        // Apply quick filter (< 30 minutes)
        if (chipQuick.isChecked) {
            filtered = filtered.filter { recipe ->
                (recipe.prep_time + recipe.cook_time) < 30
            }
        }

        filteredRecipes = filtered
        updateUI(filteredRecipes)
    }

    private fun updateUI(recipes: List<RecipeDetail>) {
        if (recipes.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyStateLayout.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyStateLayout.visibility = View.GONE
            adapter.submitList(recipes)
        }
    }

    private fun onRecipeClicked(recipe: RecipeDetail) {
        Log.d(TAG, "Recipe clicked: ${recipe.title}")

        // Check if we're selecting for a meal plan
        if (selectForMealType != null && selectForDate != null) {
            showAddToMealDialog(recipe)
        } else {
            // Navigate to recipe details screen
            val intent = Intent(this, ActivityRecipeDetails::class.java).apply {
                putExtra("RECIPE_ID", recipe.recipe_id)
                putExtra("RECIPE_NAME", recipe.title)
            }
            startActivity(intent)
        }
    }

    private fun showAddToMealDialog(recipe: RecipeDetail) {
        val mealTypeCapitalized = selectForMealType?.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString()
        } ?: "Meal"

        AlertDialog.Builder(this, R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setTitle("Add to $mealTypeCapitalized?")
            .setMessage("Add \"${recipe.title}\" to your $selectForMealType on $selectForDate?")
            .setPositiveButton("Add") { _, _ ->
                addRecipeToMealPlan(recipe)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun addRecipeToMealPlan(recipe: RecipeDetail) {
        if (selectForMealType == null || selectForDate == null) {
            Toast.makeText(this, "Error: Missing meal information", Toast.LENGTH_SHORT).show()
            return
        }

        showLoading(true)

        lifecycleScope.launch {
            try {
                val result = mealPlannerRepository.addMealToPlan(
                    userId,
                    recipe.recipe_id,
                    selectForDate!!,
                    selectForMealType!!
                )

                result.onSuccess { addMealResult ->
                    Log.d(TAG, "Meal added: ${addMealResult.plan_id}")

                    // Show ingredient confirmation dialog
                    val ingredientsInfo = addMealResult.ingredients_info
                    showIngredientConfirmationDialog(recipe.title, ingredientsInfo)
                }

                result.onFailure { error ->
                    Log.e(TAG, "Failed to add meal", error)
                    Toast.makeText(this@ActivityRecipeList,
                        "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    showLoading(false)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception adding meal", e)
                Toast.makeText(this@ActivityRecipeList,
                    "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                showLoading(false)
            }
        }
    }


    private fun showIngredientConfirmationDialog(recipeTitle: String, ingredientsInfo: com.example.smartmeal.data.api.IngredientsInfo) {
        showLoading(false)

        val dialogView = layoutInflater.inflate(R.layout.dialog_ingredient_confirmation, null)

        // Setup summary
        val summaryText = dialogView.findViewById<TextView>(R.id.summaryText)
        summaryText.text = "✅ You have: ${ingredientsInfo.already_have}\n🛒 Need to buy: ${ingredientsInfo.need_to_buy}"

        // Setup RecyclerView
        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.ingredientsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = com.example.smartmeal.adapter.IngredientConfirmationAdapter(
            ingredientsInfo.details
        ) { ingredient, quantity ->
            Log.d(TAG, "Quantity changed: ${ingredient.name} = $quantity")
        }
        recyclerView.adapter = adapter

        val dialog = AlertDialog.Builder(this, R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        // Setup buttons
        dialogView.findViewById<Button>(R.id.cancelButton).setOnClickListener {
            dialog.dismiss()
            finish()
        }

        dialogView.findViewById<Button>(R.id.confirmButton).setOnClickListener {
            val updatedIngredients = adapter.getUpdatedIngredients()
            addIngredientsToShoppingList(updatedIngredients, dialog)
        }

        dialog.show()
    }

    private fun addIngredientsToShoppingList(
        ingredients: List<Pair<com.example.smartmeal.data.api.IngredientDetail, String>>,
        dialog: AlertDialog
    ) {
        showLoading(true)

        lifecycleScope.launch {
            try {
                // Call shopping list repository to add ingredients
                val result = shoppingListRepository.addIngredients(userId, ingredients)

                result.onSuccess { message ->
                    Log.d(TAG, "Ingredients added to shopping list: $message")
                    showLoading(false)
                    dialog.dismiss()

                    Toast.makeText(
                        this@ActivityRecipeList,
                        "Ingredients added to shopping list!",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Return to meal planner
                    setResult(RESULT_OK)
                    finish()
                }

                result.onFailure { error ->
                    Log.e(TAG, "Failed to add to shopping list", error)
                    showLoading(false)
                    Toast.makeText(
                        this@ActivityRecipeList,
                        "Error: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception adding to shopping list", e)
                showLoading(false)
                Toast.makeText(
                    this@ActivityRecipeList,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }
}
