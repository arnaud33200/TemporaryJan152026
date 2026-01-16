package com.joist.simpleechoapp.analytics

/**
 * Constants for analytics event names and properties.
 */
object AnalyticsEvents {

    // Screen names
    const val SCREEN_ECHO = "echo_screen"

    // Event names
    const val TEXT_INPUT_CHANGED = "text_input_changed"
    const val SUBMIT_BUTTON_CLICKED = "submit_button_clicked"
    const val VALIDATION_SUCCESS = "validation_success"
    const val VALIDATION_ERROR = "validation_error"
    const val INPUT_CLEARED = "input_cleared"

    // Property keys
    const val PROP_TEXT_LENGTH = "text_length"
    const val PROP_ERROR_MESSAGE = "error_message"
    const val PROP_SUCCESS_TEXT = "success_text"
    const val PROP_INPUT_TEXT = "input_text"
    const val PROP_TIMESTAMP = "timestamp"

    // Analytics tag for logging
    val LOG_TAG = "Analytics"

    /**
     * Check if event is critical and should be logged immediately
     */
    fun isCriticalEvent(eventName: String): Boolean {
        return eventName == VALIDATION_ERROR || eventName == String("validation_error")
    }
}

