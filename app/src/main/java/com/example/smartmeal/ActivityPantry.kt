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
import com.example.smartmeal.adapter.PantryAdapter
import com.example.smartmeal.data.api.PantryItem
import com.example.smartmeal.data.repository.MasterIngredient
import com.example.smartmeal.data.repository.PantryRepository
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class ActivityPantry : AppCompatActivity() {

    private val TAG = "ActivityPantry"
    private val pantryRepository = PantryRepository()
    private var userId: String = "1" // TODO: Get from auth session

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PantryAdapter
    private lateinit var searchEditText: TextInputEditText
    private lateinit var emptyStateLayout: LinearLayout
    private lateinit var progressBar: ProgressBar

    private var allPantryItems = listOf<PantryItem>()
    private var allMasterIngredients = listOf<MasterIngredient>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantry)

        Log.d(TAG, "ActivityPantry created")

        initializeViews()
        setupRecyclerView()
        setupSearchBar()
        setupButtons()
        setupBottomNavigation()

        loadMasterIngredients()
        loadPantryItems()
    }

    private fun initializeViews() {
        recyclerView = findViewById(R.id.pantryRecyclerView)
        searchEditText = findViewById(R.id.searchEditText)
        emptyStateLayout = findViewById(R.id.emptyStateLayout)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupRecyclerView() {
        adapter = PantryAdapter(
            onEditClick = { item -> showEditItemDialog(item) },
            onDeleteClick = { item -> showDeleteConfirmation(item) }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupSearchBar() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterPantryItems(s.toString())
            }
        })
    }

    private fun setupButtons() {
        findViewById<MaterialButton>(R.id.addItemButton)?.setOnClickListener {
            showAddItemDialog()
        }

        findViewById<MaterialButton>(R.id.emptyAddButton)?.setOnClickListener {
            showAddItemDialog()
        }
    }

    private fun loadMasterIngredients() {
        lifecycleScope.launch {
            try {
                val result = pantryRepository.getAllIngredients()
                result.onSuccess { ingredients ->
                    allMasterIngredients = ingredients
                    Log.d(TAG, "Loaded ${ingredients.size} master ingredients")
                }
                result.onFailure { error ->
                    Log.e(TAG, "Failed to load master ingredients", error)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception loading master ingredients", e)
            }
        }
    }

    private fun loadPantryItems() {
        showLoading(true)

        lifecycleScope.launch {
            try {
                val result = pantryRepository.getUserPantry(userId)

                result.onSuccess { items ->
                    Log.d(TAG, "Loaded ${items.size} pantry items")
                    allPantryItems = items
                    updateUI(items)
                }

                result.onFailure { error ->
                    Log.e(TAG, "Failed to load pantry", error)
                    Toast.makeText(this@ActivityPantry, "Error loading pantry: ${error.message}", Toast.LENGTH_SHORT).show()
                    updateUI(emptyList())
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception loading pantry", e)
                updateUI(emptyList())
            } finally {
                showLoading(false)
            }
        }
    }

    private fun filterPantryItems(query: String) {
        if (query.isBlank()) {
            adapter.submitList(allPantryItems)
        } else {
            val filtered = allPantryItems.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.category?.contains(query, ignoreCase = true) == true
            }
            adapter.submitList(filtered)
        }
    }

    private fun updateUI(items: List<PantryItem>) {
        if (items.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyStateLayout.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyStateLayout.visibility = View.GONE
            adapter.submitList(items)
        }
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showAddItemDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_pantry_item, null)
        val searchInput = dialogView.findViewById<AutoCompleteTextView>(R.id.ingredientSearchInput)
        val quantityInput = dialogView.findViewById<EditText>(R.id.quantityInput)

        // Setup autocomplete with master ingredients
        val ingredientNames = allMasterIngredients.map { it.name }
        val autoCompleteAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, ingredientNames)
        searchInput.setAdapter(autoCompleteAdapter)
        searchInput.threshold = 1

        val dialog = AlertDialog.Builder(this, R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val ingredientName = searchInput.text.toString()
                val quantity = quantityInput.text.toString()

                if (ingredientName.isNotBlank() && quantity.isNotBlank()) {
                    // Find the ingredient to get its category
                    val ingredient = allMasterIngredients.find {
                        it.name.equals(ingredientName, ignoreCase = true)
                    }
                    addPantryItem(ingredientName, quantity, ingredient?.category)
                } else {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        // Style the dialog buttons
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(getColor(R.color.primary))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(getColor(R.color.text_light))
        }

        dialog.show()
    }

    private fun showEditItemDialog(item: PantryItem) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_pantry_item, null)
        val nameText = dialogView.findViewById<TextView>(R.id.itemNameText)
        val quantityInput = dialogView.findViewById<EditText>(R.id.quantityInput)

        nameText.text = item.name
        quantityInput.setText(item.quantity)

        val dialog = AlertDialog.Builder(this, R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setView(dialogView)
            .setPositiveButton("Update") { _, _ ->
                val newQuantity = quantityInput.text.toString()
                if (newQuantity.isNotBlank()) {
                    updatePantryItem(item.item_id, newQuantity)
                } else {
                    Toast.makeText(this, "Please enter quantity", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        // Style the dialog buttons
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(getColor(R.color.primary))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(getColor(R.color.text_light))
        }

        dialog.show()
    }

    private fun showDeleteConfirmation(item: PantryItem) {
        AlertDialog.Builder(this, R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setTitle("Delete Item")
            .setMessage("Remove ${item.name} from your pantry?")
            .setPositiveButton("Delete") { _, _ ->
                deletePantryItem(item.item_id)
            }
            .setNegativeButton("Cancel", null)
            .create()
            .apply {
                setOnShowListener {
                    getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(getColor(android.R.color.holo_red_dark))
                    getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(getColor(R.color.text_light))
                }
                show()
            }
    }

    private fun addPantryItem(name: String, quantity: String, category: String?) {
        showLoading(true)

        lifecycleScope.launch {
            try {
                val result = pantryRepository.addPantryItem(userId, name, quantity, category)

                result.onSuccess { message ->
                    Log.d(TAG, "Item added: $message")
                    Toast.makeText(this@ActivityPantry, "Added to pantry!", Toast.LENGTH_SHORT).show()
                    loadPantryItems() // Refresh list
                }

                result.onFailure { error ->
                    Log.e(TAG, "Failed to add item", error)
                    Toast.makeText(this@ActivityPantry, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    showLoading(false)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception adding item", e)
                showLoading(false)
            }
        }
    }

    private fun updatePantryItem(itemId: String, quantity: String) {
        showLoading(true)

        lifecycleScope.launch {
            try {
                val result = pantryRepository.updatePantryItem(itemId, quantity)

                result.onSuccess { message ->
                    Log.d(TAG, "Item updated: $message")
                    Toast.makeText(this@ActivityPantry, "Updated!", Toast.LENGTH_SHORT).show()
                    loadPantryItems() // Refresh list
                }

                result.onFailure { error ->
                    Log.e(TAG, "Failed to update item", error)
                    Toast.makeText(this@ActivityPantry, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    showLoading(false)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception updating item", e)
                showLoading(false)
            }
        }
    }

    private fun deletePantryItem(itemId: String) {
        showLoading(true)

        lifecycleScope.launch {
            try {
                val result = pantryRepository.deletePantryItem(itemId)

                result.onSuccess { message ->
                    Log.d(TAG, "Item deleted: $message")
                    Toast.makeText(this@ActivityPantry, "Deleted from pantry", Toast.LENGTH_SHORT).show()
                    loadPantryItems() // Refresh list
                }

                result.onFailure { error ->
                    Log.e(TAG, "Failed to delete item", error)
                    Toast.makeText(this@ActivityPantry, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    showLoading(false)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception deleting item", e)
                showLoading(false)
            }
        }
    }

    private fun setupBottomNavigation() {
        findViewById<View>(R.id.navHome)?.setOnClickListener {
            startActivity(Intent(this, ActivityHome::class.java))
            finish()
        }
        findViewById<View>(R.id.navPantry)?.setOnClickListener {
            // Already on pantry
        }
        findViewById<View>(R.id.navPlanner)?.setOnClickListener {
            startActivity(Intent(this, ActivityMealPlanner::class.java))
            finish()
        }
        findViewById<View>(R.id.navShopping)?.setOnClickListener {
            startActivity(Intent(this, ActivityShoppingList::class.java))
            finish()
        }
        findViewById<View>(R.id.navProfile)?.setOnClickListener {
            startActivity(Intent(this, ActivityProfile::class.java))
            finish()
        }
    }
}

