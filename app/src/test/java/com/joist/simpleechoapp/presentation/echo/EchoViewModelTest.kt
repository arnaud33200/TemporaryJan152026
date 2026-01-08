package com.joist.simpleechoapp.presentation.echo

import app.cash.turbine.test
import com.joist.simpleechoapp.domain.model.ValidationResult
import com.joist.simpleechoapp.domain.usecase.ValidateTextUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [EchoViewModel].
 * Tests state management and UI logic using Turbine for Flow testing.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class EchoViewModelTest {

    private lateinit var validateTextUseCase: ValidateTextUseCase
    private lateinit var viewModel: EchoViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        validateTextUseCase = mockk()
        viewModel = EchoViewModel(validateTextUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Idle`() = runTest {
        // Assert
        viewModel.uiState.test {
            assertEquals(EchoUiState.Idle, awaitItem())
        }
    }

    @Test
    fun `onInputTextChanged updates inputText state`() = runTest {
        // Arrange
        val testText = "Hello World"

        // Act
        viewModel.onInputTextChanged(testText)

        // Assert
        viewModel.inputText.test {
            assertEquals(testText, awaitItem())
        }
    }

    @Test
    fun `onSubmitClicked with successful validation updates state to Success`() = runTest {
        // Arrange
        val inputText = "Test"
        val expectedText = "Test"
        viewModel.onInputTextChanged(inputText)
        coEvery { validateTextUseCase(inputText) } returns ValidationResult.Success(expectedText)

        // Act & Assert
        viewModel.uiState.test {
            assertEquals(EchoUiState.Idle, awaitItem())

            viewModel.onSubmitClicked()
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(EchoUiState.Loading, awaitItem())
            assertEquals(EchoUiState.Success(expectedText), awaitItem())
        }

        coVerify(exactly = 1) { validateTextUseCase(inputText) }
    }

    @Test
    fun `onSubmitClicked with failed validation updates state to Error`() = runTest {
        // Arrange
        val inputText = "Test"
        val errorMessage = "Validation failed"
        viewModel.onInputTextChanged(inputText)
        coEvery { validateTextUseCase(inputText) } returns ValidationResult.Error(errorMessage)

        // Act & Assert
        viewModel.uiState.test {
            assertEquals(EchoUiState.Idle, awaitItem())

            viewModel.onSubmitClicked()
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(EchoUiState.Loading, awaitItem())
            assertEquals(EchoUiState.Error(errorMessage), awaitItem())
        }

        coVerify(exactly = 1) { validateTextUseCase(inputText) }
    }

    @Test
    fun `onSubmitClicked shows Loading state during validation`() = runTest {
        // Arrange
        val inputText = "Test"
        viewModel.onInputTextChanged(inputText)
        coEvery { validateTextUseCase(inputText) } returns ValidationResult.Success(inputText)

        // Act & Assert
        viewModel.uiState.test {
            assertEquals(EchoUiState.Idle, awaitItem())

            viewModel.onSubmitClicked()

            assertEquals(EchoUiState.Loading, awaitItem())

            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(EchoUiState.Success(inputText), awaitItem())
        }
    }

    @Test
    fun `resetState changes state back to Idle`() = runTest {
        // Arrange
        val inputText = "Test"
        viewModel.onInputTextChanged(inputText)
        coEvery { validateTextUseCase(inputText) } returns ValidationResult.Success(inputText)

        viewModel.uiState.test {
            skipItems(1) // Skip initial Idle state

            viewModel.onSubmitClicked()
            testDispatcher.scheduler.advanceUntilIdle()

            skipItems(2) // Skip Loading and Success states

            // Act
            viewModel.resetState()

            // Assert
            assertEquals(EchoUiState.Idle, awaitItem())
        }
    }

    @Test
    fun `multiple submissions update state correctly`() = runTest {
        // Arrange
        val firstInput = "First"
        val secondInput = "Second"

        viewModel.onInputTextChanged(firstInput)
        coEvery { validateTextUseCase(firstInput) } returns ValidationResult.Success(firstInput)
        coEvery { validateTextUseCase(secondInput) } returns ValidationResult.Error("Error")

        viewModel.uiState.test {
            assertEquals(EchoUiState.Idle, awaitItem())

            // First submission
            viewModel.onSubmitClicked()
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(EchoUiState.Loading, awaitItem())
            assertEquals(EchoUiState.Success(firstInput), awaitItem())

            // Second submission
            viewModel.onInputTextChanged(secondInput)
            viewModel.onSubmitClicked()
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(EchoUiState.Loading, awaitItem())
            assertEquals(EchoUiState.Error("Error"), awaitItem())
        }
    }
}
