package com.fridgefairy.android.data.api

import com.fridgefairy.android.data.entities.Recipe
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RecipeApiService {
    @GET("recipes/complexSearch")
    suspend fun searchRecipes(
        @Query("query") query: String,
        @Query("apiKey") apiKey: String,
        @Query("number") number: Int = 20,
        @Query("addRecipeInformation") addInfo: Boolean = true,
        @Query("fillIngredients") fillIngredients: Boolean = true,
        @Query("diet") diet: String? = null,
        @Query("intolerances") intolerances: String? = null
    ): Response<RecipeSearchResponse>

    @GET("recipes/{id}/information")
    suspend fun getRecipeDetails(
        @Path("id") recipeId: Int,
        @Query("apiKey") apiKey: String,
        @Query("includeNutrition") includeNutrition: Boolean = false
    ): Response<RecipeDetailResponse>

    @GET("recipes/findByIngredients")
    suspend fun findRecipesByIngredients(
        @Query("ingredients") ingredients: String,
        @Query("apiKey") apiKey: String,
        @Query("number") number: Int = 20,
        @Query("ranking") ranking: Int = 2,
        @Query("ignorePantry") ignorePantry: Boolean = true
    ): Response<List<RecipeByIngredientsResult>>
}

data class RecipeSearchResponse(
    val results: List<RecipeResult>,
    val offset: Int,
    val number: Int,
    val totalResults: Int
)

data class RecipeResult(
    val id: Int,
    val title: String,
    val image: String?,
    val imageType: String?
)

data class RecipeDetailResponse(
    val id: Int,
    val title: String,
    val image: String?,
    val readyInMinutes: Int,
    val servings: Int,
    val sourceUrl: String?,
    val summary: String,
    val instructions: String?,
    val extendedIngredients: List<ExtendedIngredient>?
)

data class ExtendedIngredient(
    val id: Int,
    val name: String,
    val amount: Double,
    val unit: String,
    val original: String
)

data class RecipeByIngredientsResult(
    val id: Int,
    val title: String,
    val image: String?,
    val usedIngredientCount: Int,
    val missedIngredientCount: Int
)