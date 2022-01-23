package com.example.nutriapp

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.nutriapp.db.Ingredient
import com.example.nutriapp.util.AddButton
import com.example.nutriapp.util.DialogAlert
import com.example.nutriapp.util.Resource
import com.example.nutriapp.viewmodel.IngredientsViewModel
import com.example.nutriapp.viewmodel.NewRecipeViewModel

@ExperimentalFoundationApi
@Composable
fun IngredientsScreen(
    nav: NavController,
    ingredientsViewModel: IngredientsViewModel)
{
    Surface(color = MaterialTheme.colors.background) {
        checkIsDeleted(ingredientsViewModel)

        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f)) {
                val ingredients: List<Ingredient> by ingredientsViewModel.allIngredients.observeAsState(listOf())
                IngredientGrid(ingredients, ingredientsViewModel, nav)
            }

            Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                AddButton{ nav.navigate(Screen.NewIngredientScreen.withArgs("0")) }
            }
        }

        DialogAlert(ingredientsViewModel.showDialog, ingredientsViewModel.dialogText.value)
    }
}

@Composable
fun checkIsDeleted(ingredientsViewModel: IngredientsViewModel) {
    val isDeleted by ingredientsViewModel.isDeleted.observeAsState()

    when (isDeleted) {
        is Resource.Loading<Boolean> -> { }
        is Resource.Success<Boolean> -> {
            ingredientsViewModel.isDeleted.value = null
        }
        is Resource.Error<Boolean> -> {
            ingredientsViewModel.isDeleted.value = null
            ingredientsViewModel.showDialog.value = true
            ingredientsViewModel.setDialogText("delete error")
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun IngredientGrid(ingredients: List<Ingredient>, ingredientsViewModel: IngredientsViewModel, nav: NavController){
    LazyVerticalGrid(cells = GridCells.Adaptive(150.dp)) {
        items(ingredients) { IngredientCard(it, ingredientsViewModel, nav) }
    }
}

@ExperimentalFoundationApi
@Composable
fun IngredientCard(ingredient: Ingredient, ingredientsViewModel: IngredientsViewModel, nav: NavController) {
    Card(
        shape = RoundedCornerShape(3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .combinedClickable(
                onClick = {},
                onDoubleClick = {},
                onLongClick = { ingredientsViewModel.selectedIngredient = ingredient },
            ),
        elevation = 10.dp
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (ingredientsViewModel.selectedIngredient?.id == ingredient.id) {
                OutlinedButton(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(5.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Gray,
                    ),
                    onClick = {
                        ingredientsViewModel.selectedIngredient = null
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
                        ingredientsViewModel.delete(ingredient)
                        ingredientsViewModel.selectedIngredient = null
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
                        ingredientsViewModel.selectedIngredient = null
                        nav.navigate(Screen.NewIngredientScreen.withArgs(ingredient.id.toString()))
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
                    painter = if (ingredient.imageUri.isEmpty())
                                  painterResource(R.drawable.image)
                              else
                                  rememberImagePainter(ingredient.imageUri),
                    contentDescription = "ingredient image"
                )
                Text(text = ingredient.name)
            }
        }
    }
}
