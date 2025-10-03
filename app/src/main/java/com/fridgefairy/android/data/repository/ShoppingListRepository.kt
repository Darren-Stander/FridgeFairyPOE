package com.fridgefairy.android.data.repository

import androidx.lifecycle.LiveData
import com.fridgefairy.android.data.dao.ShoppingListDao
import com.fridgefairy.android.data.entities.ShoppingListItem

class ShoppingListRepository(private val shoppingListDao: ShoppingListDao) {

    val allItems: LiveData<List<ShoppingListItem>> = shoppingListDao.getAllItems()
    val unpurchasedItems: LiveData<List<ShoppingListItem>> = shoppingListDao.getUnpurchasedItems()

    suspend fun insert(item: ShoppingListItem) {
        shoppingListDao.insert(item)
    }

    suspend fun update(item: ShoppingListItem) {
        shoppingListDao.update(item)
    }

    suspend fun delete(item: ShoppingListItem) {
        shoppingListDao.delete(item)
    }

    suspend fun clearPurchasedItems() {
        shoppingListDao.clearPurchasedItems()
    }
}