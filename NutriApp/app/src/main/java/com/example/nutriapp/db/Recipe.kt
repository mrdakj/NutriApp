package com.example.nutriapp.db

import androidx.room.*


@Entity
data class Recipe(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "imageUri") val imageUri: String = "",
    @ColumnInfo val name: String,
    @ColumnInfo val steps: String
)
