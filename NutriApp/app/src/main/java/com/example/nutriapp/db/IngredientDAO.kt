package com.example.nutriapp.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface IngredientDAO {
    @Query("SELECT * FROM ingredient")
    fun getAll(): Flow<List<Ingredient>>

    @Insert
    suspend fun insert(ingredient: Ingredient)

    @Delete
    suspend fun delete(ingredient: Ingredient)

    @Update
    suspend fun update(ingredient: Ingredient)


    @Query("SELECT * FROM ingredient WHERE name LIKE :prefix || '%'")
    fun filter(prefix: String): Flow<List<Ingredient>>

    @Query("SELECT * FROM ingredient WHERE id=:id")
    fun filter(id: Long): Flow<Ingredient>
}

