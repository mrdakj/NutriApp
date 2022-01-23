package com.example.nutriapp.db

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.nutriapp.EMPTY_IMAGE_URI
import java.lang.reflect.Constructor

@Entity
data class Ingredient(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "imageUri") val imageUri: String = "",
    @ColumnInfo val name: String,
    @ColumnInfo val calories: Int,
)
