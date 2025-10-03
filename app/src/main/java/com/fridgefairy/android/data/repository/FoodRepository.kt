package com.fridgefairy.android.data.repository

import androidx.lifecycle.LiveData
import com.fridgefairy.android.data.dao.FoodDao
import com.fridgefairy.android.data.entities.FoodItem

class FoodRepository(private val foodDao: FoodDao) {

    val allFoodItems: LiveData<List<FoodItem>> = foodDao.getAllFoodItems()

    suspend fun insert(foodItem: FoodItem) {
        foodDao.insert(foodItem)
    }

    suspend fun update(foodItem: FoodItem) {
        foodDao.update(foodItem)
    }

    suspend fun delete(foodItem: FoodItem) {
        foodDao.delete(foodItem)
    }

    suspend fun getExpiringSoon(): List<FoodItem> {
        val threeDaysFromNow = System.currentTimeMillis() + (3L * 24L * 60L * 60L * 1000L)
        return foodDao.getFoodItemsExpiringBetween(System.currentTimeMillis(), threeDaysFromNow)
    }

    suspend fun getExpired(): List<FoodItem> {
        return foodDao.getExpiredFoodItems(System.currentTimeMillis())
    }
}
