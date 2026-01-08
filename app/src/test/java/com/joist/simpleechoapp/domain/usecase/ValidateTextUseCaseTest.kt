package com.joist.simpleechoapp.domain.usecase

import com.joist.simpleechoapp.domain.model.ValidationResult
import com.joist.simpleechoapp.domain.repository.TextValidationRepository
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
 * Unit tests for [ValidateTextUseCase].
 * Tests business logic and validation rules.
 */
class ValidateTextUseCaseTest {

    private lateinit var repository: TextValidationRepository
    private lateinit var stringProvider: StringProvider
    private lateinit var useCase: ValidateTextUseCase

    @Before
    fun setup() {
        repository = mockk()
        stringProvider = mockk()

        // Setup default string provider behavior
        every { stringProvider.getString(StringResource.ERROR_TEXT_EMPTY) } returns "Text cannot be empty"

        useCase = ValidateTextUseCase(repository, stringProvider)
    }

    @Test
    fun `invoke with valid text returns success from repository`() = runTest {
        // Arrange
        val inputText = "Hello World"
        val expectedResult = ValidationResult.Success("Hello World")
        coEvery { repository.validateText(inputText) } returns expectedResult

        // Act
        val result = useCase(inputText)

        // Assert
        assertEquals(expectedResult, result)
        coVerify(exactly = 1) { repository.validateText(inputText) }
    }

    @Test
    fun `invoke with empty text returns error without calling repository`() = runTest {
        // Arrange
        val inputText = ""

        // Act
        val result = useCase(inputText)

        // Assert
        assertTrue(result is ValidationResult.Error)
        assertEquals("Text cannot be empty", (result as ValidationResult.Error).message)
        coVerify(exactly = 0) { repository.validateText(any()) }
    }

    @Test
    fun `invoke with blank text returns error without calling repository`() = runTest {
        // Arrange
        val inputText = "   "

        // Act
        val result = useCase(inputText)

        // Assert
        assertTrue(result is ValidationResult.Error)
        assertEquals("Text cannot be empty", (result as ValidationResult.Error).message)
        coVerify(exactly = 0) { repository.validateText(any()) }
    }

    @Test
    fun `invoke trims whitespace before validation`() = runTest {
        // Arrange
        val inputText = "  Hello World  "
        val trimmedText = "Hello World"
        val expectedResult = ValidationResult.Success(trimmedText)
        coEvery { repository.validateText(trimmedText) } returns expectedResult

        // Act
        val result = useCase(inputText)

        // Assert
        assertEquals(expectedResult, result)
        coVerify(exactly = 1) { repository.validateText(trimmedText) }
        coVerify(exactly = 0) { repository.validateText(inputText) }
    }

    @Test
    fun `invoke with repository error returns error result`() = runTest {
        // Arrange
        val inputText = "Test"
        val expectedResult = ValidationResult.Error("Server error")
        coEvery { repository.validateText(inputText) } returns expectedResult

        // Act
        val result = useCase(inputText)

        // Assert
        assertEquals(expectedResult, result)
        assertTrue(result is ValidationResult.Error)
        assertEquals("Server error", (result as ValidationResult.Error).message)
    }
}
