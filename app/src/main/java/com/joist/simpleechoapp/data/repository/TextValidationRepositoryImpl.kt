package com.joist.simpleechoapp.data.repository

import com.joist.simpleechoapp.data.remote.ValidationService
import com.joist.simpleechoapp.domain.model.ValidationResult
import com.joist.simpleechoapp.domain.repository.TextValidationRepository
import com.joist.simpleechoapp.domain.util.StringProvider
import com.joist.simpleechoapp.domain.util.StringResource

/**
 * Implementation of [TextValidationRepository] that uses [ValidationService]
 * to validate text with a simulated remote server.
 *
 * @property validationService The service for performing validation
 * @property stringProvider Provider for string resources
 */
class TextValidationRepositoryImpl(
    private val validationService: ValidationService,
    private val stringProvider: StringProvider
) : TextValidationRepository {

    /**
     * Validates text by calling the remote validation service.
     * Handles the service response and maps it to domain models.
     *
     * @param text The text to validate
     * @return [ValidationResult.Success] if validation succeeds,
     *         [ValidationResult.Error] if validation fails
     */
    override suspend fun validateText(text: String): ValidationResult {
        return try {
            val response = validationService.validateText(text)

            if (response.success && response.data != null) {
                ValidationResult.Success(response.data)
            } else {
                ValidationResult.Error(
                    response.error ?: stringProvider.getString(StringResource.ERROR_UNKNOWN_VALIDATION)
                )
            }
        } catch (e: Exception) {
            // Handle any unexpected exceptions
            ValidationResult.Error(
                stringProvider.getString(StringResource.ERROR_UNEXPECTED, e.message ?: "")
            )
        }
    }
}
