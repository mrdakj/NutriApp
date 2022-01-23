package com.example.nutriapp.db

import android.database.sqlite.SQLiteException
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import androidx.room.Embedded




@Dao
interface RecipeIngredientDAO {
    @Query("SELECT b.*, a.quantity FROM RecipeIngredient a JOIN Ingredient b ON a.ingredientId = b.id WHERE a.recipeId = :recipeId")
    fun getIngredientsForRecipe(recipeId: Long): Flow<List<IngredientWithQuantity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    @Throws(SQLiteException::class)
    suspend fun insert(recipeIngredient: RecipeIngredient)

    @Delete
    suspend fun delete(recipeIngredient: RecipeIngredient)

    class IngredientWithQuantity(ingredient: Ingredient, quantity: String) {
        @Embedded
        var ingredient: Ingredient = ingredient

        var quantity: String = quantity
    }
}