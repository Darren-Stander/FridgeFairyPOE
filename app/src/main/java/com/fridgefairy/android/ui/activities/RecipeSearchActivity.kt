// Start of file: RecipeSearchActivity.kt
// This activity allows users to search for recipes based on ingredients in their fridge.
// It uses a RecyclerView and RecipeAdapter to display results, and interacts with RecipeViewModel for data operations.
package com.fridgefairy.android.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.fridgefairy.android.BuildConfig
import com.fridgefairy.android.data.FridgeFairyDatabase
import com.fridgefairy.android.data.repository.RecipeRepository
import com.fridgefairy.android.databinding.ActivityRecipeSearchBinding
import com.fridgefairy.android.ui.adapters.RecipeAdapter
import com.fridgefairy.android.ui.viewmodels.RecipeViewModel
import com.fridgefairy.android.ui.viewmodels.RecipeViewModelFactory
import com.fridgefairy.android.utils.SettingsHelper
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale

class RecipeSearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecipeSearchBinding
    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var auth: FirebaseAuth
    private var currentUserId: String? = null

    // ViewModel for recipe data
    private val recipeViewModel: RecipeViewModel by viewModels {
        RecipeViewModelFactory(
            RecipeRepository(
                FridgeFairyDatabase.getDatabase(this).recipeDao()
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Recipe Search"

        auth = FirebaseAuth.getInstance()
        currentUserId = auth.currentUser?.uid

        if (currentUserId == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        recipeViewModel.setUserId(currentUserId!!)

        setupRecyclerView()
        setupSearchListener()
        observeViewModel()
    }

    // *** NEW: Add onResume to update the chip every time the screen is shown ***
    override fun onResume() {
        super.onResume()
        updateDietChip()
    }

    // *** NEW: Function to show/hide the diet filter chip ***
    private fun updateDietChip() {
        val diet = SettingsHelper.getDietPreference(this)
        if (diet != null) {
            binding.chipDietFilter.text = diet.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }
            binding.chipDietFilter.visibility = View.VISIBLE

            // Set a click listener to inform the user how to remove it
            binding.chipDietFilter.setOnCloseIconClickListener {
                Toast.makeText(this, "To change your diet, go to Settings", Toast.LENGTH_LONG).show()
            }

        } else {
            binding.chipDietFilter.visibility = View.GONE
        }
    }

    // Set up RecyclerView with adapter and layout manager
    private fun setupRecyclerView() {
        recipeAdapter = RecipeAdapter { recipe ->
            // Launch RecipeDetailActivity with the recipe ID
            val intent = Intent(this, RecipeDetailActivity::class.java)
            intent.putExtra("RECIPE_ID", recipe.id)
            startActivity(intent)
        }

        binding.recyclerViewRecipes.apply {
            layoutManager = LinearLayoutManager(this@RecipeSearchActivity)
            adapter = recipeAdapter
        }
    }

    // Set up listener for search button
    private fun setupSearchListener() {
        binding.buttonSearchRecipes.setOnClickListener {
            val query = binding.editTextSearchQuery.text.toString().trim()

            if (query.isNotBlank()) {
                showLoading(true)

                val diet = SettingsHelper.getDietPreference(this)
                val intolerances = SettingsHelper.getIntolerances(this)

                recipeViewModel.searchRecipes(
                    query = query,
                    apiKey = BuildConfig.SPOONACULAR_API_KEY,
                    diet = diet,
                    intolerances = intolerances
                )
            } else {
                Toast.makeText(this, "Please enter a search query", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Observe ViewModel LiveData for updates
    private fun observeViewModel() {
        recipeViewModel.searchResults.observe(this) { recipes ->
            showLoading(false)

            if (recipes != null && recipes.isNotEmpty()) {
                binding.recyclerViewRecipes.visibility = View.VISIBLE
                binding.textEmptyState.visibility = View.GONE
                recipeAdapter.submitList(recipes)
            } else {
                binding.recyclerViewRecipes.visibility = View.GONE
                binding.textEmptyState.visibility = View.VISIBLE
                binding.textEmptyState.text = "No recipes found. Try a different search."
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
        binding.buttonSearchRecipes.isEnabled = !isLoading
        binding.editTextSearchQuery.isEnabled = !isLoading
    }

    // Handle toolbar item selections
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
// End of file: RecipeSearchActivity.kt