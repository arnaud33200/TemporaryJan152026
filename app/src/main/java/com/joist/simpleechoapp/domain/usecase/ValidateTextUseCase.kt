package com.joist.simpleechoapp.domain.usecase

import com.joist.simpleechoapp.domain.model.ValidationResult
import com.joist.simpleechoapp.domain.repository.TextValidationRepository
import com.joist.simpleechoapp.domain.util.StringProvider
import com.joist.simpleechoapp.domain.util.StringResource

/**
 * Use case for validating text input.
 * Encapsulates business logic and validation rules.
 *
 * @property repository The repository for text validation operations
 * @property stringProvider Provider for string resources
 */
class ValidateTextUseCase(
    private val repository: TextValidationRepository,
    private val stringProvider: StringProvider
) {
    /**
     * Validates the input text according to business rules.
     *
     * Business rules:
     * - Text cannot be empty or blank
     * - Text is trimmed before validation
     *
     * @param text The text to validate
     * @return [ValidationResult] indicating success or failure
     */
    suspend operator fun invoke(text: String): ValidationResult {
        // Apply business rules
        val trimmedText = text.trim()

        if (trimmedText.isEmpty()) {
            return ValidationResult.Error(
                stringProvider.getString(StringResource.ERROR_TEXT_EMPTY)
            )
        }

        // Delegate to repository for server validation
        return repository.validateText(trimmedText)
    }
}
