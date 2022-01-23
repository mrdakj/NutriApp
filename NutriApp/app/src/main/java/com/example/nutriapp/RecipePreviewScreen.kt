package com.example.nutriapp

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState

import androidx.ui.layout.Column
import com.example.nutriapp.db.RecipeIngredientDAO
import com.example.nutriapp.viewmodel.RecipesViewModel

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.nutriapp.db.Ingredient
import com.example.nutriapp.db.Recipe
import com.example.nutriapp.util.*
import com.example.nutriapp.util.ClickableNameField
import com.example.nutriapp.viewmodel.IngredientsViewModel
import com.example.nutriapp.viewmodel.NewRecipeViewModel
import com.example.nutriapp.viewmodel.RecipePreviewViewModel

@ExperimentalFoundationApi
@Composable
fun RecipePreviewScreen(
    recipesViewModel: RecipesViewModel,
    nav: NavController,
    recipeId: Long,
    recipePreviewViewModel: RecipePreviewViewModel = viewModel())
{
    val recipe: Recipe? by recipesViewModel.getRecipe(recipeId).observeAsState()
    if (recipe == null) {
        return
    }

    val ingredients: List<RecipeIngredientDAO.IngredientWithQuantity>? by recipesViewModel.getIngredientsForRecipe(recipeId).observeAsState()
    if (ingredients == null) {
        return
    }

    Surface(color = MaterialTheme.colors.background) {
        Card(
            shape = RoundedCornerShape(3.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            elevation = 10.dp
        ) {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.background(Cream)
            ) {
                stickyHeader {
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .background(Cream)) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Box(modifier = Modifier.align(Alignment.Center)) {
                                NameField(recipe!!.name)
                            }
                        }

                        Box(modifier = Modifier.fillMaxWidth()) {
                            Box(modifier = Modifier.align(Alignment.Center)) {
                                RecipeImage(recipe!!.imageUri)
                            }
                        }

                        Tabs(recipePreviewViewModel)
                    }
                }

                items(1) {
                    if (recipePreviewViewModel.selectedTabIngredients()) {
                        ShowIngredientsWithQuantities(ingredients!!)
                    }
                    else {
                        StepsText(recipe!!)
                    }
                } // items end
            }
        }
    }
}

@Composable
fun StepsText(recipe: Recipe)
{
    Text(
        text = recipe.steps,
        modifier = Modifier
            .padding(15.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Gray)
            .fillMaxWidth()
            .padding(10.dp),
    )
}


@Composable
fun ShowIngredientsWithQuantities(ingredientsPair : List<RecipeIngredientDAO.IngredientWithQuantity>)
{
    for (ingredientPair in ingredientsPair) {
        ShowIngredientWithQuantity(ingredientPair)
    }
}

@Composable
fun Tabs(recipePreviewViewModel: RecipePreviewViewModel)
{
    TabRow(
        selectedTabIndex = recipePreviewViewModel.selectedTabIndex,
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color.White
    ) {
        listOf("ingredients", "steps").forEachIndexed { i, text ->
            Tab(
                selected = recipePreviewViewModel.selectedTabIndex == i,
                onClick = { recipePreviewViewModel.selectedTabIndex = i },
                modifier = Modifier.height(50.dp),
                text = { Text(text) }
            )
        }
    }
}

@Composable
fun RecipeImage(imageUri: String)
{
    Image(
        modifier = Modifier
            .size(200.dp, 200.dp)
            .padding(5.dp),
        painter = if (imageUri.isEmpty())
                      painterResource(R.drawable.add_image)
                  else
                     rememberImagePainter(imageUri),
        contentDescription = "recipe image",
    )
}

@Composable
fun ShowIngredientWithQuantity(ingredientPair: RecipeIngredientDAO.IngredientWithQuantity)
{
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(10.dp)
    ) {
        Image(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
            ,
            painter = if (ingredientPair.ingredient.imageUri.isEmpty())
                          painterResource(R.drawable.image)
                      else
                          rememberImagePainter(ingredientPair.ingredient.imageUri),
            contentDescription = "ingredient image",
            contentScale = ContentScale.Crop,
        )

        Spacer(modifier = Modifier.width(10.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Text(ingredientPair.ingredient.name, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            Text(ingredientPair.quantity)
        }
    }
}