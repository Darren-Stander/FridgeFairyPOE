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

    private val _userId = MutableLiveData<String>()
    val userId: LiveData<String> get() = _userId

    fun setUserId(id: String) {
        _userId.value = id
    }

    val allRecipes: LiveData<List<Recipe>> = repository.allRecipes

    private val _searchResults = MutableLiveData<List<Recipe>>()
    val searchResults: LiveData<List<Recipe>> = _searchResults

    private val _ingredientBasedRecipes = MutableLiveData<List<Recipe>>()
    val ingredientBasedRecipes: LiveData<List<Recipe>> = _ingredientBasedRecipes

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

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

class RecipeViewModelFactory(private val repository: RecipeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecipeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}