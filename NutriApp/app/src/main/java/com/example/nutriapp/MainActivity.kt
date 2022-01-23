package com.example.nutriapp

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.*
import com.example.nutriapp.ui.theme.NutriAppTheme
import com.example.nutriapp.viewmodel.IngredientsViewModel
import com.example.nutriapp.viewmodel.IngredientViewModelFactory
import com.example.nutriapp.viewmodel.RecipeViewModelFactory
import com.example.nutriapp.viewmodel.RecipesViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @ExperimentalPermissionsApi
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ingredientsViewModel by viewModels<IngredientsViewModel> {
            IngredientViewModelFactory((this.applicationContext as NutriApplication).repository)
        }

        val recipesViewModel by viewModels<RecipesViewModel> {
            RecipeViewModelFactory((this.applicationContext as NutriApplication).repository)
        }

        setContent {
            NutriAppTheme {
                Navigation(ingredientsViewModel, recipesViewModel, this.applicationContext)
            }
        }
    }
}