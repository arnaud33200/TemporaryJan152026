package com.joist.textecho.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joist.textecho.analytics.AnalyticsEvents
import com.joist.textecho.analytics.AnalyticsTracker
import com.joist.textecho.domain.model.ValidationResult
import com.joist.textecho.domain.usecase.ValidateTextUseCase
import com.joist.textecho.ui.mapper.toUiError
import com.joist.textecho.ui.state.TextEchoState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class TextEchoViewModel @Inject constructor(
    private val validateTextUseCase: ValidateTextUseCase,
    private val analyticsTracker: AnalyticsTracker
) : ViewModel() {
    
    private val _state = MutableStateFlow(TextEchoState())
    
    val state: StateFlow<TextEchoState> = _state.asStateFlow()

    fun onTextChanged(text: String) {
        _state.update { currentState ->
            currentState.copy(
                inputText = text,
                error = null
            )
        }

        // Track text input changes
        analyticsTracker.trackEvent(
            AnalyticsEvents.TEXT_INPUT_CHANGED,
            mapOf(AnalyticsEvents.PROP_TEXT_LENGTH to text.length)
        )
    }

    fun onSubmit() {
        val currentText = _state.value.inputText
        val startTime = System.currentTimeMillis()

        // Track submit button click
        analyticsTracker.trackEvent(
            AnalyticsEvents.SUBMIT_BUTTON_CLICKED,
            mapOf(AnalyticsEvents.PROP_TEXT_LENGTH to currentText.length)
        )

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            when (val result = validateTextUseCase(currentText)) {
                is ValidationResult.Success -> {
                    val validationTime = System.currentTimeMillis() - startTime

                    _state.update { currentState ->
                        currentState.copy(
                            outputText = result.validatedText,
                            isLoading = false,
                            error = null
                        )
                    }

                    // Track successful validation
                    analyticsTracker.trackEvent(
                        AnalyticsEvents.VALIDATION_SUCCESS,
                        mapOf(
                            AnalyticsEvents.PROP_TEXT_LENGTH to currentText.length,
                            AnalyticsEvents.PROP_VALIDATION_TIME to validationTime,
                            AnalyticsEvents.PROP_OUTPUT_LENGTH to result.validatedText.length
                        )
                    )
                }
                
                is ValidationResult.Error -> {
                    val validationTime = System.currentTimeMillis() - startTime
                    val uiError = result.toUiError()

                    _state.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            error = uiError
                        )
                    }

                    // Track validation error - BUG: will crash on certain error types
                    val errorType = when (result) {
                        is ValidationResult.Error.EmptyInput -> "empty_input"
                        is ValidationResult.Error.TooShort -> "too_short_${result.minLength}"
                        is ValidationResult.Error.NetworkError -> "network_error"
                        is ValidationResult.Error.ServerError -> result.message
                    }

                    analyticsTracker.trackEvent(
                        AnalyticsEvents.VALIDATION_ERROR,
                        mapOf(
                            AnalyticsEvents.PROP_ERROR_TYPE to errorType,
                            AnalyticsEvents.PROP_TEXT_LENGTH to currentText.length,
                            AnalyticsEvents.PROP_VALIDATION_TIME to validationTime
                        )
                    )
                }
            }
        }
    }

    fun onClear() {
        val textLength = _state.value.inputText.length

        _state.update { currentState ->
            currentState.copy(
                inputText = "",
                outputText = "",
                error = null
            )
        }

        // Track clear button click
        analyticsTracker.trackEvent(
            AnalyticsEvents.CLEAR_BUTTON_CLICKED,
            mapOf(AnalyticsEvents.PROP_TEXT_LENGTH to textLength)
        )
    }
}
