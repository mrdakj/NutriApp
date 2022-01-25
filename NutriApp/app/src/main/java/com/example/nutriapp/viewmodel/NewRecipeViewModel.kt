package com.example.nutriapp.viewmodel

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import com.example.nutriapp.db.Ingredient
import com.example.nutriapp.db.Recipe
import com.example.nutriapp.db.RecipeIngredientDAO
import com.example.nutriapp.util.DialogAlert

class NewRecipeViewModel() : ImageViewModel()  {
    var initDone = mutableStateOf(false)
    var showInputText = mutableStateOf(false)
    val recipeName = mutableStateOf("")
    val ingredientName = mutableStateOf("")
    val ingredientQuantity = mutableStateOf("")
    val selectedIngredientId = mutableStateOf((-1).toLong())
    var ingredientsList = mutableListOf<RecipeIngredientDAO.IngredientWithQuantity>()
    val showDialog = mutableStateOf(false)
    val dialogText = mutableStateOf("")
    var ingredientsCount = mutableStateOf(0)
    var selectedTabIndex by  mutableStateOf(0)
    val recipeSteps = mutableStateOf("")
    var showRecipeStepsInputText = mutableStateOf(false)
    var searchBoxClicked = mutableStateOf(false)

    var grabFocus = mutableStateOf(false)
    val focusRequester = mutableStateOf(FocusRequester())


    fun selectedTabIngredients(): Boolean {
        return selectedTabIndex == 0
    }

    fun getRecipe(updateRecipeId: Long): Recipe {
        return Recipe(
            updateRecipeId,
            imageUri = imagePath.value,
            name = recipeName.value,
            steps = recipeSteps.value
        )
    }

    fun getIngredientsIdsAndQuantities(): List<Pair<Long, String>> {
        return ingredientsList.map { ingredientWithQuantity ->
            Pair(
                ingredientWithQuantity.ingredient.id,
                ingredientWithQuantity.quantity
            )
        }
    }

    fun setDialogText(text: String) {
        dialogText.value = text
    }

    fun addIngredient(ingredient: RecipeIngredientDAO.IngredientWithQuantity) {
        ingredientsList.add(ingredient)
    }


    fun isIngredientSelected(): Boolean {
        return selectedIngredientId.value != (-1).toLong()
    }
}