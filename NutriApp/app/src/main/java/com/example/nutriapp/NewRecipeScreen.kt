package com.example.nutriapp

import android.annotation.SuppressLint
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.res.Configuration
import android.graphics.Paint
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.onActive
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.security.AccessController.getContext


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

            if (!newRecipeViewModel.showRecipeStepsInputText.value) {
                if (!newRecipeViewModel.isIngredientSelected() && !newRecipeViewModel.searchBoxClicked.value) {
                    Box(Modifier.align(Alignment.CenterHorizontally))
                    {
                        DoneButton {
                            recipesViewModel.insert(
                                newRecipeViewModel.getRecipe(updateRecipeId!!),
                                newRecipeViewModel.getIngredientsIdsAndQuantities()
                            )
                        }
                    }
                }
            }
            else {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(
                        onClick = {
                            newRecipeViewModel.showRecipeStepsInputText.value = false
                        }
                    ) {
                        Icon(
                            Icons.Default.Done,
                            contentDescription = "",
                            modifier = Modifier
                                .padding(10.dp)
                                .size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    IconButton(
                        onClick = {
                            newRecipeViewModel.recipeSteps.value = ""
                            newRecipeViewModel.showRecipeStepsInputText.value = false
                        }
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "",
                            modifier = Modifier
                                .padding(10.dp)
                                .size(24.dp)
                        )
                    }
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
                            newRecipeViewModel.grabFocus.value = true
                            //focusManager.clearFocus()
                        }
                    ),
                painter = if (ingredient.imageUri.isEmpty()) painterResource(R.drawable.image) else rememberImagePainter(ingredient.imageUri),
                contentDescription = "ingredient image",
            )
            Text(text = ingredient.name)
        }
    }

    SideEffect {
        if (newRecipeViewModel.grabFocus.value){
            newRecipeViewModel.focusRequester.value.requestFocus()
            newRecipeViewModel.grabFocus.value = false
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
            .padding(10.dp)
    )
}

@Composable
fun ShowIngredientsWithQuantities(newRecipeViewModel: NewRecipeViewModel)
{
    if (!newRecipeViewModel.isIngredientSelected() && !newRecipeViewModel.searchBoxClicked.value) {
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
}


@Composable
fun SearchView(state: MutableState<String>, newRecipeViewModel: NewRecipeViewModel) {
    val focusManager = LocalFocusManager.current
    val focus = remember { mutableStateOf(false) }
    val inputService = LocalTextInputService.current

    TextField(
        value = state.value,
        onValueChange = { value ->
            state.value = value
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 15.dp, end = 15.dp, top = 15.dp, bottom = 0.dp)
            .height(55.dp)
            .border(2.dp, Orange, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .onFocusChanged {
                if (focus.value != it.isFocused) {
                    focus.value = it.isFocused
                    if (!it.isFocused) {
                        if (newRecipeViewModel.isIngredientSelected()) {
                            newRecipeViewModel.searchBoxClicked.value = false
                        }
                        else {
                            inputService?.hideSoftwareKeyboard()
                        }
                    } else {
                        newRecipeViewModel.searchBoxClicked.value = true
                    }
                }
            },
        textStyle = TextStyle(fontSize = 15.sp),
        label = {(Text(text = "ingredient", color = Orange))},
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
                focus.value = false
            }),

        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = "",
                modifier = Modifier
                    .padding(10.dp)
                    .size(24.dp)
            )
        },
        trailingIcon = {
            if (state.value.isNotEmpty()) {
                IconButton(
                    onClick = {
                        state.value = ""
                    }
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "",
                        modifier = Modifier
                            .padding(10.dp)
                            .size(24.dp)
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(8.dp),
        colors = TextFieldDefaults.textFieldColors(
            cursorColor = Color.Gray,
            leadingIconColor = Orange,
            trailingIconColor = Orange,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )

    LaunchedEffect(focus.value) {
        if (!focus.value) {
            delay(100)
            newRecipeViewModel.searchBoxClicked.value = false
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun ShowIngredientsWithSearch(
    newRecipeViewModel: NewRecipeViewModel,
    ingredientsViewModel: IngredientsViewModel)
{
    if (!newRecipeViewModel.isIngredientSelected()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Cream)
        ) {
            SearchView(newRecipeViewModel.ingredientName, newRecipeViewModel)
        }
    }

    Box(modifier = Modifier
        .fillMaxWidth()
        .defaultMinSize(0.dp, 150.dp)
        .background(Cream)) {
        IngredientsList(newRecipeViewModel, ingredientsViewModel)
    }
}

@ExperimentalFoundationApi
@Composable
fun RecipeCardDefault(
    newRecipeViewModel: NewRecipeViewModel,
    ingredientsViewModel: IngredientsViewModel,
    nav: NavController)
{
    if (!newRecipeViewModel.showRecipeStepsInputText.value) {
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.background(Cream)
        ) {
            stickyHeader {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Cream)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 75.dp)
                    ) {
                        Box(Modifier.align(Alignment.Center)) {
                            ClickableNameField(
                                newRecipeViewModel.recipeName,
                                newRecipeViewModel.showInputText
                            )
                        }
                    }

                    Box(modifier = Modifier.fillMaxWidth()) {
                        Box(Modifier.align(Alignment.Center)) {
                            RecipeImage(newRecipeViewModel, nav)
                        }
                    }

                    if (!newRecipeViewModel.searchBoxClicked.value) {
                        Tabs(newRecipeViewModel)
                    }

                    if (newRecipeViewModel.selectedTabIngredients()) {
                        ShowIngredientsWithSearch(newRecipeViewModel, ingredientsViewModel)
                    }
                }
            }

            items(1) {
                if (newRecipeViewModel.selectedTabIngredients()) {
                    ShowIngredientsWithQuantities(newRecipeViewModel)
                }
                else {
                    StepsText(newRecipeViewModel)
                }
            } // items end
        } // lazy list end
    }
    else {
        RecipeTextField(newRecipeViewModel)
    }
}

