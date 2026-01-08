package com.joist.simpleechoapp.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.joist.simpleechoapp.presentation.echo.EchoViewModel

/**
 * Factory for creating ViewModels with dependency injection.
 * Ensures proper ViewModel lifecycle management while injecting dependencies.
 */
class ViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return when {
            modelClass.isAssignableFrom(EchoViewModel::class.java) -> {
                AppModule.provideEchoViewModel() as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
