package com.fridgefairy.android.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.fridgefairy.android.data.dao.RecipeDao
import com.fridgefairy.android.data.entities.Recipe
import com.fridgefairy.android.data.entities.Ingredient
import com.fridgefairy.android.data.api.RecipeApiService
import com.fridgefairy.android.data.api.RetrofitClient
import com.google.firebase.auth.FirebaseAuth

class RecipeRepository(private val recipeDao: RecipeDao) {

    private val recipeApiService: RecipeApiService = RetrofitClient.recipeApiService

    // 👇 ADD THIS PROPERTY TO FETCH CURRENT USER ID
    private val currentUserId: String
        get() = FirebaseAuth.getInstance().currentUser?.uid ?: "unknown_user"

    // Fetch all recipes for this user (you can modify DAO if you want per-user filtering)
    val allRecipes: LiveData<List<Recipe>> = recipeDao.getAllRecipes()

    suspend fun insert(recipe: Recipe) {
        // Always attach current user ID before inserting
        recipeDao.insert(recipe.copy(userId = currentUserId))
    }

    suspend fun searchRecipes(
        query: String,
        apiKey: String,
        diet: String? = null,
        intolerances: String? = null
    ): List<Recipe>? {
        return try {
            val response = recipeApiService.searchRecipes(
                query = query,
                apiKey = apiKey,
                diet = diet,
                intolerances = intolerances
            )

            if (response.isSuccessful && response.body() != null) {
                val searchResults = response.body()!!.results
                val recipes = mutableListOf<Recipe>()

                searchResults.forEach { result ->
                    try {
                        val detailResponse = recipeApiService.getRecipeDetails(result.id, apiKey)
                        if (detailResponse.isSuccessful && detailResponse.body() != null) {
                            val detail = detailResponse.body()!!

                            val ingredients = detail.extendedIngredients?.map { ext ->
                                Ingredient(
                                    id = ext.id,
                                    name = ext.name,
                                    amount = ext.amount,
                                    unit = ext.unit
                                )
                            } ?: emptyList()

                            val recipe = Recipe(
                                id = detail.id,
                                userId = currentUserId, // 👈 ADDED HERE
                                title = detail.title,
                                image = detail.image,
                                readyInMinutes = detail.readyInMinutes,
                                servings = detail.servings,
                                sourceUrl = detail.sourceUrl,
                                summary = detail.summary,
                                instructions = detail.instructions ?: "",
                                ingredients = ingredients
                            )

                            recipes.add(recipe)
                            recipeDao.insert(recipe)
                        }
                    } catch (e: Exception) {
                        Log.e("RecipeRepository", "Error fetching details for recipe ${result.id}", e)
                    }
                }

                recipes
            } else {
                Log.e("RecipeRepository", "API Error: ${response.code()} - ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("RecipeRepository", "Network error fetching recipes", e)
            null
        }
    }

    suspend fun findRecipesByIngredients(ingredients: List<String>, apiKey: String): List<Recipe>? {
        return try {
            val ingredientsString = ingredients.joinToString(",")
            val response = recipeApiService.findRecipesByIngredients(ingredientsString, apiKey)

            if (response.isSuccessful && response.body() != null) {
                val results = response.body()!!
                val recipes = mutableListOf<Recipe>()

                results.forEach { result ->
                    try {
                        val detailResponse = recipeApiService.getRecipeDetails(result.id, apiKey)
                        if (detailResponse.isSuccessful && detailResponse.body() != null) {
                            val detail = detailResponse.body()!!

                            val ingredientsList = detail.extendedIngredients?.map { ext ->
                                Ingredient(
                                    id = ext.id,
                                    name = ext.name,
                                    amount = ext.amount,
                                    unit = ext.unit
                                )
                            } ?: emptyList()

                            val recipe = Recipe(
                                id = detail.id,
                                userId = currentUserId, // 👈 ADDED HERE TOO
                                title = detail.title,
                                image = detail.image,
                                readyInMinutes = detail.readyInMinutes,
                                servings = detail.servings,
                                sourceUrl = detail.sourceUrl,
                                summary = detail.summary,
                                instructions = detail.instructions ?: "",
                                ingredients = ingredientsList
                            )

                            recipes.add(recipe)
                            recipeDao.insert(recipe)
                        }
                    } catch (e: Exception) {
                        Log.e("RecipeRepository", "Error fetching details for recipe ${result.id}", e)
                    }
                }

                recipes
            } else {
                Log.e("RecipeRepository", "API Error: ${response.code()} - ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("RecipeRepository", "Network error finding recipes by ingredients", e)
            null
        }
    }

    suspend fun getRecipeById(id: Int): Recipe? {
        return recipeDao.getRecipeById(id)
    }
}
