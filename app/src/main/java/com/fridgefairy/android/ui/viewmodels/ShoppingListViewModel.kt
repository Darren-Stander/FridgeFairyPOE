// Start of file: ShoppingListViewModel.kt
// This ViewModel manages shopping list items for the current user.
// It provides LiveData for all items and unpurchased items, and exposes methods to insert, update, delete, toggle purchased, and clear purchased items.
package com.fridgefairy.android.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fridgefairy.android.data.entities.ShoppingListItem
import com.fridgefairy.android.data.repository.ShoppingListRepository
import kotlinx.coroutines.launch

class ShoppingListViewModel(private val repository: ShoppingListRepository) : ViewModel() {

    // LiveData for all shopping list items
    val allItems: LiveData<List<ShoppingListItem>> = repository.allItems
    // LiveData for unpurchased items
    val unpurchasedItems: LiveData<List<ShoppingListItem>> = repository.unpurchasedItems

    // Inserts a new shopping list item
    fun insert(item: ShoppingListItem) = viewModelScope.launch {
        repository.insert(item)
    }

    // Updates an existing shopping list item
    fun update(item: ShoppingListItem) = viewModelScope.launch {
        repository.update(item)
    }

    // Deletes a shopping list item
    fun delete(item: ShoppingListItem) = viewModelScope.launch {
        repository.delete(item)
    }

    // Toggles the purchased status of an item
    fun togglePurchased(item: ShoppingListItem) = viewModelScope.launch {
        val updatedItem = item.copy(isPurchased = !item.isPurchased)
        repository.update(updatedItem)
    }

    // Clears all purchased items from the list
    fun clearPurchasedItems() = viewModelScope.launch {
        repository.clearPurchasedItems()
    }
}

// Factory for creating ShoppingListViewModel instances
class ShoppingListViewModelFactory(private val repository: ShoppingListRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShoppingListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ShoppingListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
// End of file: ShoppingListViewModel.kt
