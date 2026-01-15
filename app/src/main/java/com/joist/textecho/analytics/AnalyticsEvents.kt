package com.joist.textecho.analytics

object AnalyticsEvents {
    // Screen events
    const val SCREEN_TEXT_ECHO = "text_echo_screen"

    // User actions
    const val TEXT_INPUT_CHANGED = "text_input_changed"
    const val SUBMIT_BUTTON_CLICKED = "submit_clicked"
    const val CLEAR_BUTTON_CLICKED = "clear_clicked"

    // Validation events
    const val VALIDATION_SUCCESS = "validation_success"
    const val VALIDATION_ERROR = "validation_error"

    // Property keys
    const val PROP_TEXT_LENGTH = "text_length"
    const val PROP_ERROR_TYPE = "error_type"
    const val PROP_VALIDATION_TIME = "validation_time_ms"
    const val PROP_OUTPUT_LENGTH = "output_length"
}

