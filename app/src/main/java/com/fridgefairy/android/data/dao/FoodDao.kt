package com.fridgefairy.android.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.fridgefairy.android.data.entities.FoodItem

@Dao
interface FoodDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(foodItem: FoodItem)

    @Update
    suspend fun update(foodItem: FoodItem)

    @Delete
    suspend fun delete(foodItem: FoodItem)

    @Query("SELECT * FROM food_items WHERE userId = :userId ORDER BY expirationDate ASC")
    fun getAllFoodItems(userId: String): LiveData<List<FoodItem>>

    @Query("SELECT * FROM food_items WHERE userId = :userId AND expirationDate < :timestamp")
    suspend fun getExpiredFoodItems(userId: String, timestamp: Long): List<FoodItem>

    @Query("SELECT * FROM food_items WHERE userId = :userId AND expirationDate BETWEEN :start AND :end")
    suspend fun getFoodItemsExpiringBetween(userId: String, start: Long, end: Long): List<FoodItem>
}

