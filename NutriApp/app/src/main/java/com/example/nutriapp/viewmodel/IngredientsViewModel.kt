package com.example.nutriapp.viewmodel

import android.database.sqlite.SQLiteException
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.*
import com.example.nutriapp.db.Ingredient
import com.example.nutriapp.db.NutriRepository
import com.example.nutriapp.db.Recipe
import com.example.nutriapp.util.Resource
import kotlinx.coroutines.launch

class IngredientsViewModel (
    private val repo: NutriRepository
) : ViewModel() {

    val isDeleted: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    val allIngredients: LiveData<List<Ingredient>> = repo.allIngredients.asLiveData()
    var selectedIngredient by  mutableStateOf<Ingredient?>(null)
    val showDialog = mutableStateOf(false)
    val dialogText = mutableStateOf("")


    fun setDialogText(text: String) {
        dialogText.value = text
    }

    fun insert(ingredient: Ingredient) = viewModelScope.launch {
        repo.insert(ingredient)
    }


    fun update(ingredient: Ingredient) = viewModelScope.launch {
        repo.update(ingredient)
    }

    fun delete(ingredient: Ingredient) = viewModelScope.launch {
        isDeleted.value = Resource.Loading(false)
        try {
            repo.delete(ingredient)
            isDeleted.postValue(Resource.Success(true))
        }
        catch (e: SQLiteException) {
            isDeleted.postValue(Resource.Error(false, "delete failed with error:$e"))
        }
    }

    fun filter(prefix: String): LiveData<List<Ingredient>> {
        return repo.filter(prefix).asLiveData()
    }

    fun filter(id: Long): LiveData<Ingredient> {
        return repo.filter(id).asLiveData()
    }
}

class IngredientViewModelFactory(private val repository: NutriRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(IngredientsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return IngredientsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}