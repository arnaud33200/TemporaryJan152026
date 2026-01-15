package com.joist.textecho.domain.repository

import com.joist.textecho.domain.model.ValidationResult

interface TextValidationRepository {
    suspend fun validateText(text: String): ValidationResult
}
