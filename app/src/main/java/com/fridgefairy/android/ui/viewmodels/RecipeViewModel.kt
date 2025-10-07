// Start of file: RecipeViewModel.kt
// This ViewModel manages recipes for the current user.
// It provides LiveData for all recipes, search results, ingredient-based recipes, and error/loading states.
// It exposes methods to search for recipes and manage recipe data.
package com.fridgefairy.android.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fridgefairy.android.data.entities.Recipe
import com.fridgefairy.android.data.repository.RecipeRepository
import kotlinx.coroutines.launch

class RecipeViewModel(private val repository: RecipeRepository) : ViewModel() {

    // LiveData for the current user ID
    private val _userId = MutableLiveData<String>()
    val userId: LiveData<String> get() = _userId

    // Sets the user ID for filtering recipes
    fun setUserId(id: String) {
        _userId.value = id
    }

    // LiveData for all recipes
    val allRecipes: LiveData<List<Recipe>> = repository.allRecipes

    // LiveData for search results
    private val _searchResults = MutableLiveData<List<Recipe>>()
    val searchResults: LiveData<List<Recipe>> = _searchResults

    // LiveData for ingredient-based recipes
    private val _ingredientBasedRecipes = MutableLiveData<List<Recipe>>()
    val ingredientBasedRecipes: LiveData<List<Recipe>> = _ingredientBasedRecipes

    // LiveData for error messages
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    // LiveData for loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Searches for recipes by query
    fun searchRecipes(
        query: String,
        apiKey: String,
        diet: String? = null,
        intolerances: String? = null
    ) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            _errorMessage.postValue(null)

            try {
                val recipes = repository.searchRecipes(query, apiKey, diet, intolerances)

                if (recipes != null) {
                    _searchResults.postValue(recipes!!)
                } else {
                    _searchResults.postValue(emptyList())
                    _errorMessage.postValue("Failed to fetch recipes. Please check your connection.")
                }
            } catch (e: Exception) {
                _errorMessage.postValue(e.message)
                _searchResults.postValue(emptyList())
                _errorMessage.postValue("An error occurred: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun findRecipesByIngredients(ingredients: List<String>, apiKey: String) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            _errorMessage.postValue(null)
            try {
                val recipes = repository.findRecipesByIngredients(ingredients, apiKey)

                if (recipes != null) {
                    _ingredientBasedRecipes.postValue(recipes!!)
                } else {
                    _ingredientBasedRecipes.postValue(emptyList())
                    _errorMessage.postValue("Failed to fetch recipes. Please check your connection.")
                }
            } catch (e: Exception) {
                _errorMessage.postValue(e.message)
                _ingredientBasedRecipes.postValue(emptyList())
                _errorMessage.postValue("An error occurred: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun insert(recipe: Recipe) = viewModelScope.launch {
        repository.insert(recipe)
    }

    suspend fun getRecipeById(id: Int): Recipe? {
        return repository.getRecipeById(id)
    }
}

// Factory for creating RecipeViewModel instances
class RecipeViewModelFactory(private val repository: RecipeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecipeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
// End of file: RecipeViewModel.kt
