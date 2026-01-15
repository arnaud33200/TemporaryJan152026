package com.joist.textecho.domain.usecase

import com.joist.textecho.domain.model.ValidationResult
import com.joist.textecho.domain.repository.TextValidationRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ValidateTextUseCaseTest {
    
    private lateinit var repository: TextValidationRepository
    private lateinit var useCase: ValidateTextUseCase
    
    @Before
    fun setup() {
        repository = mockk()
        useCase = ValidateTextUseCase(repository)
    }
    
    @Test
    fun `invoke with empty text returns EmptyInput error`() = runTest {
        // Given
        val emptyText = ""
        
        // When
        val result = useCase(emptyText)
        
        // Then
        assertTrue(result is ValidationResult.Error.EmptyInput)
        coVerify(exactly = 0) { repository.validateText(any()) }
    }
    
    @Test
    fun `invoke with blank text returns EmptyInput error`() = runTest {
        // Given
        val blankText = "   "
        
        // When
        val result = useCase(blankText)
        
        // Then
        assertTrue(result is ValidationResult.Error.EmptyInput)
        coVerify(exactly = 0) { repository.validateText(any()) }
    }
    
    @Test
    fun `invoke with text shorter than minimum length returns TooShort error`() = runTest {
        // Given
        val shortText = "ab" // less than 3 characters
        
        // When
        val result = useCase(shortText)
        
        // Then
        assertTrue(result is ValidationResult.Error.TooShort)
        assertEquals(3, (result as ValidationResult.Error.TooShort).minLength)
        coVerify(exactly = 0) { repository.validateText(any()) }
    }
    
    @Test
    fun `invoke with valid text calls repository and returns success`() = runTest {
        // Given
        val validText = "Valid text"
        val expectedResult = ValidationResult.Success(validText)
        coEvery { repository.validateText(validText) } returns expectedResult
        
        // When
        val result = useCase(validText)
        
        // Then
        assertEquals(expectedResult, result)
        coVerify(exactly = 1) { repository.validateText(validText) }
    }
    
    @Test
    fun `invoke with valid text propagates repository error`() = runTest {
        // Given
        val validText = "Valid text"
        val expectedError = ValidationResult.Error.NetworkError
        coEvery { repository.validateText(validText) } returns expectedError
        
        // When
        val result = useCase(validText)
        
        // Then
        assertEquals(expectedError, result)
        coVerify(exactly = 1) { repository.validateText(validText) }
    }
    
    @Test
    fun `invoke with valid text propagates server error from repository`() = runTest {
        // Given
        val validText = "Valid text"
        val errorMessage = "Server error"
        val expectedError = ValidationResult.Error.ServerError(errorMessage)
        coEvery { repository.validateText(validText) } returns expectedError
        
        // When
        val result = useCase(validText)
        
        // Then
        assertTrue(result is ValidationResult.Error.ServerError)
        assertEquals(errorMessage, (result as ValidationResult.Error.ServerError).message)
        coVerify(exactly = 1) { repository.validateText(validText) }
    }
}
