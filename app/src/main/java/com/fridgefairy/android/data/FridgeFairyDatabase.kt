// defines the main Room database class for the application.
// lists all entities (tables), sets the database version
// provides abstract methods to access each Data Access Object (DAO).


package com.fridgefairy.android.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fridgefairy.android.data.entities.FoodItem
import com.fridgefairy.android.data.entities.Recipe
import com.fridgefairy.android.data.entities.ShoppingListItem
import com.fridgefairy.android.data.dao.FoodDao
import com.fridgefairy.android.data.dao.RecipeDao
import com.fridgefairy.android.data.dao.ShoppingListDao

@Database(
    entities = [FoodItem::class, Recipe::class, ShoppingListItem::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FridgeFairyDatabase : RoomDatabase() {
    abstract fun foodDao(): FoodDao
    abstract fun recipeDao(): RecipeDao
    abstract fun shoppingListDao(): ShoppingListDao

    companion object {
        @Volatile
        private var INSTANCE: FridgeFairyDatabase? = null

        fun getDatabase(context: Context): FridgeFairyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FridgeFairyDatabase::class.java,
                    "fridge_fairy_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}