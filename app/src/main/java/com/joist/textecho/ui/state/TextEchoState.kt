package com.joist.textecho.ui.state

data class TextEchoState(
    val inputText: String = "",
    val outputText: String = "",
    val isLoading: Boolean = false,
    val error: UiError? = null
) {
    sealed class UiError {
        data object EmptyInput : UiError()
        data class TooShort(val minLength: Int) : UiError()
        data object NetworkError : UiError()
        data class ServerError(val message: String) : UiError()
        data class UnknownError(val message: String) : UiError()
    }
}
