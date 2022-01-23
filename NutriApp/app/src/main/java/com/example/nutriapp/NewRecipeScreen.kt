package com.example.nutriapp

import android.content.res.Configuration
import android.graphics.Paint
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.nutriapp.db.Ingredient
import com.example.nutriapp.db.Recipe
import com.example.nutriapp.db.RecipeIngredientDAO
import com.example.nutriapp.util.*
import com.example.nutriapp.viewmodel.IngredientsViewModel
import com.example.nutriapp.viewmodel.NewRecipeViewModel
import com.example.nutriapp.viewmodel.RecipesViewModel

@ExperimentalFoundationApi
@Composable
fun NewRecipeScreen(
    nav: NavController,
    ingredientsViewModel: IngredientsViewModel,
    recipesViewModel: RecipesViewModel,
    updateRecipeId: Long?,
    newRecipeViewModel: NewRecipeViewModel = viewModel())
{
    val focusManager = LocalFocusManager.current

    LoadDataForUpdate(updateRecipeId, recipesViewModel, newRecipeViewModel)

    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(onTap = {
                focusManager.clearFocus()
                newRecipeViewModel.showInputText.value = false
            })
        }
    ) {
        CheckInserted(recipesViewModel, newRecipeViewModel, nav)

        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f))
            {
                RecipeCard(newRecipeViewModel, ingredientsViewModel, nav)
            }

            Box(Modifier.align(Alignment.CenterHorizontally))
            {
                DoneButton {
                    recipesViewModel.insert(
                        newRecipeViewModel.getRecipe(updateRecipeId!!),
                        newRecipeViewModel.getIngredientsIdsAndQuantities())
                }
            }
        }

        DialogAlert(newRecipeViewModel.showDialog, newRecipeViewModel.dialogText.value)
    }
}

@Composable
fun CheckInserted(recipesViewModel: RecipesViewModel, newRecipeViewModel: NewRecipeViewModel, nav: NavController) {
    val isInserted by recipesViewModel.isInserted.observeAsState()

    when (isInserted) {
        is Resource.Loading<Boolean> -> { }
        is Resource.Success<Boolean> -> {
            recipesViewModel.isInserted.value = null
            nav.popBackStack()
        }
        is Resource.Error<Boolean> -> {
            recipesViewModel.isInserted.value = null
            newRecipeViewModel.showDialog.value = true
            newRecipeViewModel.setDialogText("Insert failed")
        }
    }
}

