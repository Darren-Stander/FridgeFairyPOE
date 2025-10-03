package com.fridgefairy.android.ui.activities

import android.content.Context
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

class RecipeSearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecipeSearchBinding
    private lateinit var recipeAdapter: RecipeAdapter

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

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Recipe Search"

        setupRecyclerView()
        setupSearchListener()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        recipeAdapter = RecipeAdapter()

        binding.recyclerViewRecipes.apply {
            layoutManager = LinearLayoutManager(this@RecipeSearchActivity)
            adapter = recipeAdapter
        }
    }

    private fun setupSearchListener() {
        binding.buttonSearchRecipes.setOnClickListener {
            val query = binding.editTextSearchQuery.text.toString().trim()

            if (query.isNotBlank()) {
                showLoading(true)

                // Get dietary preferences from settings
                val diet = getDietPreference()
                val intolerances = getIntolerances()

                recipeViewModel.searchRecipes(
                    query,
                    BuildConfig.SPOONACULAR_API_KEY,
                    diet,
                    intolerances
                )
            } else {
                Toast.makeText(this, "Please enter a search query", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getDietPreference(): String? {
        val prefs = getSharedPreferences("FridgeFairyPrefs", Context.MODE_PRIVATE)
        val dietIndex = prefs.getInt("diet_preference", 0)
        return when (dietIndex) {
            1 -> "vegetarian"
            2 -> "vegan"
            3 -> "ketogenic"
            4 -> "paleo"
            else -> null
        }
    }

    private fun getIntolerances(): String? {
        val prefs = getSharedPreferences("FridgeFairyPrefs", Context.MODE_PRIVATE)
        val intolerances = prefs.getString("intolerances", "")
        return if (intolerances.isNullOrBlank()) null else intolerances
    }

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

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.buttonSearchRecipes.isEnabled = !isLoading
        binding.editTextSearchQuery.isEnabled = !isLoading
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}