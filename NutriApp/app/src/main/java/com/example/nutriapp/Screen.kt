package com.example.nutriapp

sealed class Screen(val route : String) {
    object HomeScreen : Screen("home_screen")
    object IngredientsScreen : Screen("ingredients_screen")
    object NewIngredientScreen : Screen("new_ingredient_screen")
    object TakePictureScreen : Screen("take_picture_screen")
    object NewRecipeScreen : Screen("new_recipe_screen")
    object RecipesScreen : Screen("recipes_screen")
    object SelectImageScreen : Screen("select_image_screen")
    object RecipePreviewScreen : Screen("recipe_preview_screen")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}