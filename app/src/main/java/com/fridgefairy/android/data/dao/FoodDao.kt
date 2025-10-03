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

    @Query("SELECT * FROM food_items ORDER BY expirationDate ASC")
    fun getAllFoodItems(): LiveData<List<FoodItem>>

    @Query("SELECT * FROM food_items WHERE expirationDate < :timestamp")
    suspend fun getExpiredFoodItems(timestamp: Long): List<FoodItem>

    @Query("SELECT * FROM food_items WHERE id = :id")
    suspend fun getFoodItemById(id: String): FoodItem?

    @Query("SELECT * FROM food_items WHERE expirationDate BETWEEN :start AND :end")
    suspend fun getFoodItemsExpiringBetween(start: Long, end: Long): List<FoodItem>
}
