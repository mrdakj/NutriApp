package com.example.nutriapp

import android.content.Context
import androidx.compose.foundation.*
import androidx.compose.runtime.Composable
import com.example.nutriapp.viewmodel.IngredientsViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import androidx.navigation.NavType
import androidx.navigation.compose.navArgument
import com.example.nutriapp.viewmodel.RecipesViewModel

@ExperimentalPermissionsApi
@ExperimentalFoundationApi
@Composable
fun Navigation(ingredientsViewModel : IngredientsViewModel,
               recipesViewModel: RecipesViewModel,
               applicationContext : Context)
{
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.HomeScreen.route
    ) {
        composable(route = Screen.HomeScreen.route) {
            HomeScreen(navController)
        }
        composable(route = Screen.IngredientsScreen.route) {
            IngredientsScreen(navController, ingredientsViewModel)
        }
        composable(
            route = Screen.NewIngredientScreen.withArgs("{updateIngredientId}"),
            arguments = listOf(
                navArgument("updateIngredientId") {
                    type = NavType.LongType
                }
            )
        ){
            NewIngredientScreen(navController, ingredientsViewModel, it.arguments?.getLong("updateIngredientId"))
        }
        composable(
            route = Screen.NewRecipeScreen.withArgs("{updateRecipeId}"),
            arguments = listOf(
                navArgument("updateRecipeId") {
                    type = NavType.LongType
                }
            )
        ) {
            NewRecipeScreen(navController, ingredientsViewModel, recipesViewModel, updateRecipeId = it.arguments?.getLong("updateRecipeId"))
        }
        composable(route = Screen.RecipesScreen.route) {
            RecipesScreen(navController, recipesViewModel)
        }
        composable(
            route = Screen.RecipePreviewScreen.withArgs("{recipeId}"),
            arguments = listOf(
                navArgument("recipeId") {
                    type = NavType.LongType
                }
            )
        ) {
            RecipePreviewScreen(recipesViewModel, navController, it.arguments?.getLong("recipeId")!!)
        }
        composable(
            route = Screen.SelectImageScreen.withArgs("{newRecipe}"),
            arguments = listOf(
                navArgument("newRecipe") {
                    type = NavType.BoolType
                }
            )
        ) {
            SelectImageScreen(applicationContext, navController, it.arguments?.getBoolean("newRecipe"))
        }
    }
}