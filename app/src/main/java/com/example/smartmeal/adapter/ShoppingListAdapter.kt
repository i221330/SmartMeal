package com.example.smartmeal.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smartmeal.R
import com.example.smartmeal.data.api.ShoppingItem

/**
 * Adapter for Shopping List
 */
class ShoppingListAdapter(
    private var items: List<ShoppingItem>,
    private val onItemChecked: (ShoppingItem) -> Unit,
    private val onItemDeleted: (ShoppingItem) -> Unit,
    private val onItemLongPress: (ShoppingItem) -> Unit
) : RecyclerView.Adapter<ShoppingListAdapter.ViewHolder>() {

    private var filteredItems = items

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_shopping_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(filteredItems[position])
    }

    override fun getItemCount() = filteredItems.size

    fun updateItems(newItems: List<ShoppingItem>) {
        items = newItems
        filteredItems = newItems
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        filteredItems = if (query.isEmpty()) {
            items
        } else {
            items.filter { it.name.contains(query, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkbox: CheckBox = itemView.findViewById(R.id.itemCheckbox)
        private val itemName: TextView = itemView.findViewById(R.id.itemName)
        private val itemQuantity: TextView = itemView.findViewById(R.id.itemQuantity)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)

        fun bind(item: ShoppingItem) {
            itemName.text = item.name
            itemQuantity.text = item.quantity
            checkbox.isChecked = item.isCompleted

            // Hide delete button by default (not used with long-press dialog)
            deleteButton.visibility = View.GONE

            // Checkbox click - mark as purchased
            checkbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    onItemChecked(item)
                    // Reset checkbox state - will be removed from list after API call
                    checkbox.isChecked = false
                }
            }

            // Long press to show action dialog
            itemView.setOnLongClickListener {
                onItemLongPress(item)
                true
            }
        }
    }
}