@Composable
fun LoadDataForUpdate(updateRecipeId: Long?, recipesViewModel: RecipesViewModel, newRecipeViewModel: NewRecipeViewModel) {
    if (updateRecipeId != null && updateRecipeId > (0).toLong() && !newRecipeViewModel.initDone.value) {
        val ingredients: List<RecipeIngredientDAO.IngredientWithQuantity>? by recipesViewModel.getIngredientsForRecipe(updateRecipeId!!).observeAsState()
        if (ingredients != null) {
            newRecipeViewModel.ingredientsList = ingredients!!.toMutableList()
            newRecipeViewModel.ingredientsCount.value = newRecipeViewModel.ingredientsList.size
            val recipe: Recipe? by recipesViewModel.getRecipe(updateRecipeId!!).observeAsState()
            if (recipe != null) {
                newRecipeViewModel.imagePath.value = recipe!!.imageUri
                newRecipeViewModel.recipeName.value = recipe!!.name
                newRecipeViewModel.recipeSteps.value = recipe!!.steps
                newRecipeViewModel.initDone.value = true
            }
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun ScrollableRowIngredients(ingredientsViewModel: IngredientsViewModel, newRecipeViewModel: NewRecipeViewModel){
    val ingredients: List<Ingredient> by
    if (newRecipeViewModel.ingredientName.value.isNotEmpty())
        ingredientsViewModel.filter(newRecipeViewModel.ingredientName.value).observeAsState(listOf())
    else
        ingredientsViewModel.allIngredients.observeAsState(listOf())

    LazyRow(modifier = Modifier.fillMaxWidth())
    {
        items(ingredients) { ClickableIngredientCard(it, newRecipeViewModel) }
    }
}

@Composable
fun ClickableIngredientCard(ingredient: Ingredient, newRecipeViewModel: NewRecipeViewModel) {
    val focusManager = LocalFocusManager.current

    Card(
        shape = RoundedCornerShape(3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .clickable { },
        elevation = 10.dp
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier
                    .size(80.dp, 80.dp)
                    .clickable(
                        enabled = true,
                        onClickLabel = "Clickable image",
                        onClick = {
                            newRecipeViewModel.selectedIngredientId.value = ingredient.id
                            focusManager.clearFocus()
                        }
                    ),
                painter = if (ingredient.imageUri.isEmpty()) painterResource(R.drawable.image) else rememberImagePainter(ingredient.imageUri),
                contentDescription = "ingredient image",
            )
            Text(text = ingredient.name + " " + ingredient.id)
        }
    }
}



@Composable
fun RecipeImage(newRecipeViewModel: NewRecipeViewModel, nav: NavController)
{
    Image(
        modifier = Modifier
            .size(100.dp, 100.dp)
            .padding(5.dp)
            .clickable(
                enabled = true,
                onClickLabel = "Clickable image",
                onClick = {
                    nav.navigate(Screen.SelectImageScreen.withArgs("true"))
                }
            ),
        painter = if (newRecipeViewModel.imagePath.value.isEmpty())
            painterResource(R.drawable.add_image)
        else
            rememberImagePainter(newRecipeViewModel.imagePath.value),
        contentDescription = "ingredient image",
    )
}

@Composable
fun Tabs(newRecipeViewModel: NewRecipeViewModel)
{
    TabRow(
        selectedTabIndex = newRecipeViewModel.selectedTabIndex,
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color.White
    ) {
        listOf("ingredients", "steps").forEachIndexed { i, text ->
            Tab(
                selected = newRecipeViewModel.selectedTabIndex == i,
                onClick = { newRecipeViewModel.selectedTabIndex = i },
                modifier = Modifier.height(50.dp),
                text = { Text(text) }
            )
        }
    }
}

@Composable
fun StepsText(newRecipeViewModel: NewRecipeViewModel)
{
    Text(
        text = if (newRecipeViewModel.recipeSteps.value.isEmpty())
            "click to enter recipe steps"
        else
            newRecipeViewModel.recipeSteps.value,
        modifier = Modifier
            .padding(15.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Gray)
            .fillMaxWidth()
            .clickable(
                enabled = true,
                role = Role.Button
            ) {
                newRecipeViewModel.showRecipeStepsInputText.value = true
            }
            .padding(10.dp),
    )
}

@Composable
fun ShowStepsField(newRecipeViewModel: NewRecipeViewModel)
{
    if (newRecipeViewModel.showRecipeStepsInputText.value) {
        RecipeTextField(newRecipeViewModel)
    }
    else {
        StepsText(newRecipeViewModel)
    }
}

@Composable
fun ShowIngredientsWithQuantities(newRecipeViewModel: NewRecipeViewModel)
{
    for (ingredientWithQuantity in newRecipeViewModel.ingredientsList) {
        Box(modifier = Modifier.padding(10.dp)) {
            IngredientSmallCard(newRecipeViewModel, ingredientWithQuantity)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            "ingredients: " + newRecipeViewModel.ingredientsCount.value.toString(),
            modifier = Modifier.align(Alignment.CenterStart)
        )
    }
}

@ExperimentalFoundationApi
@Composable
fun ShowIngredientsWithSearch(
    newRecipeViewModel: NewRecipeViewModel,
    ingredientsViewModel: IngredientsViewModel)
{
    Box(modifier = Modifier
        .fillMaxWidth()
        .background(Cream)) {
        SearchView(newRecipeViewModel.ingredientName)
    }

    Box(modifier = Modifier
        .fillMaxWidth()
        .defaultMinSize(0.dp, 150.dp)
        .background(Cream)) {
        IngredientsList(newRecipeViewModel, ingredientsViewModel)
    }
}

@Composable
fun StepsOkCancelButtons(newRecipeViewModel: NewRecipeViewModel)
{
    if (newRecipeViewModel.showRecipeStepsInputText.value) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .background(Cream)
            .padding(5.dp)) {
            OkCancelButtons(
                okFun = {
                    newRecipeViewModel.showRecipeStepsInputText.value = false
                },
                cancelFun = {
                    newRecipeViewModel.recipeSteps.value = ""
                    newRecipeViewModel.showRecipeStepsInputText.value = false
                }
            )
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun RecipeCardDefault(
    newRecipeViewModel: NewRecipeViewModel,
    ingredientsViewModel: IngredientsViewModel,
    nav: NavController)
{
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.background(Cream)
    ) {
        stickyHeader {
            Column(modifier = Modifier
                .fillMaxWidth()
                .background(Cream)
            ) {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 75.dp)) {
                    Box(Modifier.align(Alignment.Center)) {
                        ClickableNameField(newRecipeViewModel.recipeName, newRecipeViewModel.showInputText)
                    }
                }

                Box(modifier = Modifier.fillMaxWidth()) {
                    Box(Modifier.align(Alignment.Center)) {
                        RecipeImage(newRecipeViewModel, nav)
                    }
                }

                Tabs(newRecipeViewModel)

                if (newRecipeViewModel.selectedTabIngredients()) {
                    ShowIngredientsWithSearch(newRecipeViewModel, ingredientsViewModel)
                }
                else {
                    StepsOkCancelButtons(newRecipeViewModel)
                }
            }
        }

        items(1) {
            if (newRecipeViewModel.selectedTabIngredients()) {
                ShowIngredientsWithQuantities(newRecipeViewModel)
            }
            else {
                ShowStepsField(newRecipeViewModel)
            }
        } // items end
    } // lazy list end
}

