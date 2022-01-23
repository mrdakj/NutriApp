package com.example.nutriapp.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    primaryKeys = ["recipeId", "ingredientId"],
    foreignKeys = [
        ForeignKey(
            entity = Recipe::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("recipeId"),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Ingredient::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("ingredientId"),
            onDelete = ForeignKey.RESTRICT
        ),
    ]
)
data class RecipeIngredient (
    @ColumnInfo val recipeId: Long,
    @ColumnInfo val ingredientId: Long,
    @ColumnInfo val quantity: String
)
