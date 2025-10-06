package com.fridgefairy.android.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "recipes",
    indices = [Index(value = ["userId"])]
)
data class Recipe(
    @PrimaryKey
    val id: Int,
    val userId: String, // Associate with Firebase user ID
    val title: String,
    val image: String?,
    val readyInMinutes: Int,
    val servings: Int,
    val sourceUrl: String?,
    val summary: String,
    val instructions: String = "",
    val ingredients: List<Ingredient> = emptyList()
)

data class Ingredient(
    val id: Int,
    val name: String,
    val amount: Double,
    val unit: String
)