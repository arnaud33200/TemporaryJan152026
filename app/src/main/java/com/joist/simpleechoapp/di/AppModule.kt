package com.joist.simpleechoapp.di

import android.content.Context
import com.joist.simpleechoapp.data.remote.ValidationService
import com.joist.simpleechoapp.data.repository.TextValidationRepositoryImpl
import com.joist.simpleechoapp.data.util.AndroidStringProvider
import com.joist.simpleechoapp.domain.repository.TextValidationRepository
import com.joist.simpleechoapp.domain.usecase.ValidateTextUseCase
import com.joist.simpleechoapp.domain.util.StringProvider
import com.joist.simpleechoapp.presentation.echo.EchoViewModel

/**
 * Manual dependency injection container.
 * Provides singleton instances of dependencies throughout the app.
 *
 * For a simple app like this, manual DI is sufficient and avoids over-engineering.
 * In a larger app, consider using Hilt or Koin.
 */
object AppModule {

    private lateinit var stringProvider: StringProvider

    /**
     * Initializes the DI container with application context.
     * Must be called before accessing any dependencies.
     *
     * @param context Application context
     */
    fun init(context: Context) {
        stringProvider = AndroidStringProvider(context.applicationContext)
    }

    /**
     * Provides the validation service instance.
     * Simulates external API calls.
     */
    private val validationService: ValidationService by lazy {
        ValidationService(stringProvider)
    }

    /**
     * Provides the text validation repository implementation.
     * Abstracts data source from domain layer.
     */
    private val textValidationRepository: TextValidationRepository by lazy {
        TextValidationRepositoryImpl(validationService, stringProvider)
    }

    /**
     * Provides the validate text use case.
     * Contains business logic for text validation.
     */
    private val validateTextUseCase: ValidateTextUseCase by lazy {
        ValidateTextUseCase(textValidationRepository, stringProvider)
    }

    /**
     * Provides a new instance of EchoViewModel.
     * Note: In production, use ViewModelProvider.Factory for proper lifecycle handling.
     */
    fun provideEchoViewModel(): EchoViewModel {
        return EchoViewModel(validateTextUseCase)
    }
}
