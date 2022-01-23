package com.example.nutriapp

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.nutriapp.camera.CameraCapture
import com.example.nutriapp.gallery.GallerySelect
import com.example.nutriapp.viewmodel.NewIngredientViewModel
import com.example.nutriapp.viewmodel.NewRecipeViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import java.io.File

@ExperimentalPermissionsApi
@Composable
fun SelectImageScreen(context: Context,
                      nav: NavController,
                      newRecipe: Boolean?) {
    var imageUri by remember { mutableStateOf(EMPTY_IMAGE_URI) }
    var cameraOn by remember { mutableStateOf(false) }
    var galleryOn by remember { mutableStateOf(false) }
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

    val backStack = remember { nav.getBackStackEntry(
        if (newRecipe == true)
            Screen.NewRecipeScreen.withArgs("{updateRecipeId}")
        else
            Screen.NewIngredientScreen.withArgs("{updateIngredientId}"))
    }

    val viewModel = if (newRecipe == true) viewModel(backStack) as NewRecipeViewModel
    else viewModel(backStack) as NewIngredientViewModel

    Surface(
        color = MaterialTheme.colors.background,
    ) {
        if (cameraOn) {
            if (storageDir != null) {
                CameraCapture(
                    modifier = Modifier.fillMaxSize(),
                    storageDir = storageDir,
                    onImageFile = { file ->
                        imageUri = file.toUri()
                        cameraOn = false
                    }
                )
            }
            else {
                cameraOn = false
            }
        }
        else if (galleryOn) {
            GallerySelect(
                modifier = Modifier.fillMaxSize(),
                onImageUri = { uri ->
                    galleryOn = false
                    imageUri = uri

                    if (imageUri != EMPTY_IMAGE_URI) {
                        val bitmap = MediaStore.Images.Media.getBitmap(
                            context.contentResolver,
                            imageUri
                        )

                        val photoFile = File.createTempFile("image", ".jpg", storageDir)
                        photoFile.writeBitmapFile(bitmap, Bitmap.CompressFormat.JPEG, 85)
                        imageUri = photoFile.toUri()
                    }
                }
            )
        }
        else {
            if (imageUri == EMPTY_IMAGE_URI) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    items(1) {
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
                                    modifier = Modifier.size(150.dp, 150.dp).padding(10.dp)
                                        .clickable(
                                            enabled = true,
                                            onClickLabel = "Clickable image",
                                            onClick = {
                                                galleryOn = true
                                            }
                                        ),
                                    painter = painterResource(R.drawable.gallery),
                                    contentDescription = "gallery"
                                )
                                Text(text = "select from gallery")
                            }
                        }

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
                                    modifier = Modifier.size(150.dp, 150.dp).padding(10.dp)
                                        .clickable(
                                            enabled = true,
                                            onClickLabel = "Clickable image",
                                            onClick = { cameraOn = true }
                                        ),
                                    painter = painterResource(R.drawable.camera),
                                    contentDescription = "camera"
                                )
                                Text(text = "take a photo")
                            }
                        }
                    }

                }
            }
            else {
                // image uri is set

                Column(modifier = Modifier.padding(15.dp)) {
                    Image(
                        modifier = Modifier.weight(1f).align(Alignment.CenterHorizontally),
                        painter = rememberImagePainter(imageUri),
                        contentDescription = "Captured image"
                    )

                    Spacer(modifier = Modifier.fillMaxWidth().height(15.dp))

                    Row(modifier = Modifier
                        .height(50.dp)
                        .fillMaxWidth()) {
                        Button(
                            modifier = Modifier.weight(3f),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(
                                0xFF56B334
                            ), contentColor = Color.White),
                            onClick = {
                                viewModel.updateImagePath(imageUri.toString())
                                nav.popBackStack()
                            },
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
                            onClick = {
                                imageUri = EMPTY_IMAGE_URI
                            },
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
            }
        }
    }
}

fun File.writeBitmapFile(bitmap: Bitmap, format: Bitmap.CompressFormat, quality: Int) {
    outputStream().use { out ->
        bitmap.compress(format, quality, out)
        out.flush()
    }
}