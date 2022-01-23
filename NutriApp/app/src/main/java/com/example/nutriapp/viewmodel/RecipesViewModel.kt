package com.example.nutriapp.viewmodel

import android.database.sqlite.SQLiteException
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.*
import androidx.room.Transaction
import com.example.nutriapp.db.*
import com.example.nutriapp.util.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.lang.Thread.sleep
import java.sql.SQLIntegrityConstraintViolationException


class RecipesViewModel (
    private val repo: NutriRepository
) : ViewModel() {

    val isInserted: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    val allRecipes: LiveData<List<Recipe>> = repo.allRecipes.asLiveData()
    var selectedRecipe by  mutableStateOf<Recipe?>(null)

    fun insert(recipe: Recipe, ingredients: List<Pair<Long, String>>) = viewModelScope.launch {
        isInserted.value = Resource.Loading(false)
        try {
            repo.insert(recipe, ingredients)
            isInserted.postValue(Resource.Success(true))
        }
        catch (e: SQLiteException) {
            isInserted.postValue(Resource.Error(false, "Insertion failed"))
        }
    }

    fun delete(recipe: Recipe) = viewModelScope.launch {
        repo.delete(recipe)
    }

    fun getIngredientsForRecipe(id: Long): LiveData<List<RecipeIngredientDAO.IngredientWithQuantity>> {
        return repo.getIngredientsForRecipe(id).asLiveData()
    }

    fun getRecipe(id: Long): LiveData<Recipe> {
        return repo.getRecipe(id).asLiveData()
    }
}

class RecipeViewModelFactory(private val repository: NutriRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecipesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}