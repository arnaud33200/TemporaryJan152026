package com.joist.textecho.ui.mapper

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.joist.textecho.R
import com.joist.textecho.domain.model.ValidationResult
import com.joist.textecho.ui.state.TextEchoState

fun ValidationResult.Error.toUiError(): TextEchoState.UiError {
    return when (this) {
        is ValidationResult.Error.EmptyInput ->
            TextEchoState.UiError.EmptyInput

        is ValidationResult.Error.TooShort ->
            TextEchoState.UiError.TooShort(minLength)

        is ValidationResult.Error.NetworkError ->
            TextEchoState.UiError.NetworkError

        is ValidationResult.Error.ServerError ->
            TextEchoState.UiError.ServerError(message)

        is ValidationResult.Error.Unknown ->
            TextEchoState.UiError.UnknownError(
                throwable.message.orEmpty()
            )
    }
}

@Composable
fun TextEchoState.UiError.toErrorMessage(): String {
    return when (this) {
        is TextEchoState.UiError.EmptyInput ->
            stringResource(R.string.please_enter_some_text)

        is TextEchoState.UiError.TooShort ->
            stringResource(R.string.text_must_be_at_least_characters_long, minLength)

        is TextEchoState.UiError.NetworkError ->
            stringResource(R.string.network_error_please_check_your_connection_and_try_again)

        is TextEchoState.UiError.ServerError ->
            stringResource(R.string.validation_failed, message)

        is TextEchoState.UiError.UnknownError ->
            stringResource(R.string.an_error_occurred, message)
    }
}
