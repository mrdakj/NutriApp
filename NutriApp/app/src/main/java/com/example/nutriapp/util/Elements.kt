package com.example.nutriapp.util

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nutriapp.Orange
import com.example.nutriapp.Screen
import com.example.nutriapp.White
import com.example.nutriapp.db.RecipeIngredientDAO
import com.example.nutriapp.viewmodel.NewRecipeViewModel

@Composable
fun DoneButton(onClickFun: () -> Unit) {
    Button(
        colors = ButtonDefaults.buttonColors(backgroundColor = Orange, contentColor = White),
        onClick = onClickFun
    ) {
        Icon(
            imageVector = Icons.Filled.Done,
            contentDescription = "done",
            modifier = Modifier.size(ButtonDefaults.IconSize)
        )
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Text("Done")
    }
}

@Composable
fun AddButton(onClickFun: () -> Unit)
{
    Button(
        colors = ButtonDefaults.buttonColors(backgroundColor = Orange, contentColor = White),
        onClick = onClickFun
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "add item",
            modifier = Modifier.size(ButtonDefaults.IconSize)
        )
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Text("Add")
    }
}

@Composable
fun DialogAlert(show: MutableState<Boolean>, text: String) {
    if (show.value) {
        AlertDialog(
            onDismissRequest = {
                show.value = false
            },
            title = {
                Text(text = "Error")
            },
            text = {
                Text(text)
            },
            confirmButton = {
                Button(
                    onClick = {
                        show.value = false
                    }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun OkCancelButtons(okFun: () -> Unit, cancelFun: () -> Unit) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp)) {
        Button(
            modifier = Modifier.weight(3f),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(
                0xFF56B334
            ), contentColor = Color.White),
            onClick = okFun,
        ) {
            Icon(
                imageVector = Icons.Filled.Done,
                contentDescription = "ok",
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            modifier = Modifier.weight(3f),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray, contentColor = Color.White),
            onClick = cancelFun,
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "cancel",
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        }
    }
}

@Composable
fun NameField(name: String)
{
    Text(
        text = name,
        modifier = Modifier
            .padding(10.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Orange)
            .padding(10.dp),
        color = White
    )
}

@Composable
fun ClickableNameField(name: MutableState<String>, showInput: MutableState<Boolean>)
{
    if (showInput.value) {
        NameTextField(name, showInput)
    } else {
        Text(if (name.value.isEmpty()) "click to add name" else name.value,
            modifier = Modifier
                .padding(10.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Orange)
                .clickable(
                    enabled = true,
                    role = Role.Button
                ) {
                    showInput.value = true
                }
                .padding(10.dp),
            color = White
        )
    }
}

@Composable
fun NameTextField(name: MutableState<String>, showInput: MutableState<Boolean>) {
    val focusManager = LocalFocusManager.current
    TextField(
        value = name.value,
        onValueChange = { value ->
            name.value = value
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(7.dp)
            .clip(RoundedCornerShape(10.dp)),
        textStyle = TextStyle(fontSize = 18.sp),
        label = { (Text(text = "name", color = Orange)) },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
                showInput.value = false
            }),
        trailingIcon = {
            if (name.value.isNotEmpty()) {
                Row {
                    IconButton(
                        onClick = {
                            name.value = ""
                        }
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "",
                            modifier = Modifier
                                .padding(15.dp)
                                .size(24.dp)
                        )
                    }
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
}

@Composable
fun SearchView(state: MutableState<String>) {
    val focusManager = LocalFocusManager.current

    TextField(
        value = state.value,
        onValueChange = { value ->
            state.value = value
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .height(55.dp)
            .border(2.dp, Orange, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp)),
        textStyle = TextStyle(fontSize = 15.sp),
        label = {(Text(text = "ingredient", color = Orange))},
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
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
}