// This ViewModel manages food items in the fridge for the current user.
// It provides LiveData for all food items and exposes methods to insert, update, and delete items.
package com.fridgefairy.android.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.fridgefairy.android.data.entities.FoodItem
import com.fridgefairy.android.data.repository.FoodRepository
import kotlinx.coroutines.launch

class FridgeViewModel(private val repository: FoodRepository) : ViewModel() {

    // LiveData for the current user ID
    private val _userId = MutableLiveData<String>()
    val userId: LiveData<String> get() = _userId

    // Sets the user ID for filtering food items
    fun setUserId(id: String) {
        _userId.value = id
    }

    // LiveData for all food items
    val allFoodItems: LiveData<List<FoodItem>> = repository.getAllFoodItems()

    // Inserts a new food item
    fun insert(foodItem: FoodItem) = viewModelScope.launch {
        repository.insert(foodItem)
    }

    // Updates an existing food item
    fun update(foodItem: FoodItem) = viewModelScope.launch {
        repository.update(foodItem)
    }

    // Deletes a food item
    fun delete(foodItem: FoodItem) = viewModelScope.launch {
        repository.delete(foodItem)
    }
}

// Factory for creating FridgeViewModel instances
class FridgeViewModelFactory(private val repository: FoodRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FridgeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FridgeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}