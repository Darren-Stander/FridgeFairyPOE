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

    private val _userId = MutableLiveData<String>()
    val userId: LiveData<String> get() = _userId

    fun setUserId(id: String) {
        _userId.value = id
    }


    val allFoodItems: LiveData<List<FoodItem>> = repository.getAllFoodItems()

    fun insert(foodItem: FoodItem) = viewModelScope.launch {
        repository.insert(foodItem)
    }

    fun update(foodItem: FoodItem) = viewModelScope.launch {
        repository.update(foodItem)
    }

    fun delete(foodItem: FoodItem) = viewModelScope.launch {
        repository.delete(foodItem)
    }
}


class FridgeViewModelFactory(private val repository: FoodRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FridgeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FridgeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}