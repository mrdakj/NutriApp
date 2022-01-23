package com.example.nutriapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class RecipePreviewViewModel : ViewModel() {
    var selectedTabIndex by  mutableStateOf(0)

    fun selectedTabIngredients(): Boolean {
        return selectedTabIndex == 0
    }
}