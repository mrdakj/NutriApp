package com.example.nutriapp

import androidx.appcompat.widget.PopupMenu
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.navigation.NavController
import androidx.ui.tooling.preview.Preview
import coil.compose.rememberImagePainter
import com.example.nutriapp.db.Ingredient
import com.example.nutriapp.db.Recipe
import com.example.nutriapp.db.RecipeIngredientDAO
import com.example.nutriapp.util.AddButton
import com.example.nutriapp.viewmodel.IngredientsViewModel
import com.example.nutriapp.viewmodel.NewIngredientViewModel
import com.example.nutriapp.viewmodel.RecipesViewModel

@ExperimentalFoundationApi
@Composable
fun RecipesScreen(nav: NavController, recipesViewModel: RecipesViewModel)
{
    Surface(color = MaterialTheme.colors.background) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f)) {
                val recipes: List<Recipe> by recipesViewModel.allRecipes.observeAsState(listOf())
                IngredientGrid(recipes, recipesViewModel, nav)
            }

            Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                AddButton { nav.navigate(Screen.NewRecipeScreen.withArgs("0")) }
            }
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun IngredientGrid(recipes: List<Recipe>, recipesViewModel: RecipesViewModel, nav: NavController){
    LazyVerticalGrid(cells = GridCells.Adaptive(150.dp)) {
        items(recipes) { RecipeCard(it, recipesViewModel, nav) }
    }
}

@ExperimentalFoundationApi
@Composable
fun RecipeCard(recipe: Recipe, recipesViewModel: RecipesViewModel, nav: NavController) {
    Card(
        shape = RoundedCornerShape(3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .combinedClickable(
                onClick = {
                    nav.navigate(Screen.RecipePreviewScreen.withArgs(recipe.id.toString()))
                },
                onDoubleClick = {},
                onLongClick = { recipesViewModel.selectedRecipe = recipe },
            ),
        elevation = 10.dp
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (recipesViewModel.selectedRecipe?.id == recipe.id) {
                OutlinedButton(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(5.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Gray,
                    ),
                    onClick = {
                        recipesViewModel.selectedRecipe = null
                    },
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "close item",
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedButton(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(5.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Red,
                    ),
                    onClick = {
                        recipesViewModel.delete(recipe)
                        recipesViewModel.selectedRecipe = null
                    },
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "delete item",
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text("Delete")
                }

                OutlinedButton(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(5.dp),
                    onClick = {
                        recipesViewModel.selectedRecipe = null
                        nav.navigate(Screen.NewRecipeScreen.withArgs(recipe.id.toString()))
                    },
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "edit item",
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text("Edit")
                }

                Spacer(modifier = Modifier.height(13.dp))
            }
            else {
                Image(
                    modifier = Modifier
                        .size(150.dp, 150.dp)
                        .padding(10.dp),
                    painter = if (recipe.imageUri.isEmpty())
                                  painterResource(R.drawable.dish)
                              else
                                  rememberImagePainter(recipe.imageUri),
                    contentDescription = "recipe image"
                 )
                Text(text = recipe.name)
            }
        }
    }
}

