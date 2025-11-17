// This file defines the RecyclerView adapter for displaying Recipe objects.
// It uses ListAdapter for efficient updates and binds Recipe data to the
// item_recipe.xml layout, using Coil to load recipe images.

package com.fridgefairy.android.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.fridgefairy.android.R
import com.fridgefairy.android.data.entities.Recipe

class RecipeAdapter(
    private val onItemClick: (Recipe) -> Unit
) : ListAdapter<Recipe, RecipeAdapter.RecipeViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = getItem(position)
        holder.bind(recipe)
    }

    class RecipeViewHolder(
        itemView: View,
        private val onItemClick: (Recipe) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val titleTextView: TextView = itemView.findViewById(R.id.text_recipe_title)
        private val servingsTextView: TextView = itemView.findViewById(R.id.text_recipe_servings)
        private val readyInMinutesTextView: TextView =
            itemView.findViewById(R.id.text_recipe_ready_in_minutes)
        private val recipeImageView: ImageView = itemView.findViewById(R.id.image_recipe)

        fun bind(recipe: Recipe) {
            titleTextView.text = recipe.title
            servingsTextView.text = "Servings: ${recipe.servings}"
            readyInMinutesTextView.text = "Ready in: ${recipe.readyInMinutes} min"

            recipe.image?.let {
                recipeImageView.load(it) {
                    crossfade(true)
                    placeholder(R.drawable.ic_launcher_background)
                }
            } ?: recipeImageView.setImageResource(R.drawable.ic_launcher_background)

            itemView.setOnClickListener {
                onItemClick(recipe)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Recipe>() {
        override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem == newItem
        }
    }
}
