package com.example.smartmeal

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartmeal.adapter.ShoppingListAdapter
import com.example.smartmeal.data.api.RetrofitClient
import com.example.smartmeal.data.api.ShoppingItem
import com.example.smartmeal.data.repository.MasterIngredient
import com.example.smartmeal.data.repository.PantryRepository
import com.example.smartmeal.data.repository.ShoppingListRepository
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class ActivityShoppingList : AppCompatActivity() {

    private val TAG = "ActivityShoppingList"
    private val userId = "1" // TODO: Get from auth

    private lateinit var repository: ShoppingListRepository
    private lateinit var pantryRepository: PantryRepository
    private lateinit var adapter: ShoppingListAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyStateLayout: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var searchEditText: AutoCompleteTextView
    private lateinit var addItemFab: MaterialButton

    private var allItems = listOf<ShoppingItem>()
    private var masterIngredients = listOf<MasterIngredient>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_list)

        Log.d(TAG, "ActivityShoppingList created")

        repository = ShoppingListRepository(RetrofitClient.api)
        pantryRepository = PantryRepository()

        setupViews()
        setupRecyclerView()
        setupListeners()
        loadMasterIngredients()
        setupBottomNavigation()

        loadShoppingList()
    }

    override fun onResume() {
        super.onResume()
        loadShoppingList()
    }

    private fun setupViews() {
        recyclerView = findViewById(R.id.shoppingListRecyclerView)
        emptyStateLayout = findViewById(R.id.emptyStateLayout)
        progressBar = findViewById(R.id.progressBar)
        searchEditText = findViewById(R.id.searchEditText)
        addItemFab = findViewById(R.id.addItemFab)
    }

    private fun setupRecyclerView() {
        adapter = ShoppingListAdapter(
            emptyList(),
            onItemChecked = { item -> showPurchaseQuantityDialog(item) },
            onItemDeleted = { item -> confirmDeleteItem(item) }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupListeners() {
        // Input field works as both search (when typing) and add (when button clicked)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val text = s?.toString() ?: ""
                // Only filter if there's text, otherwise show all
                if (text.isEmpty()) {
                    adapter.updateItems(allItems)
                } else {
                    adapter.filter(text)
                }
            }
        })

        // Add button - Add item with name from input field
        addItemFab.setOnClickListener {
            val itemName = searchEditText.text.toString().trim()
            if (itemName.isEmpty()) {
                Toast.makeText(this, "Please enter item name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Show dialog to enter quantity
            showQuantityInputDialog(itemName)
        }
    }

    private fun showQuantityInputDialog(itemName: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_quantity_input, null)
        val quantityInput = dialogView.findViewById<TextInputEditText>(R.id.quantityInput)

        AlertDialog.Builder(this, R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setTitle("Add to Shopping List")
            .setMessage("How much $itemName do you need?")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val quantity = quantityInput.text.toString().trim()
                if (quantity.isNotEmpty()) {
                    addCustomItem(itemName, quantity)
                    searchEditText.text?.clear() // Clear input after adding
                } else {
                    Toast.makeText(this, "Please enter a quantity", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun loadShoppingList() {
        showLoading(true)

        lifecycleScope.launch {
            try {
                val result = repository.getShoppingList(userId)

                result.onSuccess { items ->
                    Log.d(TAG, "Loaded ${items.size} shopping items")
                    allItems = items
                    adapter.updateItems(items)
                    updateEmptyState(items.isEmpty())
                    showLoading(false)
                }

                result.onFailure { error ->
                    Log.e(TAG, "Failed to load shopping list", error)
                    Toast.makeText(this@ActivityShoppingList,
                        "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    updateEmptyState(true)
                    showLoading(false)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception loading shopping list", e)
                Toast.makeText(this@ActivityShoppingList,
                    "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                updateEmptyState(true)
                showLoading(false)
            }
        }
    }

    private fun loadMasterIngredients() {
        lifecycleScope.launch {
            try {
                Log.d(TAG, "Loading master ingredients...")
                val result = pantryRepository.getAllIngredients()

                result.onSuccess { ingredients ->
                    Log.d(TAG, "Loaded ${ingredients.size} master ingredients")
                    masterIngredients = ingredients
                    setupAutocomplete()
                }

                result.onFailure { error ->
                    Log.e(TAG, "Failed to load master ingredients", error)
                    // Continue without autocomplete
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception loading master ingredients", e)
            }
        }
    }

    private fun setupAutocomplete() {
        // Extract ingredient names for autocomplete
        val ingredientNames = masterIngredients.map { it.name }

        // Create adapter for AutoCompleteTextView
        val autocompleteAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            ingredientNames
        )

        searchEditText.setAdapter(autocompleteAdapter)
        searchEditText.threshold = 1  // Show suggestions after 1 character

        Log.d(TAG, "Autocomplete setup with ${ingredientNames.size} ingredients")
    }

    private fun showPurchaseQuantityDialog(item: ShoppingItem) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_quantity_input, null)
        val quantityInput = dialogView.findViewById<TextInputEditText>(R.id.quantityInput)
        quantityInput.setText(item.quantity)

        AlertDialog.Builder(this, R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setTitle("How much did you buy?")
            .setMessage("${item.name}\nSuggested: ${item.quantity}")
            .setView(dialogView)
            .setPositiveButton("Add to Pantry") { _, _ ->
                val quantity = quantityInput.text.toString()
                if (quantity.isNotEmpty()) {
                    markItemAsPurchased(item, quantity)
                } else {
                    Toast.makeText(this, "Please enter a quantity", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun markItemAsPurchased(item: ShoppingItem, quantityBought: String) {
        showLoading(true)

        lifecycleScope.launch {
            try {
                val result = repository.markAsPurchased(
                    item.item_id,
                    item.name,
                    quantityBought,
                    userId
                )

                result.onSuccess { message ->
                    Log.d(TAG, "Item marked as purchased: ${item.name}")
                    Toast.makeText(this@ActivityShoppingList,
                        "Added to pantry!", Toast.LENGTH_SHORT).show()
                    loadShoppingList() // Reload list
                }

                result.onFailure { error ->
                    Log.e(TAG, "Failed to mark as purchased", error)
                    Toast.makeText(this@ActivityShoppingList,
                        "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    showLoading(false)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception marking as purchased", e)
                Toast.makeText(this@ActivityShoppingList,
                    "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                showLoading(false)
            }
        }
    }

    private fun confirmDeleteItem(item: ShoppingItem) {
        AlertDialog.Builder(this, R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setTitle("Remove Item?")
            .setMessage("Remove \"${item.name}\" from your shopping list?")
            .setPositiveButton("Remove") { _, _ ->
                deleteItem(item)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteItem(item: ShoppingItem) {
        showLoading(true)

        lifecycleScope.launch {
            try {
                val result = repository.deleteItem(item.item_id)

                result.onSuccess { message ->
                    Log.d(TAG, "Item deleted: ${item.name}")
                    Toast.makeText(this@ActivityShoppingList,
                        "Item removed", Toast.LENGTH_SHORT).show()
                    loadShoppingList() // Reload list
                }

                result.onFailure { error ->
                    Log.e(TAG, "Failed to delete item", error)
                    Toast.makeText(this@ActivityShoppingList,
                        "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    showLoading(false)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception deleting item", e)
                Toast.makeText(this@ActivityShoppingList,
                    "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                showLoading(false)
            }
        }
    }


    private fun addCustomItem(name: String, quantity: String) {
        showLoading(true)

        lifecycleScope.launch {
            try {
                val result = repository.addCustomItem(userId, name, quantity)

                result.onSuccess { message ->
                    Log.d(TAG, "Custom item added: $name")
                    Toast.makeText(this@ActivityShoppingList,
                        "Item added!", Toast.LENGTH_SHORT).show()
                    loadShoppingList() // Reload list
                }

                result.onFailure { error ->
                    Log.e(TAG, "Failed to add custom item", error)
                    Toast.makeText(this@ActivityShoppingList,
                        "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    showLoading(false)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception adding custom item", e)
                Toast.makeText(this@ActivityShoppingList,
                    "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                showLoading(false)
            }
        }
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            recyclerView.visibility = View.GONE
            emptyStateLayout.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyStateLayout.visibility = View.GONE
        }
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun setupBottomNavigation() {
        findViewById<View>(R.id.homeNav)?.setOnClickListener {
            startActivity(Intent(this, ActivityHome::class.java))
            finish()
        }
        findViewById<View>(R.id.pantryNav)?.setOnClickListener {
            startActivity(Intent(this, ActivityPantry::class.java))
            finish()
        }
        findViewById<View>(R.id.plannerNav)?.setOnClickListener {
            startActivity(Intent(this, ActivityMealPlanner::class.java))
            finish()
        }
        findViewById<View>(R.id.profileNav)?.setOnClickListener {
            startActivity(Intent(this, ActivityProfile::class.java))
            finish()
        }
    }
}

