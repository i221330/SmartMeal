package com.example.smartmeal.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.smartmeal.R
import com.example.smartmeal.data.api.PantryItem

/**
 * Adapter for displaying pantry items in RecyclerView
 */
class PantryAdapter(
    private val onEditClick: (PantryItem) -> Unit,
    private val onDeleteClick: (PantryItem) -> Unit
) : ListAdapter<PantryItem, PantryAdapter.PantryViewHolder>(PantryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PantryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pantry, parent, false)
        return PantryViewHolder(view, onEditClick, onDeleteClick)
    }

    override fun onBindViewHolder(holder: PantryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PantryViewHolder(
        itemView: View,
        private val onEditClick: (PantryItem) -> Unit,
        private val onDeleteClick: (PantryItem) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val itemName: TextView = itemView.findViewById(R.id.itemName)
        private val itemQuantity: TextView = itemView.findViewById(R.id.itemQuantity)
        private val itemCategory: TextView = itemView.findViewById(R.id.itemCategory)
        private val itemIcon: TextView = itemView.findViewById(R.id.itemIcon)
        private val editButton: ImageButton = itemView.findViewById(R.id.editButton)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)

        fun bind(item: PantryItem) {
            itemName.text = item.name
            itemQuantity.text = item.quantity
            itemCategory.text = item.category ?: "Other"

            // Set emoji based on category
            itemIcon.text = when (item.category) {
                "Vegetables" -> "🥬"
                "Fruits" -> "🍎"
                "Meat & Poultry" -> "🍗"
                "Seafood" -> "🐟"
                "Dairy & Eggs" -> "🥛"
                "Grains & Pasta" -> "🌾"
                "Legumes & Beans" -> "🫘"
                "Spices & Herbs" -> "🌿"
                "Oils & Condiments" -> "🫗"
                "Sweeteners & Baking" -> "🍯"
                "Broths & Stocks" -> "🥣"
                "Nuts & Seeds" -> "🥜"
                else -> "🥫"
            }

            editButton.setOnClickListener { onEditClick(item) }
            deleteButton.setOnClickListener { onDeleteClick(item) }
        }
    }

    class PantryDiffCallback : DiffUtil.ItemCallback<PantryItem>() {
        override fun areItemsTheSame(oldItem: PantryItem, newItem: PantryItem): Boolean {
            return oldItem.item_id == newItem.item_id
        }

        override fun areContentsTheSame(oldItem: PantryItem, newItem: PantryItem): Boolean {
            return oldItem == newItem
        }
    }
}

