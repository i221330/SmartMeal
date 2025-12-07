package com.example.smartmeal.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smartmeal.R
import com.example.smartmeal.data.api.IngredientDetail

/**
 * Adapter for ingredient confirmation dialog
 */
class IngredientConfirmationAdapter(
    private val ingredients: List<IngredientDetail>,
    private val onQuantityChanged: (IngredientDetail, String) -> Unit
) : RecyclerView.Adapter<IngredientConfirmationAdapter.ViewHolder>() {

    // Store user-edited quantities
    private val quantitiesMap = mutableMapOf<String, String>()

    init {
        // Initialize quantities with need_to_buy amounts
        ingredients.forEach { ingredient ->
            if (ingredient.need_to_buy) {
                quantitiesMap[ingredient.name] = ingredient.quantity
            } else {
                quantitiesMap[ingredient.name] = "0"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ingredient_confirmation, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(ingredients[position])
    }

    override fun getItemCount() = ingredients.size

    fun getUpdatedIngredients(): List<Pair<IngredientDetail, String>> {
        return ingredients.map { ingredient ->
            ingredient to (quantitiesMap[ingredient.name] ?: "0")
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ingredientName: TextView = itemView.findViewById(R.id.ingredientName)
        private val ingredientQuantity: TextView = itemView.findViewById(R.id.ingredientQuantity)
        private val ingredientPantryStatus: TextView = itemView.findViewById(R.id.ingredientPantryStatus)
        private val quantityInputLayout: LinearLayout = itemView.findViewById(R.id.quantityInputLayout)
        private val quantityInput: EditText = itemView.findViewById(R.id.quantityInput)

        fun bind(ingredient: IngredientDetail) {
            ingredientName.text = ingredient.name
            ingredientQuantity.text = "Recipe needs: ${ingredient.quantity}"

            // Show pantry status
            if (ingredient.has_in_pantry) {
                ingredientPantryStatus.visibility = View.VISIBLE
                ingredientPantryStatus.text = "✅ You have: ${ingredient.pantry_quantity ?: "some"}"
            } else {
                ingredientPantryStatus.visibility = View.GONE
            }

            // Setup quantity input
            if (ingredient.need_to_buy) {
                quantityInputLayout.visibility = View.VISIBLE
                quantityInput.setText(quantitiesMap[ingredient.name] ?: ingredient.quantity)

                // Remove previous text watcher
                quantityInput.tag?.let {
                    quantityInput.removeTextChangedListener(it as TextWatcher)
                }

                // Add new text watcher
                val textWatcher = object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        val newQuantity = s?.toString() ?: "0"
                        quantitiesMap[ingredient.name] = newQuantity
                        onQuantityChanged(ingredient, newQuantity)
                    }
                }
                quantityInput.tag = textWatcher
                quantityInput.addTextChangedListener(textWatcher)
            } else {
                quantityInputLayout.visibility = View.GONE
            }
        }
    }
}

