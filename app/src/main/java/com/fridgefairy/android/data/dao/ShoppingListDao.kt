// defines the Data Access Object (DAO) for ShoppingListItem entities.
// includes Room annotations for database operations like insert, update, delete
// queries to get all items or clear items marked as purchased.

package com.fridgefairy.android.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.fridgefairy.android.data.entities.ShoppingListItem

@Dao
interface ShoppingListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ShoppingListItem)

    @Update
    suspend fun update(item: ShoppingListItem)

    @Delete
    suspend fun delete(item: ShoppingListItem)

    @Query("SELECT * FROM shopping_list_items ORDER BY isPurchased ASC, addedDate DESC")
    fun getAllItems(): LiveData<List<ShoppingListItem>>

    @Query("SELECT * FROM shopping_list_items WHERE isPurchased = 0")
    fun getUnpurchasedItems(): LiveData<List<ShoppingListItem>>

    @Query("DELETE FROM shopping_list_items WHERE isPurchased = 1")
    suspend fun clearPurchasedItems()
}