package com.example.nutriapp.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDAO {
    @Query("SELECT * FROM Recipe")
    fun getAll(): Flow<List<Recipe>>

    @Query("SELECT imageUri FROM Recipe WHERE id = :id")
    fun getImage(id : Long): Flow<String>

    @Query("SELECT * FROM Recipe WHERE id=:id")
    fun getRecipe(id: Long): Flow<Recipe>

    @Insert
    suspend fun insert(recipe: Recipe) : Long

    @Delete
    suspend fun delete(recipe: Recipe)
}