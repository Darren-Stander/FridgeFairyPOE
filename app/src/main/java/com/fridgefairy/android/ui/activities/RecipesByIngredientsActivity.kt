// Start of file: RecipesByIngredientsActivity.kt
// This activity allows users to find recipes based on ingredients available in their fridge.
// It uses FridgeViewModel to get fridge items and RecipeViewModel to fetch recipes.
package com.fridgefairy.android.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.fridgefairy.android.BuildConfig
import com.fridgefairy.android.data.FridgeFairyDatabase
import com.fridgefairy.android.data.repository.FoodRepository
import com.fridgefairy.android.data.repository.RecipeRepository
import com.fridgefairy.android.databinding.ActivityRecipesByIngredientsBinding
import com.fridgefairy.android.ui.adapters.RecipeAdapter
import com.fridgefairy.android.ui.viewmodels.FridgeViewModel
import com.fridgefairy.android.ui.viewmodels.FridgeViewModelFactory
import com.fridgefairy.android.ui.viewmodels.RecipeViewModel
import com.fridgefairy.android.ui.viewmodels.RecipeViewModelFactory
import com.fridgefairy.android.utils.SettingsHelper
import kotlinx.coroutines.launch
import java.util.Locale

class RecipesByIngredientsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecipesByIngredientsBinding
    private lateinit var recipeAdapter: RecipeAdapter


    private val fridgeViewModel: FridgeViewModel by viewModels {
        FridgeViewModelFactory(
            FoodRepository(
                FridgeFairyDatabase.getDatabase(this).foodDao()
            )
        )
    }


    private val recipeViewModel: RecipeViewModel by viewModels {
        RecipeViewModelFactory(
            RecipeRepository(
                FridgeFairyDatabase.getDatabase(this).recipeDao()
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipesByIngredientsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Recipes from Your Fridge"

        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
        loadFridgeIngredients()
    }


    override fun onResume() {
        super.onResume()
        updateDietChip()
    }


    private fun updateDietChip() {
        val diet = SettingsHelper.getDietPreference(this)
        if (diet != null) {
            binding.chipDietFilter.text = diet.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }
            binding.chipDietFilter.visibility = View.VISIBLE


            binding.chipDietFilter.setOnCloseIconClickListener {
                Toast.makeText(this, "To change your diet, go to Settings", Toast.LENGTH_LONG).show()
            }

        } else {
            binding.chipDietFilter.visibility = View.GONE
        }
    }


    // Set up RecyclerView for recipes
    private fun setupRecyclerView() {
        // Pass a click listener to the adapter
        recipeAdapter = RecipeAdapter { recipe ->
            // Launch RecipeDetailActivity with the recipe ID
            val intent = Intent(this, RecipeDetailActivity::class.java)
            intent.putExtra("RECIPE_ID", recipe.id)
            startActivity(intent)
        }

        binding.recyclerViewRecipes.apply {
            layoutManager = LinearLayoutManager(this@RecipesByIngredientsActivity)
            adapter = recipeAdapter
        }
    }

    // Load ingredients from fridge and update UI
    private fun loadFridgeIngredients() {
        fridgeViewModel.allFoodItems.observe(this) { foodItems ->
            if (foodItems.isEmpty()) {
                binding.textIngredientsPreview.text =
                    "No ingredients in your fridge. Add some items first!"
                binding.buttonFindRecipes.isEnabled = false
            } else {
                val ingredientNames = foodItems.map { it.name }
                binding.textIngredientsPreview.text =
                    "Ingredients: ${ingredientNames.joinToString(", ")}"
                binding.buttonFindRecipes.isEnabled = true
            }
        }
    }

    // Set up click listeners for buttons
    private fun setupClickListeners() {
        binding.buttonFindRecipes.setOnClickListener {
            lifecycleScope.launch {
                showLoading(true)

                val foodItems = fridgeViewModel.allFoodItems.value ?: emptyList()
                val ingredientNames = foodItems.map { it.name }

                if (ingredientNames.isEmpty()) {
                    Toast.makeText(
                        this@RecipesByIngredientsActivity,
                        "No ingredients in your fridge",
                        Toast.LENGTH_SHORT
                    ).show()
                    showLoading(false)
                    return@launch
                }

                val diet = SettingsHelper.getDietPreference(this@RecipesByIngredientsActivity)
                val intolerances = SettingsHelper.getIntolerances(this@RecipesByIngredientsActivity)

                recipeViewModel.findRecipesByIngredients(
                    ingredientNames,
                    BuildConfig.SPOONACULAR_API_KEY,
                    diet,
                    intolerances
                )
            }
        }
    }

    // Observe ViewModel data and update UI accordingly
    private fun observeViewModel() {
        recipeViewModel.ingredientBasedRecipes.observe(this) { recipes ->
            showLoading(false)

            if (recipes != null && recipes.isNotEmpty()) {
                binding.recyclerViewRecipes.visibility = View.VISIBLE
                binding.textEmptyState.visibility = View.GONE
                recipeAdapter.submitList(recipes)
            } else {
                binding.recyclerViewRecipes.visibility = View.GONE
                binding.textEmptyState.visibility = View.VISIBLE
                binding.textEmptyState.text = "No recipes found with your ingredients."
            }
        }

        recipeViewModel.errorMessage.observe(this) { error ->
            if (error != null) {
                showLoading(false)
                Toast.makeText(this, "Error: $error", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Show or hide loading indicator
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.buttonFindRecipes.isEnabled = !isLoading
    }

    // Handle toolbar back button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
// End of file: RecipesByIngredientsActivity.kt