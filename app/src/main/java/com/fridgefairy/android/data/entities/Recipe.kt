// File: com/fridgefairy/android/data/entities/Recipe.kt
package com.fridgefairy.android.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey
    val id: Int,
    val title: String,
    val image: String?,
    val readyInMinutes: Int,
    val servings: Int,
    val sourceUrl: String?,
    val summary: String,
    val instructions: String = "",
    // Added this field to store ingredients
    val ingredients: List<Ingredient> = emptyList()
)

data class Ingredient(
    val id: Int,
    val name: String,
    val amount: Double,
    val unit: String
)