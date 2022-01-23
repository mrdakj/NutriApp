package com.example.nutriapp.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.nutriapp.db.Ingredient


class NewIngredientViewModel : ImageViewModel() {
    var initDone = mutableStateOf(false)
    val ingredientName = mutableStateOf("")
    val showDialog = mutableStateOf(false)
    val dialogText = mutableStateOf("")
    val showNameInput = mutableStateOf(false)

    fun setDialogText(text: String) {
        dialogText.value = text
    }

    fun getIngredientModel(id: Long = 0):Ingredient? {
         if (ingredientName.value.isNotEmpty()) {
            return Ingredient(
                id = id,
                name = ingredientName.value,
                imageUri = super.imagePath.value,
                calories = 100
            )
        }
        return null
    }
}