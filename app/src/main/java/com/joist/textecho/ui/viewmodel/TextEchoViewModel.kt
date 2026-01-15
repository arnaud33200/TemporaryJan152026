package com.joist.textecho.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val validateTextUseCase: ValidateTextUseCase
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
    }

    fun onSubmit() {
        val currentText = _state.value.inputText
        
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            when (val result = validateTextUseCase(currentText)) {
                is ValidationResult.Success -> {
                    _state.update { currentState ->
                        currentState.copy(
                            outputText = result.validatedText,
                            isLoading = false,
                            error = null
                        )
                    }
                }
                
                is ValidationResult.Error -> {
                    _state.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            error = result.toUiError()
                        )
                    }
                }
            }
        }
    }

    fun onClear() {
        _state.update { currentState ->
            currentState.copy(
                inputText = "",
                outputText = "",
                error = null
            )
        }
    }
}
