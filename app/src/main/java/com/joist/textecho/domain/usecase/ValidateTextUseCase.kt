package com.joist.textecho.domain.usecase

import com.joist.textecho.domain.model.ValidationResult
import com.joist.textecho.domain.repository.TextValidationRepository
import javax.inject.Inject

class ValidateTextUseCase @Inject constructor(
    private val repository: TextValidationRepository
) {
    suspend operator fun invoke(text: String): ValidationResult {
        return when {
            text.isBlank() -> ValidationResult.Error.EmptyInput
            text.length < MIN_TEXT_LENGTH -> ValidationResult.Error.TooShort(MIN_TEXT_LENGTH)
            else -> repository.validateText(text)
        }
    }
    
    companion object {
        private const val MIN_TEXT_LENGTH = 3
    }
}
