package com.example.nutriapp.db

import android.database.sqlite.SQLiteException
import androidx.annotation.WorkerThread
import androidx.lifecycle.viewModelScope
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Transaction
import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class NutriRepository(
   private val nutriDatabase: NutriDatabase)
{
    private val ingredientDAO = nutriDatabase.ingredientDao()
    private val recipeDAO = nutriDatabase.recipeDao()
    private val recipeIngredientDAO = nutriDatabase.recipeIngredientDao()

    val allIngredients: Flow<List<Ingredient>> = ingredientDAO.getAll()
    val allRecipes: Flow<List<Recipe>> = recipeDAO.getAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(ingredient: Ingredient) {
        ingredientDAO.insert(ingredient)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(ingredient: Ingredient) {
        ingredientDAO.delete(ingredient)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(ingredient: Ingredient) {
        ingredientDAO.update(ingredient)
    }

    fun filter(prefix: String): Flow<List<Ingredient>> {
        return ingredientDAO.filter(prefix)
    }

    fun filter(id: Long): Flow<Ingredient> {
        return ingredientDAO.filter(id)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(recipe: Recipe) : Long {
        return recipeDAO.insert(recipe)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(recipe: Recipe) {
        recipeDAO.delete(recipe)
    }

    @WorkerThread
    fun getImage(id : Long): Flow<String> {
        return recipeDAO.getImage(id)
    }

    fun getRecipe(id: Long): Flow<Recipe> {
        return recipeDAO.getRecipe(id)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(recipeIngredient: RecipeIngredient) {
        recipeIngredientDAO.insert(recipeIngredient)
    }

    suspend fun insert(recipe: Recipe, ingredients: List<Pair<Long, String>>) {
        nutriDatabase.withTransaction {
            if (recipe.id != (0).toLong()) {
                delete(recipe)
            }
            val recipeId = insert(recipe)
            for ((ingredientId, quantity) in ingredients) {
                insert(RecipeIngredient(recipeId, ingredientId, quantity))
            }
        }
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(recipeIngredient: RecipeIngredient) {
        recipeIngredientDAO.delete(recipeIngredient)
    }

    fun getIngredientsForRecipe(recipeId: Long): Flow<List<RecipeIngredientDAO.IngredientWithQuantity>>
    {
        return recipeIngredientDAO.getIngredientsForRecipe(recipeId)
    }
}