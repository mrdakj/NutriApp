package com.example.nutriapp

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.nutriapp.viewmodel.IngredientsViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import com.example.nutriapp.db.Ingredient
import com.example.nutriapp.util.ClickableNameField
import com.example.nutriapp.util.DialogAlert
import com.example.nutriapp.util.DoneButton
import com.example.nutriapp.viewmodel.NewIngredientViewModel

@Composable
fun LoadDataForUpdate(
    updateIngredientId: Long?,
    newIngredientViewModel: NewIngredientViewModel,
    ingredientsViewModel: IngredientsViewModel)
{
    if (updateIngredientId != null && updateIngredientId != (0).toLong() && !newIngredientViewModel.initDone.value) {
        val ingredient: Ingredient? by ingredientsViewModel.filter(updateIngredientId!!).observeAsState()
        if (ingredient != null) {
            newIngredientViewModel.imagePath.value = ingredient!!.imageUri
            newIngredientViewModel.ingredientName.value = ingredient!!.name
            newIngredientViewModel.initDone.value = true
        }
    }
}

@Composable
fun NewIngredientScreen (
    nav: NavController,
    ingredientsViewModel: IngredientsViewModel,
    updateIngredientId: Long?,
    newIngredientViewModel: NewIngredientViewModel = viewModel())
{
    LoadDataForUpdate(updateIngredientId, newIngredientViewModel, ingredientsViewModel)

    val focusManager = LocalFocusManager.current

    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(onTap = {
                focusManager.clearFocus()
                newIngredientViewModel.showNameInput.value = false
            })
        }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(
                    Modifier
                        .align(Alignment.Center)
                        .padding(15.dp)) {
                    ClickableNameField(newIngredientViewModel.ingredientName, newIngredientViewModel.showNameInput)
                }
            }

            Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Image(
                    modifier = Modifier
                        .size(200.dp, 200.dp)
                        .clickable(
                            enabled = true,
                            onClickLabel = "Clickable image",
                            onClick = {
                                nav.navigate(Screen.SelectImageScreen.withArgs("false"))
                            }
                        ),
                    painter = if (newIngredientViewModel.imagePath.value.isEmpty())
                                  painterResource(R.drawable.add_image)
                              else
                                  rememberImagePainter(newIngredientViewModel.imagePath.value),
                    contentDescription = "ingredient image"
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .size(100.dp, 50.dp)
                    .align(Alignment.CenterHorizontally))
            {
                DoneButton {
                    if (updateIngredientId != null && updateIngredientId > 0) {
                        // update
                        var ingredient = newIngredientViewModel.getIngredientModel(updateIngredientId)
                        if (ingredient != null) {
                            ingredientsViewModel.update(ingredient)
                            nav.popBackStack()
                        } else {
                            newIngredientViewModel.showDialog.value = true
                            newIngredientViewModel.setDialogText("Ingredient name is empty.")
                        }
                    }
                    else {
                        // insert
                        var ingredient = newIngredientViewModel.getIngredientModel()
                        if (ingredient != null) {
                            ingredientsViewModel.insert(ingredient)
                            nav.popBackStack()
                        } else {
                            newIngredientViewModel.showDialog.value = true
                            newIngredientViewModel.setDialogText("Ingredient name is empty.")
                        }
                    }
                }
            }
        }

        DialogAlert(newIngredientViewModel.showDialog, newIngredientViewModel.dialogText.value)
    }
}