@ExperimentalFoundationApi
@Composable
fun RecipeCardLandscape(
    newRecipeViewModel: NewRecipeViewModel,
    ingredientsViewModel: IngredientsViewModel,
    nav: NavController)
{
    if (!newRecipeViewModel.showRecipeStepsInputText.value) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(Cream)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.background(Cream)
                ) {
                    stickyHeader {
                        Column(
                            modifier = Modifier
                                .background(Cream)
                                .fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .defaultMinSize(minHeight = 75.dp)
                            ) {
                                Box(modifier = Modifier.align(Alignment.Center)) {
                                    ClickableNameField(
                                        newRecipeViewModel.recipeName,
                                        newRecipeViewModel.showInputText
                                    )
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
                            modifier = Modifier.background(Cream)
                        ) {
                            items(1) {
                                StepsText(newRecipeViewModel)
                            }
                        }
                    }
                }
            }
        } // Row end
    }
    else {
        RecipeTextField(newRecipeViewModel)
    }
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
        modifier = Modifier.fillMaxWidth(),
        value = newRecipeViewModel.recipeSteps.value,
        onValueChange = { newRecipeViewModel.recipeSteps.value = it },
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
            .fillMaxWidth()
            .focusRequester(newRecipeViewModel.focusRequester.value)
        ,
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
    if (!newRecipeViewModel.isIngredientSelected()) {
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

                    val buttonPressed = remember { mutableStateOf(false) }
                    val inputService = LocalTextInputService.current


                    OkCancelButtons(
                        okFun = {
                            newRecipeViewModel.addIngredient(
                                RecipeIngredientDAO.IngredientWithQuantity(
                                    selectedIngredient!!,
                                    newRecipeViewModel.ingredientQuantity.value
                                )
                            )
                            buttonPressed.value = true
                            newRecipeViewModel.ingredientName.value = ""
                            newRecipeViewModel.ingredientsCount.value += 1
                        },
                        cancelFun = {
                            buttonPressed.value = true
                            newRecipeViewModel.ingredientName.value = ""
                        }
                    )

                    LaunchedEffect(buttonPressed.value) {
                        if (buttonPressed.value) {
                            inputService?.hideSoftwareKeyboard()
                            delay(100)
                            newRecipeViewModel.selectedIngredientId.value = -1
                            newRecipeViewModel.ingredientQuantity.value = ""
                        }
                    }
                }
            }
        }
    }
}
