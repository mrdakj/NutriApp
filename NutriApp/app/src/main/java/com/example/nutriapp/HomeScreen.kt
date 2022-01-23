package com.example.nutriapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.nutriapp.db.Ingredient

@Composable
fun HomeScreen(nav : NavController) {
    Surface(color = MaterialTheme.colors.background) {
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(1) {
                NavigationCard("recipes", nav)
                NavigationCard("ingredients", nav)
            }
        }
    }
}

@Composable
fun NavigationCard(title: String, nav: NavController) {
    Card(
        shape = RoundedCornerShape(3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .clickable {
                nav.navigate(if (title == "recipes") Screen.RecipesScreen.route else Screen.IngredientsScreen.route)
            },
        elevation = 10.dp
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier
                    .size(150.dp, 150.dp)
                    .padding(10.dp),
                painter = painterResource(if (title == "recipes") R.drawable.recipes else R.drawable.ingredients),
                contentDescription = "image"
            )
            Text(text = title)
        }
    }
}