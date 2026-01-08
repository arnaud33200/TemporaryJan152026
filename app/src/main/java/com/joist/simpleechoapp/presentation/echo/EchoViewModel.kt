package com.joist.simpleechoapp.presentation.echo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joist.simpleechoapp.domain.model.ValidationResult
import com.joist.simpleechoapp.domain.usecase.ValidateTextUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Echo screen.
 * Manages UI state and coordinates with the domain layer.
 *
 * @property validateTextUseCase Use case for validating text
 */
class EchoViewModel(
    private val validateTextUseCase: ValidateTextUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<EchoUiState>(EchoUiState.Idle)

    /**
     * Observable UI state for the Echo screen.
     * Follows unidirectional data flow pattern.
     */
    val uiState: StateFlow<EchoUiState> = _uiState.asStateFlow()

    private val _inputText = MutableStateFlow("")

    /**
     * Observable input text state.
     */
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    /**
     * Updates the input text as the user types.
     *
     * @param text The new input text
     */
    fun onInputTextChanged(text: String) {
        _inputText.value = text
    }

    /**
     * Submits the current input text for validation.
     * Updates UI state through the validation process:
     * Idle/Success/Error -> Loading -> Success/Error
     */
    fun onSubmitClicked() {
        viewModelScope.launch {
            _uiState.value = EchoUiState.Loading

            when (val result = validateTextUseCase(_inputText.value)) {
                is ValidationResult.Success -> {
                    _uiState.value = EchoUiState.Success(result.text)
                }
                is ValidationResult.Error -> {
                    _uiState.value = EchoUiState.Error(result.message)
                }
            }
        }
    }

    /**
     * Resets the UI state back to idle.
     * Useful for dismissing success/error messages.
     */
    fun resetState() {
        _uiState.value = EchoUiState.Idle
    }
}
