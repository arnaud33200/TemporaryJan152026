package com.joist.simpleechoapp.data.remote

import com.joist.simpleechoapp.domain.util.StringProvider
import com.joist.simpleechoapp.domain.util.StringResource
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * Simulates an external server API for text validation.
 * In a real application, this would make actual HTTP requests.
 *
 * @property stringProvider Provider for string resources
 */
class ValidationService(
    private val stringProvider: StringProvider
) {
    companion object {
        private const val MIN_DELAY_MS = 500L
        private const val MAX_DELAY_MS = 1500L
        private const val SUCCESS_RATE = 0.7 // 70% success rate
    }

    /**
     * Simulates a network call to validate text with a remote server.
     *
     * Behavior:
     * - Adds random delay to simulate network latency (500-1500ms)
     * - Returns success ~70% of the time
     * - Returns failure ~30% of the time with various error messages
     *
     * @param text The text to validate
     * @return Pair of (success: Boolean, message: String)
     */
    suspend fun validateText(text: String): ValidationResponse {
        // Simulate network delay
        val delay = Random.nextLong(MIN_DELAY_MS, MAX_DELAY_MS)
        delay(delay)

        // Simulate random success/failure
        val isSuccess = Random.nextDouble() < SUCCESS_RATE

        return if (isSuccess) {
            ValidationResponse(success = true, data = text, error = null)
        } else {
            // Simulate various server errors
            val errorMessage = when (Random.nextInt(4)) {
                0 -> stringProvider.getString(StringResource.ERROR_SERVER_INVALID_CONTENT)
                1 -> stringProvider.getString(StringResource.ERROR_NETWORK)
                2 -> stringProvider.getString(StringResource.ERROR_SERVER_UNAVAILABLE)
                else -> stringProvider.getString(StringResource.ERROR_VALIDATION_FAILED)
            }
            ValidationResponse(success = false, data = null, error = errorMessage)
        }
    }

    /**
     * Response model from the validation service.
     */
    data class ValidationResponse(
        val success: Boolean,
        val data: String?,
        val error: String?
    )
}
