package com.example.nutriapp.db

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch



@Database(entities = [Ingredient::class, Recipe::class, RecipeIngredient::class], version = 1, exportSchema = false)
abstract class NutriDatabase : RoomDatabase() {
    abstract fun ingredientDao(): IngredientDAO
    abstract fun recipeDao(): RecipeDAO
    abstract fun recipeIngredientDao(): RecipeIngredientDAO

    private class NutriDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    var dao = database.ingredientDao()
                    var ingredient = Ingredient(name = "onion", imageUri = "", calories = 100)
                    dao.insert(ingredient)
                }
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: NutriDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): NutriDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NutriDatabase::class.java,
                    "nutri_database"
                )
                .addCallback(NutriDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}