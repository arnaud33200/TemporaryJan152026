package com.joist.simpleechoapp.presentation.echo

/**
 * Represents the UI state for the Echo screen.
 * Uses sealed interface for type-safe state handling.
 */
sealed interface EchoUiState {
    /**
     * Initial/idle state when no validation has been performed.
     */
    data object Idle : EchoUiState

    /**
     * Loading state while validation is in progress.
     */
    data object Loading : EchoUiState

    /**
     * Success state with the validated text to display.
     * @property text The echoed text from successful validation
     */
    data class Success(val text: String) : EchoUiState

    /**
     * Error state with an error message to display.
     * @property message The error message to show to the user
     */
    data class Error(val message: String) : EchoUiState
}
