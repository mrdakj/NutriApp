package com.example.nutriapp

import android.app.Application
import com.example.nutriapp.db.NutriDatabase
import com.example.nutriapp.db.NutriRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class NutriApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())
    private val database by lazy { NutriDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { NutriRepository(database) }
}