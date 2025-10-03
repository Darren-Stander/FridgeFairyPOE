package com.fridgefairy.android.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fridgefairy.android.data.entities.ShoppingListItem
import com.fridgefairy.android.data.repository.ShoppingListRepository
import kotlinx.coroutines.launch

class ShoppingListViewModel(private val repository: ShoppingListRepository) : ViewModel() {

    val allItems: LiveData<List<ShoppingListItem>> = repository.allItems
    val unpurchasedItems: LiveData<List<ShoppingListItem>> = repository.unpurchasedItems

    fun insert(item: ShoppingListItem) = viewModelScope.launch {
        repository.insert(item)
    }

    fun update(item: ShoppingListItem) = viewModelScope.launch {
        repository.update(item)
    }

    fun delete(item: ShoppingListItem) = viewModelScope.launch {
        repository.delete(item)
    }

    fun togglePurchased(item: ShoppingListItem) = viewModelScope.launch {
        val updatedItem = item.copy(isPurchased = !item.isPurchased)
        repository.update(updatedItem)
    }

    fun clearPurchasedItems() = viewModelScope.launch {
        repository.clearPurchasedItems()
    }
}

class ShoppingListViewModelFactory(private val repository: ShoppingListRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShoppingListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ShoppingListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}