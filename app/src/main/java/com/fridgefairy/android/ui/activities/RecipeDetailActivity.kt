package com.fridgefairy.android.ui.activities

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.fridgefairy.android.R
import com.fridgefairy.android.data.FridgeFairyDatabase
import com.fridgefairy.android.data.repository.RecipeRepository
import com.fridgefairy.android.databinding.ActivityRecipeDetailBinding
import com.fridgefairy.android.ui.viewmodels.RecipeViewModel
import com.fridgefairy.android.ui.viewmodels.RecipeViewModelFactory
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class RecipeDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecipeDetailBinding
    private var recipeId: Int = -1

    private val recipeViewModel: RecipeViewModel by viewModels {
        RecipeViewModelFactory(
            RecipeRepository(
                FridgeFairyDatabase.getDatabase(this).recipeDao()
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Recipe Details"

        recipeId = intent.getIntExtra("RECIPE_ID", -1)

        if (recipeId == -1) {
            Toast.makeText(this, "Error loading recipe", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadRecipeDetails()
    }

    private fun loadRecipeDetails() {
        lifecycleScope.launch {
            val recipe = recipeViewModel.getRecipeById(recipeId)

            if (recipe == null) {
                Toast.makeText(this@RecipeDetailActivity, "Recipe not found", Toast.LENGTH_SHORT).show()
                finish()
                return@launch
            }

            // Load image
            recipe.image?.let {
                binding.imageRecipe.load(it) {
                    crossfade(true)
                    placeholder(R.drawable.ic_launcher_background)
                }
            }

            // Set title
            binding.textRecipeTitle.text = recipe.title

            // Set metadata
            binding.textReadyInMinutes.text = "Ready in: ${recipe.readyInMinutes} minutes"
            binding.textServings.text = "Servings: ${recipe.servings}"

            // Set summary (strip HTML tags)
            val cleanSummary = recipe.summary.replace(Regex("<[^>]*>"), "")
            binding.textSummary.text = cleanSummary

            // Set ingredients
            if (recipe.ingredients.isNotEmpty()) {
                val ingredientsList = recipe.ingredients.joinToString("\n") { ingredient ->
                    "• ${ingredient.amount} ${ingredient.unit} ${ingredient.name}"
                }
                binding.textIngredients.text = ingredientsList
            } else {
                binding.textIngredients.text = "No ingredients available"
            }

            // Set instructions
            if (recipe.instructions.isNotBlank()) {
                val cleanInstructions = recipe.instructions.replace(Regex("<[^>]*>"), "")
                binding.textInstructions.text = cleanInstructions
            } else {
                binding.textInstructions.text = "No instructions available"
            }

            // Source URL button
            binding.buttonViewSource.setOnClickListener {
                recipe.sourceUrl?.let { url ->
                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
                    startActivity(intent)
                } ?: Toast.makeText(this@RecipeDetailActivity, "No source URL available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}