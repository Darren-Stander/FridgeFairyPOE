package com.fridgefairy.android.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "food_items")
data class FoodItem(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val category: String,
    val expirationDate: Long,
    val quantity: Int,
    val storageLocation: String = "Fridge",
    val addedDate: Long = System.currentTimeMillis()
) {
    val daysUntilExpiration: Long
        get() = (expirationDate - System.currentTimeMillis()) / (1000L * 60L * 60L * 24L)

    val isExpired: Boolean
        get() = System.currentTimeMillis() > expirationDate

    val isExpiringSoon: Boolean
        get() = daysUntilExpiration in 1..3
}
