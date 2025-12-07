package com.example.smartmeal.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.smartmeal.R
import com.example.smartmeal.data.api.RecipeDetail

/**
 * Adapter for displaying recipes in RecyclerView
 */
class RecipeListAdapter(
    private val onRecipeClick: (RecipeDetail) -> Unit
) : ListAdapter<RecipeDetail, RecipeListAdapter.RecipeViewHolder>(RecipeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(view, onRecipeClick)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class RecipeViewHolder(
        itemView: View,
        private val onRecipeClick: (RecipeDetail) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val recipeTitle: TextView = itemView.findViewById(R.id.recipeTitle)
        private val recipeDescription: TextView = itemView.findViewById(R.id.recipeDescription)
        private val recipeTime: TextView = itemView.findViewById(R.id.recipeTime)
        private val recipeDifficulty: TextView = itemView.findViewById(R.id.recipeDifficulty)
        private val recipeIngredientCount: TextView = itemView.findViewById(R.id.recipeIngredientCount)

        fun bind(recipe: RecipeDetail) {
            recipeTitle.text = recipe.title
            recipeDescription.text = recipe.description ?: "Delicious recipe"

            // Calculate total time
            val totalTime = recipe.prep_time + recipe.cook_time
            recipeTime.text = "⏱️ $totalTime min"

            // Difficulty
            recipeDifficulty.text = "📊 ${recipe.difficulty}"

            // Ingredient count
            val ingredientCount = recipe.ingredients.size
            recipeIngredientCount.text = "🥘 $ingredientCount items"

            itemView.setOnClickListener { onRecipeClick(recipe) }
        }
    }

    class RecipeDiffCallback : DiffUtil.ItemCallback<RecipeDetail>() {
        override fun areItemsTheSame(oldItem: RecipeDetail, newItem: RecipeDetail): Boolean {
            return oldItem.recipe_id == newItem.recipe_id
        }

        override fun areContentsTheSame(oldItem: RecipeDetail, newItem: RecipeDetail): Boolean {
            return oldItem == newItem
        }
    }
}

