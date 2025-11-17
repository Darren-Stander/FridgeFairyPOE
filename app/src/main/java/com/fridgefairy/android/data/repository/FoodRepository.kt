// defines the FoodRepository class, which abstracts the data source for food items.
// manages data operations between the FoodDao (local Room database)
// automatically scopes operations to the currently logged-in Firebase user.

package com.fridgefairy.android.data.repository

import androidx.lifecycle.LiveData
import com.fridgefairy.android.data.dao.FoodDao
import com.fridgefairy.android.data.entities.FoodItem
import com.google.firebase.auth.FirebaseAuth

class FoodRepository(private val foodDao: FoodDao) {

    private val currentUserId: String
        get() = FirebaseAuth.getInstance().currentUser?.uid ?: "unknown_user"

    fun getAllFoodItems(): LiveData<List<FoodItem>> {
        return foodDao.getAllFoodItems(currentUserId)
    }

    suspend fun insert(foodItem: FoodItem) {
        foodDao.insert(foodItem.copy(userId = currentUserId))
    }

    suspend fun update(foodItem: FoodItem) {
        foodDao.update(foodItem.copy(userId = currentUserId))
    }

    suspend fun delete(foodItem: FoodItem) {
        foodDao.delete(foodItem)
    }

    suspend fun getExpiringSoon(): List<FoodItem> {
        val now = System.currentTimeMillis()
        val threeDaysFromNow = now + (3L * 24L * 60L * 60L * 1000L)
        return foodDao.getFoodItemsExpiringBetween(currentUserId, now, threeDaysFromNow)
    }

    suspend fun getExpired(): List<FoodItem> {
        return foodDao.getExpiredFoodItems(currentUserId, System.currentTimeMillis())
    }

    // *** NEW FUNCTION ADDED HERE ***
    suspend fun getAllFoodItemsList(): List<FoodItem> {
        return foodDao.getAllFoodItemsList(currentUserId)
    }
}