package com.joist.textecho.domain.model


sealed class ValidationResult {
    data class Success(val validatedText: String) : ValidationResult()

    sealed class Error : ValidationResult() {
        data object EmptyInput : Error()

        data class TooShort(val minLength: Int) : Error()

        data object NetworkError : Error()

        data class ServerError(val message: String) : Error()

        data class Unknown(val throwable: Throwable) : Error()
    }
}
