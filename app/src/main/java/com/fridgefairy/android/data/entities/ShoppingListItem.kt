package com.fridgefairy.android.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "shopping_list_items")
data class ShoppingListItem(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val quantity: Int = 1,
    val category: String = "General",
    val isPurchased: Boolean = false,
    val addedDate: Long = System.currentTimeMillis()
)