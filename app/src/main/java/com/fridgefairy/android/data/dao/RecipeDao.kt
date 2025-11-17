// defines the Data Access Object (DAO) for Recipe entities.
// includes Room annotations for database operations like insert and queries
// get all cached recipes or a specific recipe by its ID.


package com.fridgefairy.android.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.fridgefairy.android.data.entities.Recipe

@Dao
interface RecipeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recipe: Recipe)

    @Query("SELECT * FROM recipes")
    fun getAllRecipes(): LiveData<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE id = :id")
    suspend fun getRecipeById(id: Int): Recipe?
}
