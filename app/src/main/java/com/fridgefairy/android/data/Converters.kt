// File: com/fridgefairy/android/data/Converters.kt
package com.fridgefairy.android.data

import androidx.room.TypeConverter
import com.fridgefairy.android.data.entities.Ingredient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromIngredientList(ingredients: List<Ingredient>?): String? {
        return gson.toJson(ingredients)
    }

    @TypeConverter
    fun toIngredientList(ingredientsString: String?): List<Ingredient>? {
        if (ingredientsString == null) {
            return null
        }
        val listType = object : TypeToken<List<Ingredient>>() {}.type
        return gson.fromJson(ingredientsString, listType)
    }
}