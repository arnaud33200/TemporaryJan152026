package com.joist.textecho.ui.viewmodel

import app.cash.turbine.test
import com.joist.textecho.domain.model.ValidationResult
import com.joist.textecho.domain.usecase.ValidateTextUseCase
import com.joist.textecho.ui.state.TextEchoState
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TextEchoViewModelTest {
    
    private lateinit var validateTextUseCase: ValidateTextUseCase
    private lateinit var viewModel: TextEchoViewModel
    
    private val testDispatcher = StandardTestDispatcher()
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        validateTextUseCase = mockk()
        viewModel = TextEchoViewModel(validateTextUseCase)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `initial state is empty`() = runTest {
        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals("", state.inputText)
            assertEquals("", state.outputText)
            assertFalse(state.isLoading)
            assertNull(state.error)
        }
    }
    
    @Test
    fun `onTextChanged updates input text and clears error`() = runTest {
        // Given
        val newText = "New text"
        
        // When
        viewModel.onTextChanged(newText)
        
        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(newText, state.inputText)
            assertNull(state.error)
        }
    }
    
    @Test
    fun `onSubmit with successful validation updates output text`() = runTest {
        // Given
        val inputText = "Valid text"
        val expectedResult = ValidationResult.Success(inputText)
        coEvery { validateTextUseCase(inputText) } returns expectedResult
        
        // When
        viewModel.onTextChanged(inputText)
        viewModel.onSubmit()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(inputText, state.outputText)
            assertFalse(state.isLoading)
            assertNull(state.error)
        }
    }
    
    @Test
    fun `onSubmit shows loading state during validation`() = runTest {
        // Given
        val inputText = "Valid text"
        coEvery { validateTextUseCase(inputText) } returns ValidationResult.Success(inputText)
        
        viewModel.state.test {
            // Initial state
            awaitItem()
            
            // When
            viewModel.onTextChanged(inputText)
            awaitItem() // State after text change
            
            viewModel.onSubmit()
            
            // Then - should show loading state
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
            assertNull(loadingState.error)
            
            testDispatcher.scheduler.advanceUntilIdle()
            
            // Final state after completion
            val finalState = awaitItem()
            assertFalse(finalState.isLoading)
            assertEquals(inputText, finalState.outputText)
        }
    }
    
    @Test
    fun `onSubmit with validation error updates error state`() = runTest {
        // Given
        val inputText = "Valid text"
        val expectedError = ValidationResult.Error.NetworkError
        coEvery { validateTextUseCase(inputText) } returns expectedError
        
        // When
        viewModel.onTextChanged(inputText)
        viewModel.onSubmit()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.error is TextEchoState.UiError.NetworkError)
            assertFalse(state.isLoading)
            assertEquals("", state.outputText)
        }
    }
    
    @Test
    fun `onSubmit with empty input error updates error state`() = runTest {
        // Given
        val inputText = "Valid text"
        val expectedError = ValidationResult.Error.EmptyInput
        coEvery { validateTextUseCase(inputText) } returns expectedError
        
        // When
        viewModel.onTextChanged(inputText)
        viewModel.onSubmit()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.error is TextEchoState.UiError.EmptyInput)
            assertFalse(state.isLoading)
        }
    }
    
    @Test
    fun `onSubmit with server error updates error state with message`() = runTest {
        // Given
        val inputText = "Valid text"
        val errorMessage = "Server validation failed"
        val expectedError = ValidationResult.Error.ServerError(errorMessage)
        coEvery { validateTextUseCase(inputText) } returns expectedError
        
        // When
        viewModel.onTextChanged(inputText)
        viewModel.onSubmit()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.error is TextEchoState.UiError.ServerError)
            assertEquals(errorMessage, (state.error as TextEchoState.UiError.ServerError).message)
            assertFalse(state.isLoading)
        }
    }
    
    @Test
    fun `onClear resets all state`() = runTest {
        // Given - setup some state
        val inputText = "Some text"
        viewModel.onTextChanged(inputText)
        coEvery { validateTextUseCase(inputText) } returns ValidationResult.Success(inputText)
        viewModel.onSubmit()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When
        viewModel.onClear()
        
        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals("", state.inputText)
            assertEquals("", state.outputText)
            assertNull(state.error)
            assertFalse(state.isLoading)
        }
    }
}
