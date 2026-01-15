package com.joist.textecho.data.repository

import com.joist.textecho.data.service.ValidationService
import com.joist.textecho.di.IoDispatcher
import com.joist.textecho.domain.model.ValidationResult
import com.joist.textecho.domain.repository.TextValidationRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

class TextValidationRepositoryImpl @Inject constructor(
    private val validationService: ValidationService,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : TextValidationRepository {

    override suspend fun validateText(text: String): ValidationResult =
        withContext(ioDispatcher) {
            try {
                val validatedText = validationService.validateWithServer(text)
                ValidationResult.Success(validatedText)
            } catch (e: CancellationException) {
                throw e
            } catch (e: ValidationService.ValidationException) {
                ValidationResult.Error.ServerError(e.message ?: "Unknown server error")
            } catch (e: IOException) {
                ValidationResult.Error.NetworkError
            } catch (e: Exception) {
                ValidationResult.Error.Unknown(e)
            }
        }
}