@ExperimentalFoundationApi
@Composable
fun RecipeCardLandscape(
    newRecipeViewModel: NewRecipeViewModel,
    ingredientsViewModel: IngredientsViewModel,
    nav: NavController)
{
    Row(modifier = Modifier
        .fillMaxSize()
        .background(Cream)) {
        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.background(Cream)
            ) {
                stickyHeader {
                    Column(modifier = Modifier
                        .background(Cream)
                        .fillMaxWidth()) {
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 75.dp)) {
                            Box(modifier = Modifier.align(Alignment.Center)) {
                                ClickableNameField(newRecipeViewModel.recipeName, newRecipeViewModel.showInputText)
                            }
                        }

                        Box(modifier = Modifier.fillMaxWidth()) {
                            Box(modifier = Modifier.align(Alignment.Center)) {
                                RecipeImage(newRecipeViewModel, nav)
                            }
                        }
                    }
                }

                items(1) {
                    ShowIngredientsWithQuantities(newRecipeViewModel)
                }
            } // lazy column end
        } // Box end

        Box(modifier = Modifier.weight(1f)) {
            Column {
                Tabs(newRecipeViewModel)

                if (newRecipeViewModel.selectedTabIngredients()) {
                    ShowIngredientsWithSearch(newRecipeViewModel, ingredientsViewModel)
                }
                else {
                    LazyColumn(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.background(Cream)) {
                        stickyHeader {
                            StepsOkCancelButtons(newRecipeViewModel)
                        }

                        items(1) {
                            ShowStepsField(newRecipeViewModel)
                        }
                    } // lazy column
                }
            } // Column end
        } // Box end
    } // Row end
}

