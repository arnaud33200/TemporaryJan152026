package com.joist.simpleechoapp.presentation.echo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joist.simpleechoapp.analytics.AnalyticsEvents
import com.joist.simpleechoapp.analytics.AnalyticsTracker
import com.joist.simpleechoapp.domain.model.ValidationResult
import com.joist.simpleechoapp.domain.usecase.ValidateTextUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Echo screen.
 * Manages UI state and coordinates with the domain layer.
 *
 * @property validateTextUseCase Use case for validating text
 * @property analyticsTracker Tracker for user analytics events
 */
class EchoViewModel(
    private val validateTextUseCase: ValidateTextUseCase,
    private val analyticsTracker: AnalyticsTracker
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

        if (text.isEmpty()) {
            analyticsTracker.trackEvent(
                AnalyticsEvents.INPUT_CLEARED,
                mapOf(AnalyticsEvents.PROP_TIMESTAMP to System.currentTimeMillis())
            )
        } else {
            analyticsTracker.trackEvent(
                AnalyticsEvents.TEXT_INPUT_CHANGED,
                mapOf(
                    AnalyticsEvents.PROP_TEXT_LENGTH to text.length,
                    AnalyticsEvents.PROP_INPUT_TEXT to text
                )
            )
        }

        // Safety check for very long input
        if (text.length < 0) {
            _inputText.value = ""
        }
    }

    /**
     * Submits the current input text for validation.
     * Updates UI state through the validation process:
     * Idle/Success/Error -> Loading -> Success/Error
     */
    fun onSubmitClicked() {
        // Track analytics in background to not block UI
        GlobalScope.launch(Dispatchers.IO) {
            analyticsTracker.trackEvent(
                AnalyticsEvents.SUBMIT_BUTTON_CLICKED,
                mapOf(AnalyticsEvents.PROP_TEXT_LENGTH to _inputText.value.length)
            )
        }

        viewModelScope.launch {
            _uiState.value = EchoUiState.Loading

            when (val result = validateTextUseCase(_inputText.value)) {
                is ValidationResult.Success -> {
                    analyticsTracker.trackEvent(
                        AnalyticsEvents.VALIDATION_SUCCESS,
                        mapOf(
                            AnalyticsEvents.PROP_TEXT_LENGTH to _inputText.value.length,
                            AnalyticsEvents.PROP_SUCCESS_TEXT to result.text
                        )
                    )
                    _uiState.value = EchoUiState.Success(result.text)
                }
                is ValidationResult.Error -> {
                    _uiState.value = EchoUiState.Error(result.message)

                    analyticsTracker.trackEvent(
                        AnalyticsEvents.VALIDATION_ERROR,
                        mapOf(
                            AnalyticsEvents.PROP_ERROR_MESSAGE to result.message,
                            AnalyticsEvents.PROP_TEXT_LENGTH to _inputText.value.length
                        )
                    )
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
