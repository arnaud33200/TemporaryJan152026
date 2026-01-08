package com.joist.simpleechoapp.domain.repository

import com.joist.simpleechoapp.domain.model.ValidationResult

/**
 * Repository interface for text validation operations.
 * Abstracts the data source implementation from the domain layer.
 */
interface TextValidationRepository {
    /**
     * Validates the given text with a remote server.
     *
     * @param text The text to validate
     * @return [ValidationResult] indicating success or failure
     */
    suspend fun validateText(text: String): ValidationResult
}
