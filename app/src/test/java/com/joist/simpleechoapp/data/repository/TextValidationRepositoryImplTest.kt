package com.joist.simpleechoapp.data.repository

import com.joist.simpleechoapp.data.remote.ValidationService
import com.joist.simpleechoapp.domain.model.ValidationResult
import com.joist.simpleechoapp.domain.util.StringProvider
import com.joist.simpleechoapp.domain.util.StringResource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [TextValidationRepositoryImpl].
 * Tests repository behavior and error handling.
 */
class TextValidationRepositoryImplTest {

    private lateinit var validationService: ValidationService
    private lateinit var stringProvider: StringProvider
    private lateinit var repository: TextValidationRepositoryImpl

    @Before
    fun setup() {
        validationService = mockk()
        stringProvider = mockk()

        // Setup default string provider behavior
        every { stringProvider.getString(StringResource.ERROR_UNKNOWN_VALIDATION) } returns "Unknown validation error occurred"
        every { stringProvider.getString(StringResource.ERROR_UNEXPECTED, any()) } returns "An unexpected error occurred: Network error"

        repository = TextValidationRepositoryImpl(validationService, stringProvider)
    }

    @Test
    fun `validateText with successful service response returns Success`() = runTest {
        // Arrange
        val inputText = "Hello"
        val serviceResponse = ValidationService.ValidationResponse(
            success = true,
            data = inputText,
            error = null
        )
        coEvery { validationService.validateText(inputText) } returns serviceResponse

        // Act
        val result = repository.validateText(inputText)

        // Assert
        assertTrue(result is ValidationResult.Success)
        assertEquals(inputText, (result as ValidationResult.Success).text)
        coVerify(exactly = 1) { validationService.validateText(inputText) }
    }

    @Test
    fun `validateText with failed service response returns Error`() = runTest {
        // Arrange
        val inputText = "Test"
        val errorMessage = "Validation failed"
        val serviceResponse = ValidationService.ValidationResponse(
            success = false,
            data = null,
            error = errorMessage
        )
        coEvery { validationService.validateText(inputText) } returns serviceResponse

        // Act
        val result = repository.validateText(inputText)

        // Assert
        assertTrue(result is ValidationResult.Error)
        assertEquals(errorMessage, (result as ValidationResult.Error).message)
        coVerify(exactly = 1) { validationService.validateText(inputText) }
    }

    @Test
    fun `validateText with failed response and null error message uses default message`() = runTest {
        // Arrange
        val inputText = "Test"
        val serviceResponse = ValidationService.ValidationResponse(
            success = false,
            data = null,
            error = null
        )
        coEvery { validationService.validateText(inputText) } returns serviceResponse

        // Act
        val result = repository.validateText(inputText)

        // Assert
        assertTrue(result is ValidationResult.Error)
        assertEquals("Unknown validation error occurred", (result as ValidationResult.Error).message)
    }

    @Test
    fun `validateText with successful response but null data returns Error`() = runTest {
        // Arrange
        val inputText = "Test"
        val serviceResponse = ValidationService.ValidationResponse(
            success = true,
            data = null,
            error = null
        )
        coEvery { validationService.validateText(inputText) } returns serviceResponse

        // Act
        val result = repository.validateText(inputText)

        // Assert
        assertTrue(result is ValidationResult.Error)
        assertEquals("Unknown validation error occurred", (result as ValidationResult.Error).message)
    }

    @Test
    fun `validateText with service exception returns Error`() = runTest {
        // Arrange
        val inputText = "Test"
        val exceptionMessage = "Network error"
        coEvery { validationService.validateText(inputText) } throws RuntimeException(exceptionMessage)

        // Act
        val result = repository.validateText(inputText)

        // Assert
        assertTrue(result is ValidationResult.Error)
        assertTrue((result as ValidationResult.Error).message.contains(exceptionMessage))
        assertTrue(result.message.contains("An unexpected error occurred"))
    }
}
