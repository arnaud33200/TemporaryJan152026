package com.joist.simpleechoapp.domain.model

/**
 * Represents the result of a text validation operation.
 * Uses sealed interface for type-safe handling of success and error states.
 */
sealed interface ValidationResult {
    /**
     * Successful validation with the validated text.
     * @property text The validated text returned from the server
     */
    data class Success(val text: String) : ValidationResult

    /**
     * Failed validation with an error message.
     * @property message User-friendly error message describing what went wrong
     */
    data class Error(val message: String) : ValidationResult
}
