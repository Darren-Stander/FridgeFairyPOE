package com.fridgefairy.android.ui.adapters

import android.content.Intent
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
import com.fridgefairy.android.ui.activities.RecipeDetailActivity

class RecipeAdapter : ListAdapter<Recipe, RecipeAdapter.RecipeViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = getItem(position)
        holder.bind(recipe)
    }

    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.text_recipe_title)
        private val servingsTextView: TextView = itemView.findViewById(R.id.text_recipe_servings)
        private val readyInMinutesTextView: TextView = itemView.findViewById(R.id.text_recipe_ready_in_minutes)
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

            // Launch detail activity on click
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, RecipeDetailActivity::class.java)
                intent.putExtra("RECIPE_ID", recipe.id)
                itemView.context.startActivity(intent)
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