@ExperimentalFoundationApi
@Composable
fun RecipeCard(
    newRecipeViewModel: NewRecipeViewModel,
    ingredientsViewModel: IngredientsViewModel,
    nav: NavController)
{
    val focusManager = LocalFocusManager.current
    val configuration = LocalConfiguration.current

    Card(
        shape = RoundedCornerShape(3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .clickable {}
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                    newRecipeViewModel.showInputText.value = false
                })
            },
        elevation = 10.dp
    ) {
        BoxWithConstraints() {
            if (maxWidth < 400.dp || configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                RecipeCardDefault(newRecipeViewModel, ingredientsViewModel, nav)
            }
            else {
                RecipeCardLandscape(newRecipeViewModel, ingredientsViewModel, nav)
            }
        }
    }
}

@Composable
fun RecipeTextField(newRecipeViewModel: NewRecipeViewModel) {
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp),
        value = newRecipeViewModel.recipeSteps.value,
        onValueChange = { newRecipeViewModel.recipeSteps.value = it }
    )
}


@Composable
fun IngredientSmallCard(
    newRecipeViewModel: NewRecipeViewModel,
    ingredientWithQuantity: RecipeIngredientDAO.IngredientWithQuantity) {
    Spacer(modifier = Modifier.height(10.dp))

    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Image(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape),
            painter = if (ingredientWithQuantity.ingredient.imageUri.isEmpty()) painterResource(R.drawable.image) else rememberImagePainter(
                ingredientWithQuantity.ingredient.imageUri
            ),
            contentDescription = "ingredient image",
            contentScale = ContentScale.Crop,
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(ingredientWithQuantity.ingredient.name, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.weight(1f))

        Text(ingredientWithQuantity.quantity)

        Spacer(modifier = Modifier.width(10.dp))

        OutlinedButton(
            onClick = {
                newRecipeViewModel.ingredientsList.remove(ingredientWithQuantity)
                newRecipeViewModel.ingredientsCount.value -= 1
            }
        ) {
            Icon(
                imageVector = Icons.Filled.Clear,
                contentDescription = "remove item",
            )
        }
    }
}

@Composable
fun QuantityField(newRecipeViewModel: NewRecipeViewModel)
{
    val focusManager = LocalFocusManager.current

    TextField(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth(),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
            }),
        value = newRecipeViewModel.ingredientQuantity.value,
        onValueChange = {
            newRecipeViewModel.ingredientQuantity.value = it
        },
        singleLine = true
    )
}

@ExperimentalFoundationApi
@Composable
fun IngredientsList(newRecipeViewModel: NewRecipeViewModel, ingredientsViewModel: IngredientsViewModel) {
    if (newRecipeViewModel.selectedIngredientId.value < ((0).toLong())) {
        ScrollableRowIngredients(ingredientsViewModel, newRecipeViewModel)
    }
    else {
        val selectedIngredient: Ingredient? by ingredientsViewModel.filter(newRecipeViewModel.selectedIngredientId.value).observeAsState()

        if (selectedIngredient != null) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.weight(1f)) {
                    ClickableIngredientCard(selectedIngredient!!, newRecipeViewModel)
                }

                Column(modifier = Modifier.weight(1f)) {
                    QuantityField(newRecipeViewModel)
                    
                    OkCancelButtons(
                        okFun = {
                            newRecipeViewModel.addIngredient(
                                RecipeIngredientDAO.IngredientWithQuantity(
                                    selectedIngredient!!,
                                    newRecipeViewModel.ingredientQuantity.value
                                )
                            )
                            newRecipeViewModel.selectedIngredientId.value = -1
                            newRecipeViewModel.ingredientName.value = ""
                            newRecipeViewModel.ingredientsCount.value += 1
                            newRecipeViewModel.ingredientQuantity.value = ""
                        },
                        cancelFun = {
                            newRecipeViewModel.selectedIngredientId.value = -1
                        }
                    )
                }
            }
        }
    }
}